package com.restaurants.business.dtos;

import com.restaurants.business.models.Plat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant un plat.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatDTO {

    /** Identifiant unique du plat */
    private Long id;

    /** Nom du plat */
    private String nom;

    /** Description du plat */
    private String description;

    /** Prix du plat */
    private Double prix;

    /** Catégorie du plat */
    private String categorie;

    /** URL de l'image */
    private String imageUrl;

    /** Disponibilité du plat */
    private boolean disponible;

    /** Identifiant du restaurant */
    private Long idRestaurant;

    /**
     * Convertit une entité Plat en DTO.
     *
     * @param plat l'entité à convertir
     * @return le DTO correspondant
     */
    public static PlatDTO depuisEntite(Plat plat) {
        return PlatDTO.builder()
                .id(plat.getId())
                .nom(plat.getNom())
                .description(plat.getDescription())
                .prix(plat.getPrix())
                .categorie(plat.getCategorie())
                .imageUrl(plat.getImageUrl())
                .disponible(plat.isDisponible())
                .idRestaurant(plat.getRestaurant() != null ? plat.getRestaurant().getId() : null)
                .build();
    }
}
