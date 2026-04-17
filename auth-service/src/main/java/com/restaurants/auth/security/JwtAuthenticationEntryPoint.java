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

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest requete,
                         HttpServletResponse reponse,
                         AuthenticationException exceptionAuthentification) throws IOException {
        reponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        reponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> corps = new HashMap<>();
        corps.put("statut", HttpServletResponse.SC_UNAUTHORIZED);
        corps.put("erreur", "Non autorisé");
        corps.put("message", "Authentification requise pour accéder à cette ressource");
        corps.put("chemin", requete.getServletPath());

        new ObjectMapper().writeValue(reponse.getOutputStream(), corps);
    }
}
