package com.restaurants.business.dtos;

import com.restaurants.business.models.Commande;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO représentant une commande.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandeDTO {

    /** Identifiant unique de la commande */
    private Long id;

    /** Identifiant du client */
    private Long idClient;

    /** Identifiant du restaurant */
    private Long idRestaurant;

    /** Nom du restaurant */
    private String nomRestaurant;

    /** Statut de la commande */
    private Commande.StatutCommande statut;

    /** Sous-total */
    private BigDecimal sousTotal;

    /** Frais de livraison */
    private BigDecimal fraisLivraison;

    /** Total */
    private BigDecimal total;

    /** Adresse de livraison */
    private String adresseLivraison;

    /** Téléphone */
    private String telephone;

    /** Date de création */
    private LocalDateTime creeLe;

    /** Date de livraison estimée */
    private LocalDateTime livraisonEstimee;

    /** Lignes de la commande */
    private List<LigneCommandeDTO> lignes;

    /**
     * Convertit une entité Commande en DTO.
     *
     * @param commande l'entité à convertir
     * @return le DTO correspondant
     */
    public static CommandeDTO depuisEntite(Commande commande) {
        List<LigneCommandeDTO> lignesDTO = commande.getLignes() != null
                ? commande.getLignes().stream()
                    .map(LigneCommandeDTO::depuisEntite)
                    .collect(Collectors.toList())
                : List.of();

        return CommandeDTO.builder()
                .id(commande.getId())
                .idClient(commande.getIdClient())
                .idRestaurant(commande.getIdRestaurant())
                .nomRestaurant(commande.getNomRestaurant())
                .statut(commande.getStatut())
                .sousTotal(commande.getSousTotal())
                .fraisLivraison(commande.getFraisLivraison())
                .total(commande.getTotal())
                .adresseLivraison(commande.getAdresseLivraison())
                .telephone(commande.getTelephone())
                .creeLe(commande.getCreeLe())
                .livraisonEstimee(commande.getLivraisonEstimee())
                .lignes(lignesDTO)
                .build();
    }
}
