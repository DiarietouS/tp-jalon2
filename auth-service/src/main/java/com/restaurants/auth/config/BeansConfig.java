package com.restaurants.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.restaurants.auth.services.UtilisateurDetailsService;

/**
 * Configuration des beans partagés pour l'authentification.
 * Définit le gestionnaire d'authentification, le fournisseur et l'encodeur de mot de passe.
 *
 * COURS INF1013 - Sécurité :
 * "PasswordEncoder : BCryptPasswordEncoder"
 * "AuthenticationManager : gère l'authentification"
 */
@Configuration
public class BeansConfig {

    private final UtilisateurDetailsService utilisateurDetailsService;

    public BeansConfig(UtilisateurDetailsService utilisateurDetailsService) {
        this.utilisateurDetailsService = utilisateurDetailsService;
    }

    /**
     * Encodeur de mots de passe utilisant l'algorithme BCrypt.
     * BCrypt est recommandé car il intègre un sel (salt) automatiquement.
     *
     * @return l'encodeur de mots de passe
     */
    @Bean
    public PasswordEncoder encodeurMotDePasse() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Fournisseur d'authentification qui utilise la base de données.
     * Charge l'utilisateur par son courriel et vérifie le mot de passe.
     *
     * @return le fournisseur d'authentification
     */
    @Bean
    public AuthenticationProvider fournisseurAuthentification() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(utilisateurDetailsService);
        authProvider.setPasswordEncoder(encodeurMotDePasse());
        return authProvider;
    }

    /**
     * Gestionnaire d'authentification principal.
     * Délègue l'authentification au fournisseur configuré.
     *
     * @param config la configuration d'authentification de Spring
     * @return le gestionnaire d'authentification
     * @throws Exception si la configuration échoue
     */
    @Bean
    public AuthenticationManager gestionnaireAuthentification(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
