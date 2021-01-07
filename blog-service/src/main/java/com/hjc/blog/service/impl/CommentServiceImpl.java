package com.hjc.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hjc.blog.dao.CommentVoMapper;
import com.hjc.common.exception.TipException;
import com.hjc.blog.model.Bo.CommentBo;
import com.hjc.blog.model.Vo.UserVo;
import com.hjc.blog.service.IContentService;
import com.hjc.blog.utils.TaleUtils;
import com.hjc.blog.model.Vo.CommentVo;
import com.hjc.blog.model.Vo.ContentVo;
import com.hjc.blog.service.ICommentService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements ICommentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    private CommentVoMapper commentDao;

    @Autowired
    private IContentService contentService;

    @Transactional(rollbackFor = TipException.class)
    @Override
    public void insertComment(CommentVo comments) {
        if (null == comments) {
            throw new TipException("评论对象为空");
        }
        if (StringUtils.isBlank(comments.getAuthor())) {
            comments.setAuthor("热心网友");
        }
        if (StringUtils.isNotBlank(comments.getMail()) && !TaleUtils.isEmail(comments.getMail())) {
            throw new TipException("请输入正确的邮箱格式");
        }
        if (StringUtils.isBlank(comments.getContent())) {
            throw new TipException("评论内容不能为空");
        }
        if (comments.getContent().length() < 5 || comments.getContent().length() > 2000) {
            throw new TipException("评论字数在5-2000个字符");
        }
        if (null == comments.getCid()) {
            throw new TipException("评论文章不能为空");
        }
        ContentVo contents = contentService.getContents(String.valueOf(comments.getCid()));
        if (null == contents) {
            throw new TipException("不存在的文章");
        }
        comments.setOwnerId(contents.getAuthorId());
        comments.setAuthorId(contents.getAuthorId());
        comments.setStatus("not_audit");
        commentDao.insert(comments);

        ContentVo temp = new ContentVo();
        temp.setCid(contents.getCid());
        temp.setCommentsNum(contents.getCommentsNum() + 1);
        contentService.updateContentByCid(temp);
    }

    @Override
    public PageInfo<CommentBo> getComments(Integer cid, int page, int limit) {

        if (null != cid) {
            PageHelper.startPage(page, limit);

            QueryWrapper<CommentVo> wrapper = new QueryWrapper<>();
            wrapper.eq("cid", cid).eq("status","approved").
                    eq("parent",0).orderByDesc("coid");

            List<CommentVo> parents = commentDao.selectList(wrapper);
            PageInfo<CommentVo> commentPaginator = new PageInfo<>(parents);
            PageInfo<CommentBo> returnBo = copyPageInfo(commentPaginator);
            if (parents.size() != 0) {
                List<CommentBo> comments = new ArrayList<>(parents.size());
                parents.forEach(parent -> {
                    CommentBo comment = new CommentBo(parent);
                    comments.add(comment);
                });
                returnBo.setList(comments);
            }
            return returnBo;
        }
        return null;
    }

    @Override
    public PageInfo<CommentVo> getCommentsWithPage(UserVo users, int page, int limit) {
        PageHelper.startPage(page, limit);

        QueryWrapper<CommentVo> wrapper = new QueryWrapper<>();
        wrapper.eq("author_id", users.getUid()).
                eq("parent",0).orderByDesc("coid");

        List<CommentVo> commentVos = commentDao.selectList(wrapper);
        PageInfo<CommentVo> pageInfo = new PageInfo<>(commentVos);
        return pageInfo;
    }

    @Transactional(rollbackFor = TipException.class)
    @Override
    public void update(CommentVo comments) {
        if (null != comments && null != comments.getCoid()) {
            commentDao.updateById(comments);
        }
    }

    @Transactional(rollbackFor = TipException.class)
    @Override
    public void delete(Integer coid, Integer cid) {
        if (null == coid) {
            throw new TipException("主键为空");
        }
        commentDao.deleteById(coid);
        ContentVo contents = contentService.getContents(cid + "");
        if (null != contents && contents.getCommentsNum() > 0) {
            ContentVo temp = new ContentVo();
            temp.setCid(cid);
            temp.setCommentsNum(contents.getCommentsNum() - 1);
            contentService.updateContentByCid(temp);
        }
    }

    @Override
    public CommentVo getCommentById(Integer coid) {
        if (null != coid) {
            return commentDao.selectById(coid);
        }
        return null;
    }

    /**
     * copy原有的分页信息，除数据
     *
     * @param ordinal
     * @param <T>
     * @return
     */
    private <T> PageInfo<T> copyPageInfo(PageInfo ordinal) {
        PageInfo<T> returnBo = new PageInfo<T>();
        returnBo.setPageSize(ordinal.getPageSize());
        returnBo.setPageNum(ordinal.getPageNum());
        returnBo.setEndRow(ordinal.getEndRow());
        returnBo.setTotal(ordinal.getTotal());
        returnBo.setHasNextPage(ordinal.isHasNextPage());
        returnBo.setHasPreviousPage(ordinal.isHasPreviousPage());
        returnBo.setIsFirstPage(ordinal.isIsFirstPage());
        returnBo.setIsLastPage(ordinal.isIsLastPage());
        returnBo.setNavigateFirstPage(ordinal.getNavigateFirstPage());
        returnBo.setNavigateLastPage(ordinal.getNavigateLastPage());
        returnBo.setNavigatepageNums(ordinal.getNavigatepageNums());
        returnBo.setSize(ordinal.getSize());
        returnBo.setPrePage(ordinal.getPrePage());
        returnBo.setNextPage(ordinal.getNextPage());
        return returnBo;
    }
}
