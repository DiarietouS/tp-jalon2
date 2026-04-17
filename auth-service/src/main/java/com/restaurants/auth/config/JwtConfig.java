package com.restaurants.auth.config;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Configuration du service JWT.
 * Définit la clé secrète et la durée d'expiration des jetons.
 *
 * COURS INF1013 - JWT :
 * "Un JWT est composé de 3 parties : Header.Payload.Signature"
 * "La signature utilise HMAC256 avec une clé secrète"
 */
@Configuration
@Getter
public class JwtConfig {

    /**
     * Clé secrète pour signer les jetons JWT (HMAC256).
     * Doit contenir au moins 256 bits (32 caractères).
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Durée de validité du jeton en millisecondes.
     * Par défaut : 24 heures (86400000 ms)
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Génère la clé de signature à partir du secret configuré.
     * Utilise l'algorithme HMAC-SHA256 (HS256).
     *
     * @return la clé de signature sécurisée
     */
    public SecretKey getCleSignature() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
