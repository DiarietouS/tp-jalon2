package com.restaurants.business.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Utilitaire JWT pour business-service.
 * Ne génère PAS de tokens (rôle de auth-service uniquement).
 * Valide et lit les tokens générés par auth-service.
 */
@Component
public class JwtUtils {

    private final JwtConstants jwtConstants;

    public JwtUtils(JwtConstants jwtConstants) {
        this.jwtConstants = jwtConstants;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtConstants.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return (String) getClaims(token).get("role");
    }

    public Long getIdFromToken(String token) {
        Object id = getClaims(token).get("id");
        if (id instanceof Integer) return ((Integer) id).longValue();
        if (id instanceof Long) return (Long) id;
        return null;
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
