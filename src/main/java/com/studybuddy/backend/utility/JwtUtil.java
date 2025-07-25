package com.studybuddy.backend.utility;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    private final Key secretKey;

    private final long ACCESS_TOKEN_EXPIRY = 60 * 60 * 1000; // 1 hour
    private final long REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60 * 1000; // 7 days

    public JwtUtil(@Value("${JWT_SECRET}") String secret) {
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String id) {
        return createToken(id, ACCESS_TOKEN_EXPIRY);
    }

    public String generateRefreshToken(String id) {
        return createToken(id, REFRESH_TOKEN_EXPIRY);
    }

    /**
     * Creates a new JWT token.
     * 
     * @param id     - ID of the user.
     * @param expiry - Time the token is valid for in ms.
     * @return A new JWT token.
     */
    private String createToken(String id, long expiry) {
        return Jwts.builder()
                .setSubject(id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate token.
    public boolean validateToken(String token, String id) {
        final String tokenUsername = extractId(token);
        return (tokenUsername.equals(id) && !isTokenExpired(token));
    }

    // Extract ID from token.
    public String extractId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Check if token is expired.
    private boolean isTokenExpired(String token) {
        final Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    // Extract any claim with resolver function.
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
