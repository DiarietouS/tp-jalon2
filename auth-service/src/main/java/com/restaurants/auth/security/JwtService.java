package com.restaurants.auth.security;

import com.restaurants.auth.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

    public String genererToken(UserDetails userDetails) {
        return genererToken(new HashMap<>(), userDetails);
    }

    public String genererToken(Map<String, Object> claimsSupplementaires, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claimsSupplementaires)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
                .signWith(getCleSignature(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean estTokenValide(String token, UserDetails userDetails) {
        final String courriel = extraireCourriel(token);
        return courriel.equals(userDetails.getUsername()) && !estTokenExpire(token);
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
