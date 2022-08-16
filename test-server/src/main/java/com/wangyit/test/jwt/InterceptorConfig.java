package com.wangyit.test.jwt;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    /**
     * 添加自定义的拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/**") // 拦截路径
                .excludePathPatterns("/ws/**")
                .excludePathPatterns("/swagger-ui/**")
                .excludePathPatterns("/doc.html")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("/swagger**/**")
                .excludePathPatterns("/v3/api-docs")
                .excludePathPatterns("/**/user/login") // 登录接口排除
                .excludePathPatterns("/**/qrcode/generate/**"); // 二维码接口排除
    }
}