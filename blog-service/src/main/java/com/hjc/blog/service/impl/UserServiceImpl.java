package com.hjc.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hjc.blog.dao.UserVoMapper;
import com.hjc.common.exception.TipException;
import com.hjc.blog.model.Vo.UserVo;
import com.hjc.blog.utils.TaleUtils;
import com.hjc.blog.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserVoMapper userDao;

    @Transactional(rollbackFor = TipException.class)
    @Override
    public Integer insertUser(UserVo userVo) {
        Integer uid = null;
        if (StringUtils.isNotBlank(userVo.getUsername()) && StringUtils.isNotBlank(userVo.getEmail())) {
//            用户密码加密
            String encodePwd = TaleUtils.MD5encode(userVo.getUsername() + userVo.getPassword());
            userVo.setPassword(encodePwd);
            userDao.insert(userVo);
        }
        return userVo.getUid();
    }

    @Override
    public UserVo queryUserById(Integer uid) {
        UserVo userVo = null;
        if (uid != null) {
            userVo = userDao.selectById(uid);
        }
        return userVo;
    }

    @Override
    public UserVo login(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new TipException("用户名和密码不能为空");
        }

        QueryWrapper<UserVo> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);

        long count = userDao.selectCount(wrapper);
        if (count < 1) {
            throw new TipException("不存在该用户");
        }
        String pwd = TaleUtils.MD5encode(username + password);

        wrapper.eq("password", pwd);
        List<UserVo> userVos = userDao.selectList(wrapper);
        if (userVos.size() != 1) {
            throw new TipException("用户名或密码错误");
        }
        return userVos.get(0);
    }

    @Transactional(rollbackFor = TipException.class)
    @Override
    public Boolean register(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new TipException("用户名和密码不能为空");
        }

        QueryWrapper<UserVo> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);

        long count = userDao.selectCount(wrapper);
        if (count >= 1) {
            throw new TipException("已存在该用户");
        }
        String pwd = TaleUtils.MD5encode(username + password);

        wrapper.eq("password", pwd);
        UserVo user = new UserVo();
        user.setUsername(username);
        user.setPassword(pwd);
        user.setGroupName("visitor");
        userDao.insert(user);

        return true;
    }

    @Transactional(rollbackFor = TipException.class)
    @Override
    public void updateByUid(UserVo userVo) {
        if (null == userVo || null == userVo.getUid()) {
            throw new TipException("userVo is null");
        }
        int i = userDao.updateById(userVo);
        if (i != 1) {
            throw new TipException("update user by uid and retrun is not one");
        }
    }
}
