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
@TableName(value = "t_comments")
public class CommentVo extends BaseVo implements Serializable {
    /**
     * comment表主键
     */
    @TableId(value = "coid",type = IdType.AUTO)//指定自增策略
    private Integer coid;

    /**
     * post表主键,关联字段
     */
    private Integer cid;

    /**
     * 评论作者
     */
    private String author;

    /**
     * 评论所属用户id
     */
    private Integer authorId;

    /**
     * 评论所属内容作者id
     */
    private Integer ownerId;

    /**
     * 评论者邮件
     */
    private String mail;

    /**
     * 评论者网址
     */
    private String url;

    /**
     * 评论者ip地址
     */
    private String ip;

    /**
     * 评论者客户端
     */
    private String agent;

    /**
     * 评论类型
     */
    private String type;

    /**
     * 评论状态
     */
    private String status;

    /**
     * 父级评论
     */
    private Integer parent;

    /**
     * 评论内容
     */
    private String content;

    private static final long serialVersionUID = 1L;

}