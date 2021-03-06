package com.hjc.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hjc.blog.dao.OptionVoMapper;
import com.hjc.common.exception.TipException;
import com.hjc.blog.model.Vo.OptionVo;
import com.hjc.blog.service.IOptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

/**
 * options表的service
 */
@Service
public class OptionServiceImpl implements IOptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptionServiceImpl.class);

    @Autowired
    private OptionVoMapper optionDao;

    @Transactional(rollbackFor = TipException.class)
    @Override
    public void insertOption(OptionVo optionVo) {
        LOGGER.debug("Enter insertOption method:optionVo={}", optionVo);
        optionDao.insert(optionVo);
        LOGGER.debug("Exit insertOption method.");
    }

    @Transactional(rollbackFor = TipException.class)
    @Override
    public void insertOption(String name, String value) {
        LOGGER.debug("Enter insertOption method:name={},value={}", name, value);
        OptionVo optionVo = new OptionVo();
        optionVo.setName(name);
        optionVo.setValue(value);
        if (optionDao.selectById(name) == null) {
            optionDao.insert(optionVo);
        } else {
            optionDao.updateById(optionVo);
        }
        LOGGER.debug("Exit insertOption method.");
    }

    @Transactional(rollbackFor = TipException.class)
    @Override
    public void saveOptions(Map<String, String> options) {
        if (null != options && !options.isEmpty()) {
            options.forEach(this::insertOption);
        }
    }

    @Override
    public OptionVo getOptionByName(String name) {
        return optionDao.selectById(name);
    }

    @Override
    public List<OptionVo> getOptions() {
        return optionDao.selectList(new QueryWrapper<>());
    }
}
