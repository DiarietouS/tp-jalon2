package com.restaurants.auth.config;

import com.restaurants.auth.security.JwtAuthenticationEntryPoint;
import com.restaurants.auth.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider fournisseurAuthentification;
    private final JwtAuthenticationEntryPoint pointEntreeAuthentification;
    private final CorsConfig corsConfig;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          AuthenticationProvider fournisseurAuthentification,
                          JwtAuthenticationEntryPoint pointEntreeAuthentification,
                          CorsConfig corsConfig) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.fournisseurAuthentification = fournisseurAuthentification;
        this.pointEntreeAuthentification = pointEntreeAuthentification;
        this.corsConfig = corsConfig;
    }

    @Bean
    public SecurityFilterChain chaineFiltreSecurity(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(pointEntreeAuthentification))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/connexion", "/api/auth/inscription").permitAll()
                .anyRequest().authenticated()
            )
            .authenticationProvider(fournisseurAuthentification)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
