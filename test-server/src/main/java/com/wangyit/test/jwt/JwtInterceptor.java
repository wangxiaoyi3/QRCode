package com.wangyit.test.jwt;

import com.google.gson.Gson;
import com.wangyit.test.base.BaseResult;
import com.wangyit.test.base.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        ResultCode resultCode = null;
        if (token != null) {
            try {
                JwtUtil.verify(token);
                return true;
            } catch (Exception e) {
                resultCode = ResultCode.TOKEN_INVALID;
            }
        } else {
            resultCode = ResultCode.TOKEN_NULL;
        }
        BaseResult<?> baseResult = BaseResult.failed(resultCode);
        //错误信息响应到前台
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().print(new Gson().toJson(baseResult));
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return false;
    }
}
