package com.hjc.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.hjc.blog.annotation.ActionLog;
import com.hjc.common.exception.TipException;
import com.hjc.blog.model.Bo.RestResponseBo;
import com.hjc.blog.constant.WebConst;
import com.hjc.blog.controller.BaseController;
import com.hjc.blog.dto.Types;
import com.hjc.blog.model.Vo.ContentVo;
import com.hjc.blog.model.Vo.UserVo;
import com.hjc.blog.service.IContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Controller()
@RequestMapping("admin/page")
public class PageController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageController.class);

    @Autowired
    private IContentService contentsService;

    @GetMapping(value = "")
    @ActionLog(title = "Page", action = "页面页")
    public String index(HttpServletRequest request) {

        QueryWrapper<ContentVo> wrapper = new QueryWrapper<>();
        wrapper.eq("type", Types.PAGE.getType())
                .orderByDesc("create_time");

        PageInfo<ContentVo> contentsPaginator = contentsService.getArticlesWithpage(wrapper, 1, WebConst.MAX_POSTS);
        request.setAttribute("articles", contentsPaginator);
        return "admin/page_list";
    }

    @GetMapping(value = "new")
    @ActionLog(title = "Page", action = "发布页面页")
    public String newPage(HttpServletRequest request) {
        return "admin/page_edit";
    }

    @GetMapping(value = "/{cid}")
    @ActionLog(title = "Page", action = "编辑页面页")
    public String editPage(@PathVariable String cid, HttpServletRequest request) {
        ContentVo contents = contentsService.getContents(cid);
        request.setAttribute("contents", contents);
        return "admin/page_edit";
    }

    @PostMapping(value = "publish")
    @ResponseBody
    @ActionLog(title = "Page", action = "页面发布")
    public RestResponseBo publishPage(@RequestParam String title, @RequestParam String content,
                                      @RequestParam String status, @RequestParam String slug,
                                      @RequestParam(required = false) Integer allowComment, @RequestParam(required = false) Integer allowPing, HttpServletRequest request) {

        UserVo users = this.user(request);
        ContentVo contents = new ContentVo();
        contents.setTitle(title);
        contents.setContent(content);
        contents.setStatus(status);
        contents.setSlug(slug);
        contents.setType(Types.PAGE.getType());
        if (null != allowComment) {
            contents.setAllowComment(allowComment == 1);
        }
        if (null != allowPing) {
            contents.setAllowPing(allowPing == 1);
        }
        contents.setAuthorId(users.getUid());

        try {
            contentsService.publish(contents);
        } catch (Exception e) {
            String msg = "页面发布失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

    @PostMapping(value = "modify")
    @ResponseBody
    @ActionLog(title = "Page", action = "页面编辑")
    public RestResponseBo modifyArticle(@RequestParam Integer cid, @RequestParam String title,
                                        @RequestParam String content,
                                        @RequestParam String status, @RequestParam String slug,
                                        @RequestParam(required = false) Integer allowComment, @RequestParam(required = false) Integer allowPing, HttpServletRequest request) {

        UserVo users = this.user(request);
        ContentVo contents = new ContentVo();
        contents.setCid(cid);
        contents.setTitle(title);
        contents.setContent(content);
        contents.setStatus(status);
        contents.setSlug(slug);
        contents.setType(Types.PAGE.getType());
        if (null != allowComment) {
            contents.setAllowComment(allowComment == 1);
        }
        if (null != allowPing) {
            contents.setAllowPing(allowPing == 1);
        }
        contents.setAuthorId(users.getUid());
        try {
            contentsService.updateArticle(contents);
        } catch (Exception e) {
            String msg = "页面编辑失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    @ActionLog(title = "Page", action = "页面删除")
    public RestResponseBo delete(@RequestParam int cid, HttpServletRequest request) {
        try {
            contentsService.deleteByCid(cid);
        } catch (Exception e) {
            String msg = "页面删除失败";
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
