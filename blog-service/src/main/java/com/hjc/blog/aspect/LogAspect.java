package com.hjc.blog.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hjc.blog.annotation.ActionLog;
import com.hjc.blog.constant.WebConst;
import com.hjc.blog.model.Vo.LogVo;
import com.hjc.blog.service.ILogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.Arrays;

/**
 * 借口aop
 */
@Aspect
@Component
public class LogAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSource.class);

    StopWatch watch = new StopWatch();

    @Autowired
    ILogService logService;

    @Autowired
    ObjectMapper objectMapper;

    @Pointcut("execution(public * com.hjc.blog.controller..*.*(..))")
    public void webLog(){}

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
        LOGGER.info("URL : " + request.getRequestURL().toString() + ",IP : " + request.getRemoteAddr() + ",CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + ",ARGS : " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(returning = "object", pointcut = "webLog()")
    public void doAfterReturning(Object object) throws Throwable {
        // 处理完请求，返回内容
        LOGGER.info("RESPONSE : " + object);
    }


    @Around(value = "@annotation(com.hjc.blog.annotation.ActionLog)")
    public Object around(ProceedingJoinPoint pjd) {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        Object result = null;
        MethodSignature ms = (MethodSignature)pjd.getSignature();
        Class<?> targetCls=pjd.getTarget().getClass();
        String methodName = ms.getName();
        Object[] args = pjd.getArgs();

        try {
            ActionLog al = targetCls.getDeclaredMethod(methodName, ms.getParameterTypes()).getAnnotation(ActionLog.class);

            //执行目标方法
            watch.start();
            result = pjd.proceed();
            watch.stop();

            LogVo vo = new LogVo();
            vo.setAction(al.action());
            vo.setTitle(al.title());
            vo.setMethod(request.getMethod());
            vo.setUrl(request.getRequestURL().toString());
            vo.setIp(request.getRemoteAddr());
            Object username = request.getSession().getAttribute(WebConst.LOGIN_SESSION_USER_NAME);
            vo.setUserName(username == null ? "" : username.toString());
            vo.setProcessTime(watch.getLastTaskTimeMillis());
            vo.setReqData(objectMapper.writeValueAsString(request.getParameterMap()));
            vo.setRespData(objectMapper.writeValueAsString(result));
            logService.insertLog(vo);

            System.out.println("The method" + methodName+" begins with"+ Arrays.asList(pjd.getArgs()));
        }catch (Throwable e){
            LOGGER.error(e.getMessage(), e);
        }

        return result;
    }
}
