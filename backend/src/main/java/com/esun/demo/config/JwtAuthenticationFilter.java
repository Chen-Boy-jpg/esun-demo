package com.esun.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 從 Header 拿 Token (通常格式是 Authorization: Bearer <TOKEN>)
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String phone = jwtUtils.extractUsername(token); // 需要在 JwtUtils 補上這個方法

            // 在 JwtAuthenticationFilter 裡的 doFilterInternal 方法中
            if (phone != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 加上驗證邏輯
                if (jwtUtils.validateToken(token, phone)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(phone, null,
                            new ArrayList<>());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}