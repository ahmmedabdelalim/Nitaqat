package com.nitaqat.nitaqat.config;

import com.nitaqat.nitaqat.filter.ActivityTrackingFilter;
import com.nitaqat.nitaqat.security.JwtAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    // ✅ Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    // ✅ Custom unauthorized response
    private final AuthenticationEntryPoint apiAuthEntryPoint = (request, response, authException) -> {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"status\":401,\"message\":\"Unauthorized access\"}");
    };

    // ✅ Register JwtAuthFilter as a Spring bean
    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter();
    }

    // ✅ Main API security configuration
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurity(HttpSecurity http, JwtAuthFilter jwtAuthFilter , ActivityTrackingFilter activityTrackingFilter) throws Exception {
        System.out.println("API Security Filter Chain applied for /api/**"); // Debug log

        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "api/**",
                                "/api/auth/**",
                                "/api/import",
                                "/api/import/**",
                                "/api/profession-report",
                                "/api/profession-report/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(apiAuthEntryPoint))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                // ✅ Injected filter bean (not new object)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(activityTrackingFilter,JwtAuthFilter.class);

        return http.build();
    }
}
