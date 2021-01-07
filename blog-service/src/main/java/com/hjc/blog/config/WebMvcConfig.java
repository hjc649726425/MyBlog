package com.hjc.blog.config;


import com.hjc.blog.interceptor.BaseInterceptor;
import com.hjc.blog.utils.TaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 向mvc中添加自定义组件.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private BaseInterceptor baseInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(baseInterceptor)
            .addPathPatterns("/**");
    }

    /**
     * 添加静态资源文件，外部可以直接访问地址
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:"+ TaleUtils.getUplodFilePath()+"upload/");
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

    //解决跨域问题
    @Override
    public void addCorsMappings(CorsRegistry registry) {//解决跨域的问题
        registry.addMapping("**")
                .allowedOrigins("*")
                .allowedHeaders("*")
                .allowedMethods("*")
                .maxAge(3600)
                .allowCredentials(true);
    }
}
