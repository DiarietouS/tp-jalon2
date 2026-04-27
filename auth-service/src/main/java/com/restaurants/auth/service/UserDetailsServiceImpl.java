package com.restaurants.auth.service;

import com.restaurants.auth.dto.ConnectedUserDTO;
import com.restaurants.auth.entity.UtilisateurEntity;
import com.restaurants.auth.repository.UtilisateurRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Charge l'utilisateur depuis la BD par son courriel.
 * COURS INF1013: "L'interface UserDetailsService a une méthode pour charger
 * l'utilisateur à partir de son nom d'utilisateur (ici le courriel)."
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public UserDetailsServiceImpl(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String courriel) throws UsernameNotFoundException {
        UtilisateurEntity user = utilisateurRepository.findByCourriel(courriel)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable: " + courriel));
        return new ConnectedUserDTO(user);
    }
}
