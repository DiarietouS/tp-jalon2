package com.restaurants.auth.service;

import com.restaurants.auth.dto.MotDePasseOubliResponseDTO;

public interface MotDePasseService {
    /** Génère un token de réinitialisation pour le courriel donné. */
    MotDePasseOubliResponseDTO demanderReinitialisation(String courriel);

    /** Valide le token et met à jour le mot de passe. */
    void reinitialiserMotDePasse(String jeton, String nouveauMotDePasse);
}
