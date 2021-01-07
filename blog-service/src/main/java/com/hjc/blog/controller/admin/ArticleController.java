package com.hjc.blog.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.hjc.blog.annotation.ActionLog;
import com.hjc.common.exception.TipException;
import com.hjc.blog.model.Bo.RestResponseBo;
import com.hjc.blog.controller.BaseController;
import com.hjc.blog.dto.LogActions;
import com.hjc.blog.dto.Types;
import com.hjc.blog.model.Vo.ContentVo;
import com.hjc.blog.model.Vo.MetaVo;
import com.hjc.blog.model.Vo.UserVo;
import com.hjc.blog.service.IContentService;
import com.hjc.blog.service.IMetaService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/admin/article")
public class ArticleController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private IContentService contentsService;

    @Autowired
    private IMetaService metasService;

    @GetMapping(value = "")
    @ActionLog(title = "Article", action = "文章列表界面")
    public String index(@RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "15") int limit, HttpServletRequest request) {

        QueryWrapper<ContentVo> wrapper = new QueryWrapper<>();
        wrapper.eq("type", Types.ARTICLE.getType()).orderByDesc("create_time");

        PageInfo<ContentVo> contentsPaginator = contentsService.getArticlesWithpage(wrapper,page,limit);
        request.setAttribute("articles", contentsPaginator);
        return "admin/article_list";
    }

    @GetMapping(value = "/publish")
    @ActionLog(title = "Article", action = "文章修改发布界面")
    public String newArticle(HttpServletRequest request) {
        List<MetaVo> categories = metasService.getMetas(Types.CATEGORY.getType());
        request.setAttribute("categories", categories);
        return "admin/article_edit";
    }

    @GetMapping(value = "/{cid}")
    @ActionLog(title = "Article", action = "文章详细")
    public String editArticle(@PathVariable String cid, HttpServletRequest request) {
        ContentVo contents = contentsService.getContents(cid);
        request.setAttribute("contents", contents);
        List<MetaVo> categories = metasService.getMetas(Types.CATEGORY.getType());
        request.setAttribute("categories", categories);
        request.setAttribute("active", "article");
        return "admin/article_edit";
    }

    @PostMapping(value = "/publish")
    @ResponseBody
    @ActionLog(title = "Article", action = "文章发布")
    public RestResponseBo publishArticle(ContentVo contents, HttpServletRequest request) {
        UserVo users = this.user(request);
        contents.setAuthorId(users.getUid());
        contents.setType(Types.ARTICLE.getType());
        if (StringUtils.isBlank(contents.getCategories())) {
            contents.setCategories("默认分类");
        }
        try {
            contentsService.publish(contents);
        } catch (Exception e) {
            String msg = "文章发布失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

    @PostMapping(value = "/modify")
    @ResponseBody
    @ActionLog(title = "Article", action = "文章修改")
    public RestResponseBo modifyArticle(ContentVo contents,HttpServletRequest request) {
        UserVo users = this.user(request);
        contents.setAuthorId(users.getUid());
        contents.setType(Types.ARTICLE.getType());
        try {
            contentsService.updateArticle(contents);
        } catch (Exception e) {
            String msg = "文章编辑失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    @ActionLog(title = "Article", action = "文章删除")
    public RestResponseBo delete(@RequestParam int cid, HttpServletRequest request) {
        try {
            contentsService.deleteByCid(cid);
        } catch (Exception e) {
            String msg = "文章删除失败";
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
