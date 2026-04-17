package com.restaurants.auth.config;

import com.restaurants.auth.services.UtilisateurDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeansConfig {

    private final UtilisateurDetailsService utilisateurDetailsService;

    public BeansConfig(UtilisateurDetailsService utilisateurDetailsService) {
        this.utilisateurDetailsService = utilisateurDetailsService;
    }

    @Bean
    public PasswordEncoder encodeurMotDePasse() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider fournisseurAuthentification() {
        DaoAuthenticationProvider fournisseur = new DaoAuthenticationProvider();
        fournisseur.setUserDetailsService(utilisateurDetailsService);
        fournisseur.setPasswordEncoder(encodeurMotDePasse());
        return fournisseur;
    }

    @Bean
    public AuthenticationManager gestionnaireAuthentification(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
