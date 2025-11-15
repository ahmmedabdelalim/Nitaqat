package com.nitaqat.nitaqat.filter;

import com.nitaqat.nitaqat.security.JwtUtils;
import com.nitaqat.nitaqat.service.RedisSessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ActivityTrackingFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisSessionService redisSessionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtUtils.validateJwtToken(token)) {
                Long userId = jwtUtils.extractUserId(token);

                String sessionKey = "USER_SESSION_" + userId;

                // ✅ THIS IS THE IMPORTANT LINE
                redisSessionService.updateLastActivityByKey(sessionKey);
            }
        }

        filterChain.doFilter(request, response);
    }
}
