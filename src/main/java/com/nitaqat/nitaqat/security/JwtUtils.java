package com.nitaqat.nitaqat.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import io.jsonwebtoken.io.Decoders;

@Component
public class JwtUtils {

    private final String jwtSecret = "a8rI3VqV3HRJZP8Z7fUWhuhwIhHGHep9zN5ukdN+zOaGc5qtrtPUuUWhjX9sCQ6yTqMQW8AqOfq1Rjfs7dJowQ==";
    private final long jwtExpirationMs = 86400000; // 1 day

    private Key key() {
        // decode Base64 secret
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    public String generateJwtToken(String email, Long userId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId) // 👈 add userId here
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key()) // 👈 use key() not raw string
                .compact();
    }

    public String getEmailFromJwtToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
