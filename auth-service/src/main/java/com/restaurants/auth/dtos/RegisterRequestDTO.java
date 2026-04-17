package com.restaurants.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la requête d'inscription d'un nouvel utilisateur.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    /** Prénom de l'utilisateur */
    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    /** Nom de famille de l'utilisateur */
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    /** Adresse courriel unique */
    @NotBlank(message = "Le courriel est obligatoire")
    @Email(message = "Le format du courriel est invalide")
    private String courriel;

    /** Mot de passe (sera chiffré avec BCrypt) */
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;

    /** Numéro de téléphone (optionnel) */
    private String telephone;

    /** Adresse postale (optionnel) */
    private String adresse;

    /**
     * Rôle de l'utilisateur.
     * Valeurs acceptées : CLIENT, RESTAURATEUR, LIVREUR
     * Par défaut : CLIENT
     */
    private String role;
}
