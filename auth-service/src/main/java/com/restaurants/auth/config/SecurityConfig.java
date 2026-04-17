package com.restaurants.auth.config;

import com.restaurants.auth.security.JwtAuthenticationEntryPoint;
import com.restaurants.auth.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Configuration principale de Spring Security.
 * Définit les règles d'accès, la politique de session et les filtres.
 *
 * COURS INF1013 - Sécurité :
 * "@EnableWebSecurity : active la sécurité web Spring"
 * "CSRF désactivé pour les API REST (stateless)"
 * "Session STATELESS : aucune session côté serveur"
 * "JWT : chaque requête porte son propre jeton d'authentification"
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider fournisseurAuthentification;
    private final JwtAuthenticationEntryPoint pointEntreeAuthentification;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider fournisseurAuthentification,
            JwtAuthenticationEntryPoint pointEntreeAuthentification,
            CorsConfigurationSource corsConfigurationSource
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.fournisseurAuthentification = fournisseurAuthentification;
        this.pointEntreeAuthentification = pointEntreeAuthentification;
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
            // Activer la configuration CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            // Désactiver CSRF pour les API REST (stateless)
            .csrf(AbstractHttpConfigurer::disable)

            // Configurer le point d'entrée pour les erreurs d'authentification
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(pointEntreeAuthentification)
            )

            // Politique de session stateless (pas de session côté serveur)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Règles d'autorisation des requêtes
            .authorizeHttpRequests(auth -> auth
                // Routes publiques (pas d'authentification requise)
                .requestMatchers(
                    "/api/auth/**",
                    "/actuator/health",
                    "/error"
                ).permitAll()
                // Toutes les autres routes nécessitent une authentification
                .anyRequest().authenticated()
            )

            // Fournisseur d'authentification personnalisé
            .authenticationProvider(fournisseurAuthentification)

            // Ajouter le filtre JWT avant le filtre d'authentification standard
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
