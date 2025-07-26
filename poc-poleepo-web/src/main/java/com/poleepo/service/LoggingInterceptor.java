package com.poleepo.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String correlationId = UUID.randomUUID().toString();
        log.info("Request URI: {} ", request.getRequestURI());
        log.info("X-Correlation-ID: {} ", correlationId);
        response.setHeader("X-Correlation-ID", correlationId);
        return true;
    }
}
