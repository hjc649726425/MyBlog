package com.hjc.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hjc.blog.constant.WebConst;
import com.hjc.blog.dao.MetaVoMapper;
import com.hjc.blog.dto.MetaDto;
import com.hjc.blog.dto.Types;
import com.hjc.common.exception.TipException;
import com.hjc.blog.model.Vo.MetaVo;
import com.hjc.blog.model.Vo.RelationshipVoKey;
import com.hjc.blog.service.IContentService;
import com.hjc.blog.service.IMetaService;
import com.hjc.blog.model.Vo.ContentVo;
import com.hjc.blog.service.IRelationshipService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class MetaServiceImpl implements IMetaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaServiceImpl.class);

    @Autowired
    private MetaVoMapper metaDao;

    @Autowired
    private IRelationshipService relationshipService;

    @Autowired
    private IContentService contentService;

    @Autowired
    RedissonClient redisson;

    @Override
    public MetaDto getMeta(String type, String name) {
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(name)) {
            return metaDao.selectDtoByNameAndType(name, type);
        }
        return null;
    }

    @Override
    public Integer countMeta(Integer mid) {
        return metaDao.countWithSql(mid);
    }

    @Override
    public List<MetaVo> getMetas(String types) {
        if (StringUtils.isNotBlank(types)) {

            QueryWrapper<MetaVo> wrapper = new QueryWrapper<>();
            wrapper.eq("type", types)
                    .orderByDesc("sort")
                    .orderByDesc("mid");

            return metaDao.selectList(wrapper);
        }
        return null;
    }

    @Override
    @Cacheable(value = "metas", key = "#type", sync = true) //sync设置同步，可以防止击穿
    public List<MetaDto> getMetaList(String type, String orderby, int limit) {
        if (StringUtils.isNotBlank(type)) {
            if (StringUtils.isBlank(orderby)) {
                orderby = "count desc, a.mid desc";
            }
            if (limit < 1 || limit > WebConst.MAX_POSTS) {
                limit = 10;
            }
            Map<String, Object> paraMap = new HashMap<>();
            paraMap.put("type", type);
            paraMap.put("order", orderby);
            paraMap.put("limit", limit);
            return metaDao.selectFromSql(paraMap);
        }
        return null;
    }

    @Transactional(rollbackFor = TipException.class)
    @Override
    @CacheEvict(value = "metas", allEntries=true, beforeInvocation = false)
    public void delete(int mid) {
        MetaVo metas = metaDao.selectById(mid);
        if (null != metas) {
            String type = metas.getType();
            String name = metas.getName();

            metaDao.deleteById(mid);

            List<RelationshipVoKey> rlist = relationshipService.getRelationshipById(null, mid);
            if (null != rlist) {
                for (RelationshipVoKey r : rlist) {
                    ContentVo contents = contentService.getContents(String.valueOf(r.getCid()));
                    if (null != contents) {
                        ContentVo temp = new ContentVo();
                        temp.setCid(r.getCid());
                        if (type.equals(Types.CATEGORY.getType())) {
                            temp.setCategories(reMeta(name, contents.getCategories()));
                        }
                        if (type.equals(Types.TAG.getType())) {
                            temp.setTags(reMeta(name, contents.getTags()));
                        }
                        contentService.updateContentByCid(temp);
                    }
                }
            }
            relationshipService.deleteById(null, mid);
        }
    }

    @Transactional(rollbackFor = TipException.class)
    @Override
    @CacheEvict(value = "metas", allEntries=true, beforeInvocation = false)
    public void saveMeta(String type, String name, Integer mid) {
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(name)) {

            QueryWrapper<MetaVo> wrapper = new QueryWrapper<>();
            wrapper.eq("type", type).eq("name", name);

            List<MetaVo> metaVos = metaDao.selectList(wrapper);
            MetaVo metas;
            if (metaVos.size() != 0) {
                throw new TipException("已经存在该项");
            } else {
                metas = new MetaVo();
                metas.setName(name);
                if (null != mid) {
                    MetaVo original = metaDao.selectById(mid);
                    metas.setMid(mid);
                    metaDao.updateById(metas);
//                    更新原有文章的categories
                    contentService.updateCategory(original.getName(),name);
                } else {
                    metas.setType(type);
                    metaDao.insert(metas);
                }
            }
        }
    }

    @Transactional(rollbackFor = TipException.class)
    @Override
    @CacheEvict(value = "metas", allEntries=true, beforeInvocation = false)
    public void saveMetas(Integer cid, String names, String type) {
        if (null == cid) {
            throw new TipException("项目关联id不能为空");
        }
        if (StringUtils.isNotBlank(names) && StringUtils.isNotBlank(type)) {
            String[] nameArr = StringUtils.split(names, ",");
            for (String name : nameArr) {
                this.saveOrUpdate(cid, name, type);
            }
        }
    }

    private void saveOrUpdate(Integer cid, String name, String type) {

        QueryWrapper<MetaVo> wrapper = new QueryWrapper<>();
        wrapper.eq("type", type).eq("name", name);
        List<MetaVo> metaVos = metaDao.selectList(wrapper);

        int mid;
        MetaVo metas;
        if (metaVos.size() == 1) {
            metas = metaVos.get(0);
            mid = metas.getMid();
        } else if (metaVos.size() > 1) {
            throw new TipException("查询到多条数据");
        } else {
            metas = new MetaVo();
            metas.setSlug(name);
            metas.setName(name);
            metas.setType(type);
            metaDao.insert(metas);
            mid = metas.getMid();
        }
        if (mid != 0) {
            Long count = relationshipService.countById(cid, mid);
            if (count == 0) {
                RelationshipVoKey relationships = new RelationshipVoKey();
                relationships.setCid(cid);
                relationships.setMid(mid);
                relationshipService.insertVo(relationships);
            }
        }
    }


    private String reMeta(String name, String metas) {
        String[] ms = StringUtils.split(metas, ",");
        StringBuilder sbuf = new StringBuilder();
        for (String m : ms) {
            if (!name.equals(m)) {
                sbuf.append(",").append(m);
            }
        }
        if (sbuf.length() > 0) {
            return sbuf.substring(1);
        }
        return "";
    }

    @Transactional(rollbackFor = TipException.class)
    @Override
    @CacheEvict(value = "metas", allEntries=true, beforeInvocation = false)
    public void saveMeta(MetaVo metas) {
        if (null != metas) {
            metaDao.insert(metas);
        }
    }

    @Transactional(rollbackFor = TipException.class)
    @Override
    @CacheEvict(value = "metas", allEntries=true, beforeInvocation = false)
    public void update(MetaVo metas) {
        if (null != metas && null != metas.getMid()) {
            metaDao.updateById(metas);
        }
    }
}
