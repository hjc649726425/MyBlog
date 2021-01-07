package com.hjc.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.hjc.blog.constant.WebConst;
import com.hjc.blog.dao.LogVoMapper;
import com.hjc.common.exception.TipException;
import com.hjc.blog.service.ILogService;
import com.hjc.blog.model.Vo.LogVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class LogServiceImpl implements ILogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogServiceImpl.class);

    @Autowired
    private LogVoMapper logDao;

    @Transactional(rollbackFor = TipException.class)
    @Override
    public void insertLog(LogVo logVo) {
        logDao.insert(logVo);
    }

    @Transactional(rollbackFor = TipException.class)
    @Override
    public void insertLog(String action, String data, String ip, Integer authorId) {
        LogVo logs = new LogVo();
        logs.setAction(action);
        logs.setReqData(data);
        logs.setIp(ip);
        logDao.insert(logs);
    }

    @Override
    public List<LogVo> getLogs(int page, int limit) {
        LOGGER.debug("Enter getLogs method:page={},linit={}",page,limit);
        if (page <= 0) {
            page = 1;
        }
        if (limit < 1 || limit > WebConst.MAX_POSTS) {
            limit = 10;
        }

        QueryWrapper<LogVo> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");

        PageHelper.startPage((page - 1) * limit, limit);
        List<LogVo> logVos = logDao.selectList(wrapper);
        LOGGER.debug("Exit getLogs method");
        return logVos;
    }
}
