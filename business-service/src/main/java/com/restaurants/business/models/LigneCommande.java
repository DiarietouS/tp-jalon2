package com.restaurants.business.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entité représentant une ligne de commande (un plat dans une commande).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ligne_commande")
public class LigneCommande {

    /** Identifiant unique de la ligne */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Commande parente */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_commande", nullable = false)
    private Commande commande;

    /** Identifiant du plat commandé */
    @Column(name = "id_plat", nullable = false)
    private Long idPlat;

    /** Nom du plat au moment de la commande */
    @Column(name = "nom_plat", nullable = false)
    private String nomPlat;

    /** Quantité commandée */
    @Column(nullable = false)
    private Integer quantite;

    /** Prix unitaire au moment de la commande */
    @Column(name = "prix_unitaire", nullable = false)
    private BigDecimal prixUnitaire;
}
