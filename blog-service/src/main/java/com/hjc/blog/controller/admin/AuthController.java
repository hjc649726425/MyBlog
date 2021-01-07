package com.hjc.blog.controller.admin;

import com.hjc.blog.annotation.ActionLog;
import com.hjc.blog.constant.WebConst;
import com.hjc.common.exception.TipException;
import com.hjc.blog.model.Bo.RestResponseBo;
import com.hjc.blog.model.Vo.UserVo;
import com.hjc.blog.controller.BaseController;
import com.hjc.blog.service.IUserService;
import com.hjc.blog.utils.TaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 用户后台登录/登出
 */
@Controller
@RequestMapping("/admin")
public class AuthController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private IUserService usersService;

    @GetMapping(value = "/login")
    @ActionLog(title = "Auth", action = "跳转登录页面")
    public String login() {
        return "admin/login";
    }

    @GetMapping(value = "/register")
    @ActionLog(title = "Auth", action = "跳转注册页面")
    public String register() {
        return "admin/register";
    }


    @PostMapping(value = "login")
    @ResponseBody
    @ActionLog(title = "Auth", action = "用户登录")
    public RestResponseBo doLogin(@RequestParam String username,
                                  @RequestParam String password,
                                  @RequestParam(required = false) String remeber_me,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {

        Integer error_count = cache.get("login_error_count");
        try {
            UserVo user = usersService.login(username, password);
            request.getSession().setAttribute(WebConst.LOGIN_SESSION_KEY, user);
            request.getSession().setAttribute(WebConst.LOGIN_SESSION_USER_NAME, user.getUsername());

            if (StringUtils.isNotBlank(remeber_me)) {
                TaleUtils.setCookie(response, user.getUid());
            }
        } catch (Exception e) {
            error_count = null == error_count ? 1 : error_count + 1;
            if (error_count > 3) {
                return RestResponseBo.fail("您输入密码已经错误超过3次，请10分钟后尝试");
            }
            cache.set("login_error_count", error_count, 10 * 60);
            String msg = "登录失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

    @PostMapping(value = "register")
    @ResponseBody
    @ActionLog(title = "Auth", action = "用户注册")
    public RestResponseBo doRegister(@RequestParam String username,
                                  @RequestParam String password,
                                  @RequestParam(required = false) String remeber_me,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {

        usersService.register(username, password);
        return RestResponseBo.ok();
    }

    /**
     * 注销
     *
     * @param session
     * @param response
     */
    @RequestMapping("/logout")
    @ActionLog(title = "Auth", action = "用户登出")
    public void logout(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        session.removeAttribute(WebConst.LOGIN_SESSION_KEY);
        session.removeAttribute(WebConst.LOGIN_SESSION_USER_NAME);
        Cookie cookie = new Cookie(WebConst.USER_IN_COOKIE, "");
        cookie.setValue(null);
        cookie.setMaxAge(0);// 立即销毁cookie
        cookie.setPath("/");
        response.addCookie(cookie);
        try {
            response.sendRedirect("/admin/login");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("注销失败", e);
        }
    }

}
