package com.restaurants.business.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité représentant une livraison dans la base de données.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "livraison")
public class Livraison {

    /** Identifiant unique de la livraison */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Commande associée à cette livraison */
    @OneToOne
    @JoinColumn(name = "id_commande", nullable = false)
    private Commande commande;

    /** Identifiant du livreur (référence à l'utilisateur dans auth-service) */
    @Column(name = "id_livreur")
    private Long idLivreur;

    /** Adresse de livraison */
    @Column(name = "adresse_livraison", nullable = false)
    private String adresseLivraison;

    /**
     * Statut de la livraison.
     * Valeurs : EN_ATTENTE, EN_COURS, LIVREE, ANNULEE
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutLivraison statut = StatutLivraison.EN_ATTENTE;

    /** Date et heure de création */
    @Column(name = "cree_le", nullable = false)
    @Builder.Default
    private LocalDateTime creeLe = LocalDateTime.now();

    /** Date et heure de livraison effective */
    @Column(name = "livre_le")
    private LocalDateTime livreLe;

    /**
     * Énumération des statuts possibles d'une livraison.
     */
    public enum StatutLivraison {
        EN_ATTENTE,
        EN_COURS,
        LIVREE,
        ANNULEE
    }
}
