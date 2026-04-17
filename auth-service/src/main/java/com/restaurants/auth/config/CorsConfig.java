package com.restaurants.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration CORS (Cross-Origin Resource Sharing).
 * Permet à l'application Angular (localhost:4200) d'accéder à l'API.
 *
 * COURS INF1013 :
 * "CORS doit être configuré pour permettre les requêtes depuis localhost:4200"
 */
@Configuration
public class CorsConfig {

    /**
     * Configure les règles CORS pour l'application.
     * Autorise les requêtes depuis l'application Angular.
     *
     * @return la source de configuration CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origines autorisées (application Angular en développement)
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // En-têtes autorisés
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));

        // Autoriser l'envoi de cookies et d'en-têtes d'authentification
        configuration.setAllowCredentials(true);

        // Exposer l'en-tête Authorization dans les réponses
        configuration.setExposedHeaders(List.of("Authorization"));

        // Appliquer cette configuration à toutes les routes
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
