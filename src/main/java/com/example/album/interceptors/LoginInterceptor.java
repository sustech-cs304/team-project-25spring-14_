package com.example.album.interceptors;

import com.example.album.utils.JwtUtil;
import com.example.album.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;
import java.util.Map;


@Component
public class LoginInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 令牌验证
        logger.info("📌 收到请求: {} {}", request.getMethod(), request.getRequestURL());
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            logger.info("✅ 放行 OPTIONS 预检请求");
            response.setStatus(HttpServletResponse.SC_OK);
            return false;
        }
        String token = request.getHeader("Authorization");
        logger.info("📌 请求头: {}", request.getHeader("Authorization"));
        // 验证token
        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);
            // 将用户信息存入ThreadLocal
            ThreadLocalUtil.set(claims);
            // 放行
            return true;
        } catch (Exception e) {
            // http响应状态码为401
            response.setStatus(401);
            // 不放行
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除ThreadLocal中的用户信息
        ThreadLocalUtil.remove();
    }
}
