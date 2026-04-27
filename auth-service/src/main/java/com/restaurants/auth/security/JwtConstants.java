package com.restaurants.auth.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties JWT lues depuis application.yaml (préfixe "jwt").
 * COURS INF1013: injection des propriétés via @ConfigurationProperties
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConstants {

    private String secret;
    private int expiration; // en heures
    private String issuer;

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }

    public int getExpiration() { return expiration; }
    public void setExpiration(int expiration) { this.expiration = expiration; }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
}
