package com.restaurants.business.security;

import com.restaurants.business.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final JwtConfig jwtConfig;

    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String extraireCourriel(String token) {
        return extraireClaim(token, Claims::getSubject);
    }

    public <T> T extraireClaim(String token, Function<Claims, T> resolveurClaim) {
        final Claims claims = extraireTousClaims(token);
        return resolveurClaim.apply(claims);
    }

    public boolean estTokenValide(String token, String courriel) {
        final String tokenCourriel = extraireCourriel(token);
        return tokenCourriel.equals(courriel) && !estTokenExpire(token);
    }

    private boolean estTokenExpire(String token) {
        return extraireExpiration(token).before(new Date());
    }

    private Date extraireExpiration(String token) {
        return extraireClaim(token, Claims::getExpiration);
    }

    private Claims extraireTousClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getCleSignature())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getCleSignature() {
        byte[] cleBytes = jwtConfig.getSecret().getBytes();
        return Keys.hmacShaKeyFor(cleBytes);
    }
}
