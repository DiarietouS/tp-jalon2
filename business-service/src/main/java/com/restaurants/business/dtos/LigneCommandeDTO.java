package com.restaurants.business.dtos;

import com.restaurants.business.models.LigneCommande;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO représentant une ligne de commande.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LigneCommandeDTO {

    /** Identifiant de la ligne */
    private Long id;

    /** Identifiant du plat */
    private Long idPlat;

    /** Nom du plat */
    private String nomPlat;

    /** Quantité */
    private Integer quantite;

    /** Prix unitaire */
    private BigDecimal prixUnitaire;

    /**
     * Convertit une entité LigneCommande en DTO.
     *
     * @param ligne l'entité à convertir
     * @return le DTO correspondant
     */
    public static LigneCommandeDTO depuisEntite(LigneCommande ligne) {
        return LigneCommandeDTO.builder()
                .id(ligne.getId())
                .idPlat(ligne.getIdPlat())
                .nomPlat(ligne.getNomPlat())
                .quantite(ligne.getQuantite())
                .prixUnitaire(ligne.getPrixUnitaire())
                .build();
    }
}
