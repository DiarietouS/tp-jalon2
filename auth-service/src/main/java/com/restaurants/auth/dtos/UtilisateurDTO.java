package com.restaurants.auth.dtos;

import com.restaurants.auth.models.Utilisateur;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant un utilisateur (sans le mot de passe).
 * Utilisé pour transmettre les données de l'utilisateur de façon sécurisée.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurDTO {

    /** Identifiant unique de l'utilisateur */
    private Long id;

    /** Prénom de l'utilisateur */
    private String prenom;

    /** Nom de famille de l'utilisateur */
    private String nom;

    /** Adresse courriel de l'utilisateur */
    private String courriel;

    /** Numéro de téléphone */
    private String telephone;

    /** Adresse postale */
    private String adresse;

    /** Rôle de l'utilisateur dans le système */
    private Utilisateur.RoleUtilisateur role;

    /**
     * Convertit une entité Utilisateur en DTO (sans le mot de passe).
     *
     * @param utilisateur l'entité à convertir
     * @return le DTO correspondant
     */
    public static UtilisateurDTO depuisEntite(Utilisateur utilisateur) {
        return UtilisateurDTO.builder()
                .id(utilisateur.getId())
                .prenom(utilisateur.getPrenom())
                .nom(utilisateur.getNom())
                .courriel(utilisateur.getCourriel())
                .telephone(utilisateur.getTelephone())
                .adresse(utilisateur.getAdresse())
                .role(utilisateur.getRole())
                .build();
    }
}
