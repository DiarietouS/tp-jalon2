package com.restaurants.auth.config;

import com.restaurants.auth.security.JwtAuthenticationEntryPoint;
import com.restaurants.auth.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuration de la sécurité Spring Security avec JWT.
 * COURS INF1013:
 * - @EnableWebSecurity: fournit les configurations HttpSecurity (cors, csrf, session, accès)
 * - @EnableMethodSecurity: active @PreAuthorize, @PostAuthorize sur les méthodes
 * - SecurityFilterChain: remplace WebSecurityConfigurerAdapter (déprécié)
 * - SESSION STATELESS: JWT ne nécessite pas de session serveur
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Routes publiques: création de compte et connexion
    private static final String[] AUTH_WHITELIST = { "/api/auth/**" };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationFilter authFilter,
                                           JwtAuthenticationEntryPoint jwtEntryPoint) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(ehc -> ehc.authenticationEntryPoint(jwtEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(req -> req
                    .requestMatchers(AUTH_WHITELIST).permitAll()
                    .anyRequest().authenticated())
            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuration CORS: autorise les requêtes depuis l'app Angular (localhost:4200).
     * COURS INF1013: "Access-Control-Allow-Origin" + "Access-Control-Allow-Methods"
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
