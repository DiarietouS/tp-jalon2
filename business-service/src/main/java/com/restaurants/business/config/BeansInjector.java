package com.restaurants.business.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * business-service n'a pas de base utilisateurs.
 * On fournit un PasswordEncoder (requis par Spring Security)
 * et un UserDetailsService factice qui n'est jamais appelé
 * (l'authentification est gérée entièrement par JwtAuthenticationFilter).
 */
@Configuration
public class BeansInjector {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("Utilisez le JWT pour l'authentification");
        };
    }
}
