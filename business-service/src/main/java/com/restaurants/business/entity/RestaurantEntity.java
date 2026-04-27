package com.restaurants.business.entity;

import jakarta.persistence.*;

/**
 * Entité JPA pour la table "restaurants".
 * Champs alignés sur le modèle Angular RestaurantModel.
 */
@Entity
@Table(name = "restaurants")
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_proprietaire", nullable = false)
    private Long idProprietaire;

    @Column(nullable = false)
    private String nom;

    private String adresse;

    @Column(name = "localisation_texte")
    private String localisationTexte;

    private String telephone;
    private String courriel;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "type_cuisine")
    private String typeCuisine;

    private Double note = 0.0;

    @Column(name = "temps_livraison")
    private String tempsLivraison;

    @Column(name = "frais_livraison")
    private Double fraisLivraison = 0.0;

    @Column(name = "commande_minimum")
    private Double commandeMinimum = 0.0;

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdProprietaire() { return idProprietaire; }
    public void setIdProprietaire(Long idProprietaire) { this.idProprietaire = idProprietaire; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getLocalisationTexte() { return localisationTexte; }
    public void setLocalisationTexte(String localisationTexte) { this.localisationTexte = localisationTexte; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getCourriel() { return courriel; }
    public void setCourriel(String courriel) { this.courriel = courriel; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getTypeCuisine() { return typeCuisine; }
    public void setTypeCuisine(String typeCuisine) { this.typeCuisine = typeCuisine; }

    public Double getNote() { return note; }
    public void setNote(Double note) { this.note = note; }

    public String getTempsLivraison() { return tempsLivraison; }
    public void setTempsLivraison(String tempsLivraison) { this.tempsLivraison = tempsLivraison; }

    public Double getFraisLivraison() { return fraisLivraison; }
    public void setFraisLivraison(Double fraisLivraison) { this.fraisLivraison = fraisLivraison; }

    public Double getCommandeMinimum() { return commandeMinimum; }
    public void setCommandeMinimum(Double commandeMinimum) { this.commandeMinimum = commandeMinimum; }
}
