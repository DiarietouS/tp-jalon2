package com.restaurants.auth.config;

import com.restaurants.auth.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Beans de configuration pour l'authentification.
 * COURS INF1013: "définir l'injecteur de PasswordEncoder" + AuthenticationManager.
 * DaoAuthenticationProvider + UserDetailsService + PasswordEncoder valide les credentials.
 */
@Configuration
public class BeansInjector {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsServiceImpl userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        // Spring Security 7: constructeur exige UserDetailsService
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
