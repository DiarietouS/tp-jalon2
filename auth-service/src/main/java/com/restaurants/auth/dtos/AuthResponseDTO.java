package com.restaurants.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la réponse d'authentification.
 * Contient le jeton JWT et les informations de l'utilisateur connecté.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    /** Jeton JWT pour authentifier les requêtes suivantes */
    private String token;

    /** Type de jeton (toujours "Bearer") */
    private String typeToken;

    /** Informations de l'utilisateur connecté */
    private UtilisateurDTO utilisateur;

    /** Message de confirmation */
    private String message;
}
