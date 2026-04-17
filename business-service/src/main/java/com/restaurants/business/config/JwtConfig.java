package com.restaurants.business.config;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Configuration JWT pour la validation des jetons dans le service métier.
 * La même clé secrète que dans l'auth-service doit être utilisée.
 */
@Configuration
@Getter
public class JwtConfig {

    /**
     * Clé secrète pour vérifier les jetons JWT.
     * Doit être identique à celle utilisée dans l'auth-service.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Génère la clé de vérification à partir du secret configuré.
     *
     * @return la clé de vérification
     */
    public SecretKey getCleSignature() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
