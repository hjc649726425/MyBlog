package com.hjc.blog.model.Vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 
 */
@Data
@TableName(value = "t_users")
public class UserVo extends BaseVo implements Serializable {
    /**
     * user表主键
     */
    @TableId(value = "uid",type = IdType.AUTO)//指定自增策略
    private Integer uid;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户的邮箱
     */
    private String email;

    /**
     * 用户的主页
     */
    private String homeUrl;

    /**
     * 用户显示的名称
     */
    private String screenName;

    /**
     * 最后活动时间
     */
    private Integer activated;

    /**
     * 上次登录最后活跃时间
     */
    private Integer logged;

    /**
     * 用户组
     */
    private String groupName;

    private static final long serialVersionUID = 1L;

}