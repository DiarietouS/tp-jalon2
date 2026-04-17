package com.restaurants.business.dtos;

import com.restaurants.business.models.Livraison;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO représentant une livraison.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LivraisonDTO {

    /** Identifiant unique de la livraison */
    private Long id;

    /** Identifiant de la commande */
    private Long idCommande;

    /** Identifiant du livreur */
    private Long idLivreur;

    /** Adresse de livraison */
    private String adresseLivraison;

    /** Statut de la livraison */
    private Livraison.StatutLivraison statut;

    /** Date de création */
    private LocalDateTime creeLe;

    /** Date de livraison effective */
    private LocalDateTime livreLe;

    /**
     * Convertit une entité Livraison en DTO.
     *
     * @param livraison l'entité à convertir
     * @return le DTO correspondant
     */
    public static LivraisonDTO depuisEntite(Livraison livraison) {
        return LivraisonDTO.builder()
                .id(livraison.getId())
                .idCommande(livraison.getCommande() != null ? livraison.getCommande().getId() : null)
                .idLivreur(livraison.getIdLivreur())
                .adresseLivraison(livraison.getAdresseLivraison())
                .statut(livraison.getStatut())
                .creeLe(livraison.getCreeLe())
                .livreLe(livraison.getLivreLe())
                .build();
    }
}
