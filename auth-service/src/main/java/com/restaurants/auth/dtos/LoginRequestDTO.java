package com.restaurants.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la requête de connexion.
 * Contient les informations nécessaires pour authentifier un utilisateur.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    /** Adresse courriel de l'utilisateur */
    @NotBlank(message = "Le courriel est obligatoire")
    @Email(message = "Le format du courriel est invalide")
    private String courriel;

    /** Mot de passe de l'utilisateur */
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;
}
