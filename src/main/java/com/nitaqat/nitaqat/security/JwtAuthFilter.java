package com.nitaqat.nitaqat.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    // Inject the service to load the user details
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        String token = null;
        String email = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);

            try {
                // Try to get the username (email) from the token
                email = jwtUtils.getEmailFromJwtToken(token);
            } catch (ExpiredJwtException e) {
                // Handle expired token logic
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":401,\"message\":\"Token expired\"}");
                return;
            } catch (JwtException e) {
                // Handle invalid token logic
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":401,\"message\":\"Invalid token\"}");
                return;
            }
        }

        // ✅ CRITICAL LOGIC: AUTHENTICATION
        // Check if we have an email and if the user is NOT already authenticated (SecurityContext is empty)
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 1. Load User Details from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

            // 2. Validate the token against the loaded user details
            if (jwtUtils.validateJwtToken(token, userDetails)) {

                // 3. Create the Authentication object
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, // This contains your CustomUserDetails (with the ID)
                        null, // Credentials are null for token-based authentication
                        userDetails.getAuthorities()
                );

                // 4. Attach request details (optional but recommended)
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 5. 🔥 CRITICAL: Set the Authentication object in the Security Context
                // This is what makes the user "logged in" for the duration of the request
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}