package com.hjc.blog.controller.admin;

import com.github.pagehelper.PageInfo;
import com.hjc.blog.annotation.ActionLog;
import com.hjc.common.exception.TipException;
import com.hjc.blog.model.Bo.RestResponseBo;
import com.hjc.blog.controller.BaseController;
import com.hjc.blog.model.Vo.CommentVo;
import com.hjc.blog.model.Vo.UserVo;
import com.hjc.blog.service.ICommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("admin/comments")
public class CommentController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private ICommentService commentsService;

    @GetMapping(value = "")
    @ActionLog(title = "Comment", action = "评论列表页")
    public String index(@RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "15") int limit, HttpServletRequest request) {
        UserVo users = this.user(request);

        PageInfo<CommentVo> commentsPaginator = commentsService.getCommentsWithPage(users, page, limit);
        request.setAttribute("comments", commentsPaginator);
        return "admin/comment_list";
    }

    /**
     * 删除一条评论
     *
     * @param coid
     * @return
     */
    @PostMapping(value = "delete")
    @ResponseBody
    @ActionLog(title = "Comment", action = "删除评论")
    public RestResponseBo delete(@RequestParam Integer coid) {
        try {
            CommentVo comments = commentsService.getCommentById(coid);
            if (null == comments) {
                return RestResponseBo.fail("不存在该评论");
            }
            commentsService.delete(coid, comments.getCid());
        } catch (Exception e) {
            String msg = "评论删除失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

    @PostMapping(value = "status")
    @ResponseBody
    @ActionLog(title = "Comment", action = "评论通过")
    public RestResponseBo status(@RequestParam Integer coid, @RequestParam String status) {
        try {
            CommentVo comments = commentsService.getCommentById(coid);
            if (comments != null) {
                comments.setCoid(coid);
                comments.setStatus(status);
                commentsService.update(comments);
            } else {
                return RestResponseBo.fail("操作失败");
            }
        } catch (Exception e) {
            String msg = "操作失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

}
