package com.restaurants.business.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propriétés JWT lues depuis application.yaml (préfixe "jwt").
 * COURS INF1013: même secret partagé entre auth-service et business-service.
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConstants {

    private String secret;
    private int expiration;
    private String issuer;

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }

    public int getExpiration() { return expiration; }
    public void setExpiration(int expiration) { this.expiration = expiration; }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
}
