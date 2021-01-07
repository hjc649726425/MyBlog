package com.hjc.blog.model.Bo;

import com.hjc.blog.model.Vo.CommentVo;
import lombok.Data;

import java.util.List;

/**
 * 返回页面的评论，包含父子评论内容
 */
@Data
public class CommentBo extends CommentVo {

    private int levels;
    private List<CommentVo> children;

    public CommentBo(CommentVo comments) {
        setAuthor(comments.getAuthor());
        setMail(comments.getMail());
        setCoid(comments.getCoid());
        setAuthorId(comments.getAuthorId());
        setUrl(comments.getUrl());
        setAgent(comments.getAgent());
        setIp(comments.getIp());
        setContent(comments.getContent());
        setOwnerId(comments.getOwnerId());
        setCid(comments.getCid());
    }

}
