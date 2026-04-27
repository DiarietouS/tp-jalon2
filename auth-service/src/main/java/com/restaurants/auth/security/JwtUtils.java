package com.restaurants.auth.security;

import com.restaurants.auth.dto.ConnectedUserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utilitaire JWT : génération et validation des tokens.
 * COURS INF1013 (JWT): token = header.payload.signature
 * Signature: HMACSHA256(base64(header) + "." + base64(payload) + secret)
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

    /**
     * Génère un token JWT pour l'utilisateur connecté.
     * COURS INF1013: payload contient username, id, role, iat, exp, iss
     */
    public String generateToken(ConnectedUserDTO userDetails) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + (long) jwtConstants.getExpiration() * 3600 * 1000);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("id", userDetails.getId())
                .claim("role", userDetails.getRole())
                .issuedAt(now)
                .expiration(exp)
                .issuer(jwtConstants.getIssuer())
                .signWith(getSigningKey())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
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
