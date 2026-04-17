package com.restaurants.business.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une commande dans la base de données.
 *
 * Statuts possibles :
 * EN_ATTENTE → CONFIRMEE → EN_PREPARATION → PRETE → EN_LIVRAISON → LIVREE
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "commande")
public class Commande {

    /** Identifiant unique de la commande */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identifiant du client (référence à l'utilisateur dans auth-service) */
    @Column(name = "id_client", nullable = false)
    private Long idClient;

    /** Identifiant du restaurant */
    @Column(name = "id_restaurant", nullable = false)
    private Long idRestaurant;

    /** Nom du restaurant au moment de la commande */
    @Column(name = "nom_restaurant")
    private String nomRestaurant;

    /**
     * Statut de la commande.
     * Valeurs : EN_ATTENTE, CONFIRMEE, EN_PREPARATION, PRETE, EN_LIVRAISON, LIVREE, ANNULEE
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutCommande statut = StatutCommande.EN_ATTENTE;

    /** Sous-total (avant frais de livraison) */
    @Column(name = "sous_total", nullable = false)
    private BigDecimal sousTotal;

    /** Frais de livraison */
    @Column(name = "frais_livraison", nullable = false)
    private BigDecimal fraisLivraison;

    /** Total de la commande */
    @Column(nullable = false)
    private BigDecimal total;

    /** Adresse de livraison */
    @Column(name = "adresse_livraison")
    private String adresseLivraison;

    /** Numéro de téléphone pour la livraison */
    private String telephone;

    /** Date et heure de création de la commande */
    @Column(name = "cree_le", nullable = false)
    @Builder.Default
    private LocalDateTime creeLe = LocalDateTime.now();

    /** Date et heure estimée de livraison */
    @Column(name = "livraison_estimee")
    private LocalDateTime livraisonEstimee;

    /** Lignes de la commande (plats commandés) */
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LigneCommande> lignes = new ArrayList<>();

    /**
     * Énumération des statuts possibles d'une commande.
     */
    public enum StatutCommande {
        EN_ATTENTE,
        CONFIRMEE,
        EN_PREPARATION,
        PRETE,
        EN_LIVRAISON,
        LIVREE,
        ANNULEE
    }
}
