package com.restaurants.business.dtos;

import com.restaurants.business.models.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant un restaurant.
 * Utilisé pour les échanges entre le client et l'API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {

    /** Identifiant unique du restaurant */
    private Long id;

    /** Nom du restaurant */
    private String nom;

    /** Description du restaurant */
    private String description;

    /** Adresse du restaurant */
    private String adresse;

    /** Texte de localisation */
    private String localisationTexte;

    /** Numéro de téléphone */
    private String telephone;

    /** Adresse courriel */
    private String courriel;

    /** URL de l'image */
    private String imageUrl;

    /** Type de cuisine */
    private String typeCuisine;

    /** Note moyenne */
    private Double note;

    /** Temps de livraison estimé */
    private String tempsLivraison;

    /** Frais de livraison */
    private Double fraisLivraison;

    /** Commande minimum */
    private Double commandeMinimum;

    /** Identifiant du propriétaire */
    private Long idProprietaire;

    /**
     * Convertit une entité Restaurant en DTO.
     *
     * @param restaurant l'entité à convertir
     * @return le DTO correspondant
     */
    public static RestaurantDTO depuisEntite(Restaurant restaurant) {
        return RestaurantDTO.builder()
                .id(restaurant.getId())
                .nom(restaurant.getNom())
                .description(restaurant.getDescription())
                .adresse(restaurant.getAdresse())
                .localisationTexte(restaurant.getLocalisationTexte())
                .telephone(restaurant.getTelephone())
                .courriel(restaurant.getCourriel())
                .imageUrl(restaurant.getImageUrl())
                .typeCuisine(restaurant.getTypeCuisine())
                .note(restaurant.getNote())
                .tempsLivraison(restaurant.getTempsLivraison())
                .fraisLivraison(restaurant.getFraisLivraison())
                .commandeMinimum(restaurant.getCommandeMinimum())
                .idProprietaire(restaurant.getIdProprietaire())
                .build();
    }
}
