package com.shj.eids.config;

import com.shj.eids.interceptor.AdminAuthentication;
import com.shj.eids.interceptor.CookieLoginIntercepter;
import com.shj.eids.interceptor.EpidemicEventInterceptor;
import com.shj.eids.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName: WebMvcConfig
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-06 12:11
 **/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;
    @Autowired
    private EpidemicEventInterceptor epidemicEventInterceptor;
    @Autowired
    private CookieLoginIntercepter cookieLoginIntercepter;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(cookieLoginIntercepter).addPathPatterns("/**");
        registry.addInterceptor(loginInterceptor).addPathPatterns("/user/search", "/user/article/edit",
                "/user/img/doUpload", "/user/article/publish", "/user/search**", "/admin**", "/admin/**");
        registry.addInterceptor(new AdminAuthentication()).addPathPatterns("/admin**", "/admin/**");
        registry.addInterceptor(epidemicEventInterceptor).addPathPatterns("/**");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/index");
        registry.addViewController("/test").setViewName("test");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/register").setViewName("register");
        registry.addViewController("/user/help").setViewName("help");
        registry.addViewController("/admin/event").setViewName("admin/event");
        registry.addViewController("/admin/help").setViewName("admin/help");
        registry.addViewController("/admin").setViewName("admin/adminindex");
        registry.addViewController("/admin/helptable/postaid").setViewName("admin/postaid");
        registry.addViewController("/admin/article").setViewName("admin/ariticle");

        

    }
}