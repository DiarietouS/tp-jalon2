package com.restaurants.business.entity;

import jakarta.persistence.*;

/**
 * Entité JPA pour la table "plats".
 * Champs alignés sur le modèle Angular Plat.
 */
@Entity
@Table(name = "plats")
public class PlatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String description;

    @Column(nullable = false)
    private Double prix;

    private String categorie;

    @Column(name = "image_url")
    private String imageUrl;

    private boolean disponible = true;

    @Column(name = "id_restaurant", nullable = false)
    private Long idRestaurant;

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public Long getIdRestaurant() { return idRestaurant; }
    public void setIdRestaurant(Long idRestaurant) { this.idRestaurant = idRestaurant; }
}
