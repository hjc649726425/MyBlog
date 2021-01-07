package com.hjc.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hjc.blog.dao.RelationshipVoMapper;
import com.hjc.common.exception.TipException;
import com.hjc.blog.model.Vo.RelationshipVoKey;
import com.hjc.blog.service.IRelationshipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RelationshipServiceImpl implements IRelationshipService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationshipServiceImpl.class);

    @Autowired
    private RelationshipVoMapper relationshipVoMapper;

    @Transactional(rollbackFor = TipException.class)
    @Override
    public void deleteById(Integer cid, Integer mid) {
        QueryWrapper<RelationshipVoKey> wrapper = new QueryWrapper<>();

        if (cid != null) {
            wrapper.eq("cid", cid);
        }
        if (mid != null) {
            wrapper.eq("mid", mid);
        }
        relationshipVoMapper.delete(wrapper);
    }

    @Override
    public List<RelationshipVoKey> getRelationshipById(Integer cid, Integer mid) {
        QueryWrapper<RelationshipVoKey> wrapper = new QueryWrapper<>();

        if (cid != null) {
            wrapper.eq("cid", cid);
        }
        if (mid != null) {
            wrapper.eq("mid", mid);
        }
        return relationshipVoMapper.selectList(wrapper);
    }

    @Transactional(rollbackFor = TipException.class)
    @Override
    public void insertVo(RelationshipVoKey relationshipVoKey) {
        relationshipVoMapper.insert(relationshipVoKey);
    }

    @Override
    public Long countById(Integer cid, Integer mid) {
        LOGGER.debug("Enter countById method:cid={},mid={}",cid,mid);
        QueryWrapper<RelationshipVoKey> wrapper = new QueryWrapper<>();

        if (cid != null) {
            wrapper.eq("cid", cid);
        }
        if (mid != null) {
            wrapper.eq("mid", mid);
        }
        long num = relationshipVoMapper.selectCount(wrapper);
        LOGGER.debug("Exit countById method return num={}",num);
        return num;
    }
}
