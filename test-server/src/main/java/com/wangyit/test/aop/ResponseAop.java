package com.wangyit.test.aop;

import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 接口Aop，打印接口请求和返回内容
 */
@Aspect
@Order(5)
@Component
@Slf4j
public class ResponseAop {

    ThreadLocal<Long> startTime = new ThreadLocal<>();

    /**
     * 切点
     */
    @Pointcut("execution(public * com.wangyit.test.controller..*(..))")
    public void httpResponse() {
    }

    /**
     * 切点之前执行
     * @param joinPoint
     */
    @Before("httpResponse()")
    public void doBefore(JoinPoint joinPoint){
        //开始计时
        startTime.set(System.currentTimeMillis());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //打印请求的内容
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));//获取请求头中的User-Agent
        log.info("接口路径：{}" , request.getRequestURL().toString());
        log.info("浏览器：{}", userAgent.getBrowser().toString());
        log.info("浏览器版本：{}",userAgent.getBrowserVersion());
        log.info("操作系统: {}", userAgent.getOperatingSystem().toString());
        log.info("IP : {}" , request.getRemoteAddr());
        log.info("请求类型：{}", request.getMethod());
        log.info("类方法 : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        log.info("请求参数 : {} " + Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * 切点之后执行
     * @param ret
     */
    @AfterReturning(returning = "ret" , pointcut = "httpResponse()")
    public void doAfterReturning(Object ret){
        //处理完请求后，返回内容
        log.info("方法返回值：{}" , ret);
        log.info("方法执行时间：{}毫秒", (System.currentTimeMillis() - startTime.get()));
    }

}

