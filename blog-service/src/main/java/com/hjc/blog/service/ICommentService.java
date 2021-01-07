package com.hjc.blog.service;

import com.github.pagehelper.PageInfo;
import com.hjc.blog.model.Bo.CommentBo;
import com.hjc.blog.model.Vo.CommentVo;
import com.hjc.blog.model.Vo.UserVo;


public interface ICommentService {

    /**
     * 保存对象
     * @param commentVo
     */
    void insertComment(CommentVo commentVo);

    /**
     * 获取文章下的评论
     * @param cid
     * @param page
     * @param limit
     * @return CommentBo
     */
    PageInfo<CommentBo> getComments(Integer cid, int page, int limit);

    /**
     * 获取文章下的评论
     * @param users
     * @param page
     * @param limit
     * @return CommentVo
     */
    PageInfo<CommentVo> getCommentsWithPage(UserVo users, int page, int limit);


    /**
     * 根据主键查询评论
     * @param coid
     * @return
     */
    CommentVo getCommentById(Integer coid);


    /**
     * 删除评论，暂时没用
     * @param coid
     * @param cid
     * @throws Exception
     */
    void delete(Integer coid, Integer cid);

    /**
     * 更新评论状态
     * @param comments
     */
    void update(CommentVo comments);

}
