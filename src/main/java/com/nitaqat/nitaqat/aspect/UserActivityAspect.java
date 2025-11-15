package com.nitaqat.nitaqat.aspect;


import com.nitaqat.nitaqat.service.ActivityService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
@Aspect
@Component
public class UserActivityAspect {
    private final ActivityService activityService;

    public UserActivityAspect(ActivityService activityService) {
        this.activityService = activityService;
    }

    @After("@annotation(com.nitaqat.nitaqat.aspect.LogUserAction)")
    public void logAction(JoinPoint joinPoint) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return;

        HttpServletRequest request = attrs.getRequest();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogUserAction annotation = method.getAnnotation(LogUserAction.class);
        String action = annotation.action();

        // Retrieve current user (from security context or token)
        Long userId = 1L; // example
        String username = "testUser"; // example

        activityService.logAction(userId, username, action, request.getRequestURI(), request);
    }

}
