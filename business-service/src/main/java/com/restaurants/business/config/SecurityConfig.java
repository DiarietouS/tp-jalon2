package com.restaurants.business.config;

import com.restaurants.business.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Configuration de Spring Security pour le microservice métier.
 * Valide les jetons JWT pour protéger les ressources.
 *
 * COURS INF1013 - Sécurité :
 * "@EnableWebSecurity : active la sécurité web Spring"
 * "Session STATELESS : les jetons JWT remplacent les sessions"
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CorsConfigurationSource corsConfigurationSource
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    /**
     * Configure la chaîne de filtres de sécurité.
     *
     * @param http le constructeur de sécurité HTTP
     * @return la chaîne de filtres configurée
     * @throws Exception si la configuration échoue
     */
    @Bean
    public SecurityFilterChain chaineFiltresSecurity(HttpSecurity http) throws Exception {
        http
            // Activer CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            // Désactiver CSRF pour les API REST
            .csrf(AbstractHttpConfigurer::disable)

            // Politique de session stateless
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Règles d'autorisation
            .authorizeHttpRequests(auth -> auth
                // Routes publiques (lecture seule des restaurants et plats)
                .requestMatchers(HttpMethod.GET, "/api/restaurants/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/plats/**").permitAll()
                .requestMatchers("/actuator/health", "/error").permitAll()
                // Toutes les autres routes nécessitent une authentification
                .anyRequest().authenticated()
            )

            // Ajouter le filtre JWT
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
