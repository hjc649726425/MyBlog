package com.hjc.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hjc.blog.constant.WebConst;
import com.hjc.blog.dao.ContentVoMapper;
import com.hjc.blog.dao.MetaVoMapper;
import com.hjc.blog.dto.Types;
import com.hjc.common.exception.TipException;
import com.hjc.blog.service.IContentService;
import com.hjc.blog.service.IMetaService;
import com.hjc.common.utils.DateKit;
import com.hjc.blog.utils.TaleUtils;
import com.hjc.common.utils.Tools;
import com.hjc.blog.model.Vo.ContentVo;
import com.hjc.blog.service.IRelationshipService;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContentServiceImpl implements IContentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentServiceImpl.class);

    @Autowired
    private ContentVoMapper contentDao;

    @Autowired
    private MetaVoMapper metaDao;

    @Autowired
    private IRelationshipService relationshipService;

    @Autowired
    private IMetaService metasService;

    @Transactional(rollbackFor = TipException.class)
    @Override
    public void publish(ContentVo contents) {
        if (null == contents) {
            throw new TipException("文章对象为空");
        }
        if (StringUtils.isBlank(contents.getTitle())) {
            throw new TipException("文章标题不能为空");
        }
        if (StringUtils.isBlank(contents.getContent())) {
            throw new TipException("文章内容不能为空");
        }
        int titleLength = contents.getTitle().length();
        if (titleLength > WebConst.MAX_TITLE_COUNT) {
            throw new TipException("文章标题过长");
        }
        int contentLength = contents.getContent().length();
        if (contentLength > WebConst.MAX_TEXT_COUNT) {
            throw new TipException("文章内容过长");
        }
        if (null == contents.getAuthorId()) {
            throw new TipException("请登录后发布文章");
        }
        if (StringUtils.isNotBlank(contents.getSlug())) {
            if (contents.getSlug().length() < 5) {
                throw new TipException("路径太短了");
            }
            if (!TaleUtils.isPath(contents.getSlug())) throw new TipException("您输入的路径不合法");

            QueryWrapper<ContentVo> wrapper = new QueryWrapper<>();
            wrapper.eq("type", contents.getType()).eq("status", contents.getSlug());
            long count = contentDao.selectCount(wrapper);
            if (count > 0) throw new TipException("该路径已经存在，请重新输入");
        } else {
            contents.setSlug(null);
        }

        contents.setContent(EmojiParser.parseToAliases(contents.getContent()));

        contents.setHits(0);
        contents.setCommentsNum(0);

        String tags = contents.getTags();
        String categories = contents.getCategories();
        contentDao.insert(contents);
        Integer cid = contents.getCid();

        metasService.saveMetas(cid, tags, Types.TAG.getType());
        metasService.saveMetas(cid, categories, Types.CATEGORY.getType());
    }

    @Override
    public PageInfo<ContentVo> getContents(Integer p, Integer limit) {
        LOGGER.debug("Enter getContents method");

        QueryWrapper<ContentVo> wrapper = new QueryWrapper<>();
        wrapper.eq("type", Types.ARTICLE.getType()).eq("status", Types.PUBLISH.getType()).
            orderByDesc("create_time");

        PageHelper.startPage(p, limit);
        List<ContentVo> data = contentDao.selectList(wrapper);
        PageInfo<ContentVo> pageInfo = new PageInfo<>(data);
        LOGGER.debug("Exit getContents method");
        return pageInfo;
    }

    @Override
    public ContentVo getContents(String id) {
        if (StringUtils.isNotBlank(id)) {
            if (Tools.isNumber(id)) {
                ContentVo contentVo = contentDao.selectById(Integer.valueOf(id));
                if (contentVo != null) {
                    contentVo.setHits(contentVo.getHits() + 1);
                    contentDao.updateById(contentVo);
                }
                return contentVo;
            } else {
                QueryWrapper<ContentVo> wrapper = new QueryWrapper<>();
                wrapper.eq("slug", id);

                List<ContentVo> contentVos = contentDao.selectList(wrapper);
                if (contentVos.size() != 1) {
                    throw new TipException("query content by id and return is not one");
                }
                return contentVos.get(0);
            }
        }
        return null;
    }

    @Transactional(rollbackFor = TipException.class)
    @Override
    public void updateContentByCid(ContentVo contentVo) {
        if (null != contentVo && null != contentVo.getCid()) {
            contentDao.updateById(contentVo);
        }
    }

    @Override
    public PageInfo<ContentVo> getArticles(Integer mid, int page, int limit) {
        int total = metaDao.countWithSql(mid);
        PageHelper.startPage(page, limit);
        List<ContentVo> list = contentDao.findByCatalog(mid);
        PageInfo<ContentVo> paginator = new PageInfo<>(list);
        paginator.setTotal(total);
        return paginator;
    }

    @Override
    public PageInfo<ContentVo> getArticles(String keyword, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);

        QueryWrapper<ContentVo> wrapper = new QueryWrapper<>();
        wrapper.eq("type", Types.ARTICLE.getType()).eq("status",Types.PUBLISH.getType())
            .like("title", "%" + keyword + "%")
            .orderByDesc("create_time");

        List<ContentVo> contentVos = contentDao.selectList(wrapper);
        return new PageInfo<>(contentVos);
    }

    @Override
    public PageInfo<ContentVo> getArticlesWithpage(QueryWrapper wrapper, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<ContentVo> contentVos = contentDao.selectList(wrapper);
        return new PageInfo<>(contentVos);
    }

    @Transactional(rollbackFor = TipException.class)
    @Override
    public void deleteByCid(Integer cid) {
        ContentVo contents = this.getContents(cid + "");
        if (null != contents) {
            contentDao.deleteById(cid);
            relationshipService.deleteById(cid, null);
        }
    }

    @Transactional(rollbackFor = TipException.class)
    @Override
    public void updateCategory(String ordinal, String newCatefory) {
        ContentVo contentVo = new ContentVo();
        contentVo.setCategories(newCatefory);

        QueryWrapper<ContentVo> wrapper = new QueryWrapper<>();
        wrapper.eq("categories", ordinal);
        contentDao.update(contentVo, wrapper);
    }

    @Transactional(rollbackFor = TipException.class)
    @Override
    public void updateArticle(ContentVo contents) {
        if (null == contents || null == contents.getCid()) {
            throw new TipException("文章对象不能为空");
        }
        if (StringUtils.isBlank(contents.getTitle())) {
            throw new TipException("文章标题不能为空");
        }
        if (StringUtils.isBlank(contents.getContent())) {
            throw new TipException("文章内容不能为空");
        }
        if (contents.getTitle().length() > 200) {
            throw new TipException("文章标题过长");
        }
        if (contents.getContent().length() > 65000) {
            throw new TipException("文章内容过长");
        }
        if (null == contents.getAuthorId()) {
            throw new TipException("请登录后发布文章");
        }
        if (StringUtils.isBlank(contents.getSlug())) {
            contents.setSlug(null);
        }
        int time = DateKit.getCurrentUnixTime();
        Integer cid = contents.getCid();
        contents.setContent(EmojiParser.parseToAliases(contents.getContent()));

        contentDao.updateById(contents);
        relationshipService.deleteById(cid, null);
        metasService.saveMetas(cid, contents.getTags(), Types.TAG.getType());
        metasService.saveMetas(cid, contents.getCategories(), Types.CATEGORY.getType());
    }
}
