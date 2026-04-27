package com.nitaqat.nitaqat.config;

import com.nitaqat.nitaqat.filter.ActivityTrackingFilter;
import com.nitaqat.nitaqat.security.JwtAuthFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    // ✅ Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    // ✅ Custom 401 response for API routes
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

    // ✅ CHAIN 1 — API routes: JWT protected
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurity(HttpSecurity http,
                                           JwtAuthFilter jwtAuthFilter,
                                           ActivityTrackingFilter activityTrackingFilter) throws Exception {
        http
                .securityMatcher("/api/**")
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/export/**",
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
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(activityTrackingFilter, JwtAuthFilter.class);

        return http.build();
    }



    // ✅ CHAIN 2 — All other routes: redirect to Flutter app
//    @Bean
//    @Order(2)
//    public SecurityFilterChain webSecurity(HttpSecurity http) throws Exception {
//        http
//                .securityMatcher("/**")
//                .csrf(csrf -> csrf.disable())
//                .formLogin(form -> form.disable())
//                .httpBasic(basic -> basic.disable())
//                .oauth2Login(oauth -> oauth.disable())   // 👈 disables the Google login redirect
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll()
//                )
//                .addFilterBefore(new OncePerRequestFilter() {
//                    @Override
//                    protected void doFilterInternal(HttpServletRequest request,
//                                                    HttpServletResponse response,
//                                                    FilterChain filterChain)
//                            throws ServletException, IOException {
//                        // Redirect every non-API browser request to Flutter app
//                        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY); // 301
//                        response.setHeader("Location", "https://www.nitaqatcalc.com/");
//                    }
//                }, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
}