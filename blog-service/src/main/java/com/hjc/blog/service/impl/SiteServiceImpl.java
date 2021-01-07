package com.hjc.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.hjc.blog.constant.WebConst;
import com.hjc.blog.controller.admin.AttachController;
import com.hjc.blog.dao.AttachVoMapper;
import com.hjc.blog.dao.CommentVoMapper;
import com.hjc.blog.dao.ContentVoMapper;
import com.hjc.blog.dao.MetaVoMapper;
import com.hjc.blog.dto.MetaDto;
import com.hjc.blog.dto.Types;
import com.hjc.common.exception.TipException;
import com.hjc.blog.model.Bo.ArchiveBo;
import com.hjc.blog.model.Bo.BackResponseBo;
import com.hjc.blog.model.Bo.StatisticsBo;
import com.hjc.blog.model.Vo.MetaVo;
import com.hjc.common.utils.DateKit;
import com.hjc.blog.utils.TaleUtils;
import com.hjc.common.utils.ZipUtils;
import com.hjc.blog.utils.backup.Backup;
import com.hjc.blog.model.Vo.CommentVo;
import com.hjc.blog.model.Vo.ContentVo;
import com.hjc.blog.service.ISiteService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.*;

@Service
public class SiteServiceImpl implements ISiteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiteServiceImpl.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    private CommentVoMapper commentDao;

    @Autowired
    private ContentVoMapper contentDao;

    @Autowired
    private AttachVoMapper attachDao;

    @Autowired
    private MetaVoMapper metaDao;

    @Override
    public List<CommentVo> recentComments(int limit) {
        LOGGER.debug("Enter recentComments method:limit={}", limit);
        if (limit < 0 || limit > 10) {
            limit = 10;
        }

        QueryWrapper<CommentVo> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        PageHelper.startPage(1, limit);
        List<CommentVo> byPage = commentDao.selectList(wrapper);
        LOGGER.debug("Exit recentComments method");
        return byPage;
    }

    @Override
    public List<ContentVo> recentContents(int limit) {
        LOGGER.debug("Enter recentContents method");
        if (limit < 0 || limit > 10) {
            limit = 10;
        }

        QueryWrapper<ContentVo> wrapper = new QueryWrapper<>();
        wrapper.eq("type", Types.ARTICLE.getType())
                .eq("status", Types.PUBLISH.getType())
                .orderByDesc("create_time");
        PageHelper.startPage(1, limit);
        List<ContentVo> list = contentDao.selectList(wrapper);
        LOGGER.debug("Exit recentContents method");
        return list;
    }

    @Override
    public BackResponseBo backup(String bk_type, String bk_path, String fmt) throws Exception {
        BackResponseBo backResponse = new BackResponseBo();
        if (bk_type.equals("attach")) {
            if (StringUtils.isBlank(bk_path)) {
                throw new TipException("请输入备份文件存储路径");
            }
            if (!(new File(bk_path)).isDirectory()) {
                throw new TipException("请输入一个存在的目录");
            }
            String bkAttachDir = AttachController.CLASSPATH + "upload";
            String bkThemesDir = AttachController.CLASSPATH + "templates/themes";

            String fname = DateKit.dateFormat(new Date(), fmt) + "_" + TaleUtils.getRandomNumber(5) + ".zip";

            String attachPath = bk_path + "/" + "attachs_" + fname;
            String themesPath = bk_path + "/" + "themes_" + fname;

            ZipUtils.zipFolder(bkAttachDir, attachPath);
            ZipUtils.zipFolder(bkThemesDir, themesPath);

            backResponse.setAttachPath(attachPath);
            backResponse.setThemePath(themesPath);
        }
        if (bk_type.equals("db")) {

            String bkAttachDir = AttachController.CLASSPATH + "upload/";
            if (!(new File(bkAttachDir)).isDirectory()) {
                File file = new File(bkAttachDir);
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
            String sqlFileName = "tale_" + DateKit.dateFormat(new Date(), fmt) + "_" + TaleUtils.getRandomNumber(5) + ".sql";
            String zipFile = sqlFileName.replace(".sql", ".zip");

            Backup backup = new Backup(dataSource.getConnection());
            String sqlContent = backup.execute();

            File sqlFile = new File(bkAttachDir + sqlFileName);
            write(sqlContent, sqlFile, Charset.forName("UTF-8"));

            String zip = bkAttachDir + zipFile;
            ZipUtils.zipFile(sqlFile.getPath(), zip);

            if (!sqlFile.exists()) {
                throw new TipException("数据库备份失败");
            }
            sqlFile.delete();

            backResponse.setSqlPath(zipFile);

            // 10秒后删除备份文件
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    new File(zip).delete();
                }
            }, 10 * 1000);
        }
        return backResponse;
    }

    @Override
    public CommentVo getComment(Integer coid) {
        if (null != coid) {
            return commentDao.selectById(coid);
        }
        return null;
    }

    @Override
    public StatisticsBo getStatistics() {
        LOGGER.debug("Enter getStatistics method");
        StatisticsBo statistics = new StatisticsBo();

        QueryWrapper<ContentVo> wrapper = new QueryWrapper<>();
        wrapper.eq("type", Types.ARTICLE.getType()).eq("status", Types.PUBLISH.getType());

        Long articles =   contentDao.selectCount(wrapper).longValue();

        Long comments = commentDao.selectCount(new QueryWrapper<>()).longValue();

        Long attachs = attachDao.selectCount(new QueryWrapper<>()).longValue();

        QueryWrapper<MetaVo> wrapperMeta = new QueryWrapper<>();
        wrapperMeta.eq("type", Types.LINK.getType());

        Long links = metaDao.selectCount(wrapperMeta).longValue();

        statistics.setArticles(articles);
        statistics.setComments(comments);
        statistics.setAttachs(attachs);
        statistics.setLinks(links);
        LOGGER.debug("Exit getStatistics method");
        return statistics;
    }

    @Override
    public List<ArchiveBo> getArchives() {
        LOGGER.debug("Enter getArchives method");
        List<ArchiveBo> archives = contentDao.findReturnArchiveBo();
        if (null != archives) {
            archives.forEach(archive -> {
                QueryWrapper<ContentVo> wrapper = new QueryWrapper<>();

                String date = archive.getDate();
                Date sd = null;
                try {
                    sd = DateKit.dateFormat(date, "yyyy-MM-dd HH:mm:ss");
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                int start = DateKit.getUnixTimeByDate(sd);
                int end = DateKit.getUnixTimeByDate(DateKit.dateAdd(DateKit.INTERVAL_MONTH, sd, 1)) - 1;

                wrapper.eq("type", Types.LINK.getType())
                        .eq("status", Types.PUBLISH.getType())
                        .ge("create_time", start)
                        .le("create_time", end)
                        .orderByDesc("create_time");

                List<ContentVo> contentss = contentDao.selectList(wrapper);
                archive.setArticles(contentss);
            });
        }
        LOGGER.debug("Exit getArchives method");
        return archives;
    }

    @Override
    public List<MetaDto> metas(String type, String orderBy, int limit){
        LOGGER.debug("Enter metas method:type={},order={},limit={}", type, orderBy, limit);
        List<MetaDto> retList=null;
        if (StringUtils.isNotBlank(type)) {
            if(StringUtils.isBlank(orderBy)){
                orderBy = "count desc, a.mid desc";
            }
            if(limit < 1 || limit > WebConst.MAX_POSTS){
                limit = 10;
            }
            Map<String, Object> paraMap = new HashMap<>();
            paraMap.put("type", type);
            paraMap.put("order", orderBy);
            paraMap.put("limit", limit);
            retList= metaDao.selectFromSql(paraMap);
        }
        LOGGER.debug("Exit metas method");
        return retList;
    }


    private void write(String data, File file, Charset charset) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(data.getBytes(charset));
        } catch (IOException var8) {
            throw new IllegalStateException(var8);
        } finally {
            if(null != os) {
                try {
                    os.close();
                } catch (IOException var2) {
                    var2.printStackTrace();
                }
            }
        }

    }

}
