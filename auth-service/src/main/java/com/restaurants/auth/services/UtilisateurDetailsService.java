package com.restaurants.auth.services;

import com.restaurants.auth.repositories.UtilisateurRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String courriel) throws UsernameNotFoundException {
        return utilisateurRepository.findByCourriel(courriel)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé avec le courriel: " + courriel));
    }
}
