package com.nitaqat.nitaqat.aspect;

import com.nitaqat.nitaqat.security.CustomUserDetails;
import com.nitaqat.nitaqat.service.ActivityService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        Long userId = null;
        String username = "ANONYMOUS";

        // 🔹 1. Prefer explicitly set attributes (for pre-auth endpoints like login/verify-otp,
        //        where SecurityContextHolder has no authenticated user yet)
        Object attrUserId = request.getAttribute("LOG_USER_ID");
        Object attrUsername = request.getAttribute("LOG_USERNAME");

        if (attrUserId != null) {
            userId = (Long) attrUserId;
            username = (attrUsername != null) ? (String) attrUsername : "UNKNOWN";
        } else {
            // 🔹 2. Fallback to SecurityContext for normal authenticated endpoints
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                    !(authentication.getPrincipal() instanceof String &&
                            authentication.getPrincipal().equals("anonymousUser"))) {
                try {
                    Object principal = authentication.getPrincipal();
                    if (principal instanceof CustomUserDetails customUser) {
                        userId = customUser.getId();
                        username = customUser.getUsername();
                    } else {
                        // You might need to query the database here to get the ID
                        // if it's not in the principal object.
                        // For now, we only get the username:
                        username = authentication.getName();
                        // userId remains null, or you look it up here.
                    }
                } catch (Exception e) {
                    // Handle cases where the principal object is unexpected
                    username = "UNKNOWN_USER_ERROR";
                    System.err.println("Error casting Security Principal: " + e.getMessage());
                }
            }
        }

        activityService.logAction(userId, username, action, request.getRequestURI(), request);
    }
}