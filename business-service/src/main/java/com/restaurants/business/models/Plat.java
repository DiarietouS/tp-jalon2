package com.restaurants.business.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité représentant un plat dans la base de données.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "plat")
public class Plat {

    /** Identifiant unique du plat */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom du plat */
    @Column(nullable = false)
    private String nom;

    /** Description du plat */
    private String description;

    /** Prix du plat */
    @Column(nullable = false)
    private Double prix;

    /** Catégorie du plat (ex: "Pizza", "Burger", "Dessert") */
    private String categorie;

    /** URL de l'image du plat */
    private String imageUrl;

    /** Disponibilité du plat */
    @Column(nullable = false)
    private boolean disponible;

    /** Restaurant auquel appartient ce plat */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_restaurant", nullable = false)
    private Restaurant restaurant;
}
