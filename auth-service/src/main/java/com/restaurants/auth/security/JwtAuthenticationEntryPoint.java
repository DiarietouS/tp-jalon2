package com.restaurants.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Point d'entrée pour les erreurs 401.
 * COURS INF1013: "AuthenticationEntryPoint détecte les erreurs de type 401
 * lorsque les clients accèdent aux ressources protégées sans authentification."
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"Accès non autorisé. Token JWT manquant ou invalide.\"}");
    }
}
