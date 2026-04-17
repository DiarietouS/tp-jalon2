package com.restaurants.auth.services;

import com.restaurants.auth.repositories.UtilisateurRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service de chargement des détails d'un utilisateur pour Spring Security.
 * Implémente UserDetailsService pour l'intégration avec Spring Security.
 *
 * COURS INF1013 - Sécurité :
 * "UserDetailsService : interface pour charger les données de l'utilisateur"
 * "loadUserByUsername : utilisé par AuthenticationProvider"
 */
@Service
public class UtilisateurDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Charge un utilisateur par son adresse courriel.
     * Utilisé par Spring Security lors de l'authentification.
     *
     * @param courriel l'adresse courriel de l'utilisateur (utilisé comme nom d'utilisateur)
     * @return les détails de l'utilisateur
     * @throws UsernameNotFoundException si l'utilisateur n'existe pas
     */
    @Override
    public UserDetails loadUserByUsername(String courriel) throws UsernameNotFoundException {
        return utilisateurRepository.findByCourriel(courriel)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur introuvable avec le courriel : " + courriel
                ));
    }
}
