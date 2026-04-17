package com.restaurants.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Point d'entrée d'authentification pour les requêtes non autorisées.
 * Retourne une réponse JSON avec un message d'erreur en français
 * quand une requête non authentifiée tente d'accéder à une ressource protégée.
 *
 * COURS INF1013 - Sécurité JWT :
 * "Attaque MiM : interception de la communication entre client et serveur"
 * "Le filtre JWT intercepte toutes les requêtes pour valider le jeton"
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Appelé lorsqu'une requête non authentifiée tente d'accéder à une ressource protégée.
     * Retourne une réponse 401 avec un message d'erreur en français.
     *
     * @param request       la requête HTTP
     * @param response      la réponse HTTP
     * @param authException l'exception d'authentification
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        // Configurer la réponse en JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Corps de la réponse avec message d'erreur en français
        Map<String, Object> corps = new HashMap<>();
        corps.put("statut", HttpServletResponse.SC_UNAUTHORIZED);
        corps.put("erreur", "Non autorisé");
        corps.put("message", "Accès refusé : jeton JWT manquant ou invalide");
        corps.put("chemin", request.getServletPath());

        objectMapper.writeValue(response.getOutputStream(), corps);
    }
}
