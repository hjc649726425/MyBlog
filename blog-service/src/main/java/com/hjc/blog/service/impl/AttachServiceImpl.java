package com.hjc.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hjc.blog.dao.AttachVoMapper;
import com.hjc.common.exception.TipException;
import com.hjc.blog.model.Vo.AttachVo;
import com.hjc.blog.service.IAttachService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AttachServiceImpl implements IAttachService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttachServiceImpl.class);

    @Autowired
    private AttachVoMapper attachDao;

    @Override
    public PageInfo<AttachVo> getAttachs(Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        QueryWrapper<AttachVo> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        List<AttachVo> attachVos = attachDao.selectList(wrapper);
        return new PageInfo<>(attachVos);
    }

    @Override
    public AttachVo selectById(Integer id) {
        if(null != id){
            return attachDao.selectById(id);
        }
        return null;
    }

    @Transactional(rollbackFor = TipException.class)
    @Override
    public void save(String fname, String fkey, String ftype, Integer author) {
        AttachVo attach = new AttachVo();
        attach.setFname(fname);
        attach.setAuthorId(author);
        attach.setFkey(fkey);
        attach.setFtype(ftype);
        attachDao.insert(attach);
    }

    @Transactional(rollbackFor = TipException.class)
    @Override
    public void deleteById(Integer id) {
        if (null != id) {
            attachDao.deleteById(id);
        }
    }
}
