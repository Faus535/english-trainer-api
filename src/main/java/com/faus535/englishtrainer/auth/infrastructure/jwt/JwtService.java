package com.faus535.englishtrainer.auth.infrastructure.jwt;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public String generateToken(AuthUser user) {
        return buildToken(user, expiration);
    }

    public String generateRefreshToken(AuthUser user) {
        return buildToken(user, refreshExpiration);
    }

    private String buildToken(AuthUser user, long expirationMs) {
        return Jwts.builder()
                .subject(user.id().value().toString())
                .claim("email", user.email())
                .claim("profileId", user.userProfileId().value().toString())
                .claim("role", user.role())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUserId(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractProfileId(String token) {
        return extractClaims(token).get("profileId", String.class);
    }

    public String extractRole(String token) {
        String role = extractClaims(token).get("role", String.class);
        return role != null ? role : "USER";
    }

    public long getRefreshExpiration() {
        return refreshExpiration;
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("Token expired for subject {}", e.getClaims().getSubject());
            return false;
        } catch (SignatureException e) {
            log.warn("Invalid token signature");
            return false;
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
