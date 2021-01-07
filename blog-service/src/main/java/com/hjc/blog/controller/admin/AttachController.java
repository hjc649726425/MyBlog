package com.hjc.blog.controller.admin;

import com.github.pagehelper.PageInfo;
import com.hjc.blog.annotation.ActionLog;
import com.hjc.common.exception.TipException;
import com.hjc.blog.model.Bo.RestResponseBo;
import com.hjc.blog.constant.WebConst;
import com.hjc.blog.controller.BaseController;
import com.hjc.blog.dto.LogActions;
import com.hjc.blog.dto.Types;
import com.hjc.blog.model.Vo.AttachVo;
import com.hjc.blog.model.Vo.UserVo;
import com.hjc.blog.service.IAttachService;
import com.hjc.blog.utils.Commons;
import com.hjc.blog.utils.TaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 附件管理
 *
 */
@Controller
@RequestMapping("admin/attach")
public class AttachController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttachController.class);

    public static final String CLASSPATH = TaleUtils.getUplodFilePath();

    @Autowired
    private IAttachService attachService;

    /**
     * 附件页面
     *
     * @param request
     * @param page
     * @param limit
     * @return
     */
    @GetMapping(value = "")
    public String index(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "12") int limit) {
        PageInfo<AttachVo> attachPaginator = attachService.getAttachs(page, limit);
        request.setAttribute("attachs", attachPaginator);
        request.setAttribute(Types.ATTACH_URL.getType(), Commons.site_option(Types.ATTACH_URL.getType(), Commons.site_url()));
        request.setAttribute("max_file_size", WebConst.MAX_FILE_SIZE / 1024);
        return "admin/attach";
    }

    /**
     * 上传文件接口
     *
     * @param request
     * @return
     */
    @PostMapping(value = "upload")
    @ResponseBody
    @ActionLog(title = "Attach", action = "附件上传")
    public RestResponseBo upload(HttpServletRequest request, @RequestParam("file") MultipartFile[] multipartFiles) throws IOException {
        UserVo users = this.user(request);
        Integer uid = users.getUid();
        List<String> errorFiles = new ArrayList<>();
        try {
            for (MultipartFile multipartFile : multipartFiles) {
                String fname = multipartFile.getOriginalFilename();
                if (multipartFile.getSize() <= WebConst.MAX_FILE_SIZE) {
                    String fkey = TaleUtils.getFileKey(fname);
                    String ftype = TaleUtils.isImage(multipartFile.getInputStream()) ? Types.IMAGE.getType() : Types.FILE.getType();
                    File file = new File(CLASSPATH+fkey);
                    try {
                        FileCopyUtils.copy(multipartFile.getInputStream(),new FileOutputStream(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    attachService.save(fname, fkey, ftype, uid);
                } else {
                    errorFiles.add(fname);
                }
            }
        } catch (Exception e) {
            return RestResponseBo.fail();
        }
        return RestResponseBo.ok(errorFiles);
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    @ActionLog(title = "Attach", action = "附件删除")
    public RestResponseBo delete(@RequestParam Integer id, HttpServletRequest request) {
        try {
            AttachVo attach = attachService.selectById(id);
            if (null == attach) return RestResponseBo.fail("不存在该附件");
            attachService.deleteById(id);
            new File(CLASSPATH+attach.getFkey()).delete();
        } catch (Exception e) {
            String msg = "附件删除失败";
            if (e instanceof TipException) msg = e.getMessage();
            else LOGGER.error(msg, e);
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

}
