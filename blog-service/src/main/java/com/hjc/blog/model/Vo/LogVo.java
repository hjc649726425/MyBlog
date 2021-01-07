package com.hjc.blog.model.Vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 
 */
@Data
@TableName(value = "t_logs")
public class LogVo extends BaseVo implements Serializable {
    /**
     * 日志主键
     */
    @TableId(value = "id",type = IdType.AUTO)//指定自增策略
    private Integer id;

    /**
     * 方法模块
     */
    private String title;

    /**
     * 产生的动作
     */
    private String action;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 请求
     */
    private String reqData;

    /**
     * 响应
     */
    private String respData;

    /**
     * 发生人id
     */
    private String userName;

    /**
     * 日志产生的ip
     */
    private String ip;

    /**
     * 日志产生的url
     */
    private String url;

    /**
     * 日志产生的url
     */
    private long processTime;



    private static final long serialVersionUID = 1L;

}