package com.nitaqat.nitaqat.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // ✅ Your Base64 secret (keep this secure!)
    private final String jwtSecret = "a8rI3VqV3HRJZP8Z7fUWhuhwIhHGHep9zN5ukdN+zOaGc5qtrtPUuUWhjX9sCQ6yTqMQW8AqOfq1Rjfs7dJowQ==";

    // ✅ Expiration time — 1 day in milliseconds
    private final long jwtExpirationMs = 24*60*60*1000   ;

    // ✅ Decode and build key from Base64 secret
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // ✅ Generate a new token
    public String generateJwtToken(String email, Long userId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Extract all claims (throws ExpiredJwtException if expired)
    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, null, "Token expired");
        }
    }


    // ✅ Extract user ID
    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    // ✅ Extract email (subject)
    public String getEmailFromJwtToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ✅ Validate token (handle expiration & invalidity)
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("❌ JWT token expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("❌ JWT token is unsupported: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("❌ JWT token is malformed: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("❌ JWT signature validation failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ JWT token is empty or null: " + e.getMessage());
        }
        return false;
    }

    public String getJtiFromToken(String token) {
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret.getBytes())
                    .build()
                    .parseClaimsJws(token);

            Claims claims = jws.getBody();
            return claims.getId(); // jti
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateJwtToken(String token, UserDetails userDetails) {
        try {
            final String email = getEmailFromJwtToken(token);

            // 1. Check if the token's email matches the user details' email
            // 2. Check token validity (signature and expiry)
            return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (ExpiredJwtException e) {
            // The isTokenExpired check handles this, but catch here just in case.
            return false;
        }
    }

    // New helper method to check only expiration (used by the new validate method)
    private boolean isTokenExpired(String token) {
        try {
            return extractAllClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            // If extracting claims throws ExpiredJwtException, the token is certainly expired.
            return true;
        }
    }
}
