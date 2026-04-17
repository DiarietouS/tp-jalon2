package com.restaurants.business.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un restaurant dans la base de données.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "restaurant")
public class Restaurant {

    /** Identifiant unique du restaurant */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom du restaurant */
    @Column(nullable = false)
    private String nom;

    /** Description du restaurant */
    private String description;

    /** Adresse du restaurant */
    @Column(nullable = false)
    private String adresse;

    /** Texte de localisation (ex: "Plateau-Mont-Royal, Montréal") */
    private String localisationTexte;

    /** Numéro de téléphone */
    private String telephone;

    /** Adresse courriel */
    private String courriel;

    /** URL de l'image du restaurant */
    private String imageUrl;

    /** Type de cuisine (ex: "Italienne", "Burger", "Sushi") */
    private String typeCuisine;

    /** Note moyenne (sur 5) */
    private Double note;

    /** Temps de livraison estimé en minutes */
    private String tempsLivraison;

    /** Frais de livraison */
    private Double fraisLivraison;

    /** Commande minimum */
    private Double commandeMinimum;

    /** Identifiant du propriétaire (référence à l'utilisateur dans auth-service) */
    private Long idProprietaire;

    /** Liste des plats du restaurant */
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Plat> plats = new ArrayList<>();
}
