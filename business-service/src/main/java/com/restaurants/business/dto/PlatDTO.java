package com.restaurants.business.dto;

/**
 * DTO pour les plats — miroir du modèle Angular Plat.
 */
public class PlatDTO {

    private Long id;
    private String nom;
    private String description;
    private Double prix;
    private String categorie;
    private String imageUrl;
    private boolean disponible;
    private Long idRestaurant;

    public PlatDTO() {}

    public PlatDTO(Long id, String nom, String description, Double prix, String categorie,
                   String imageUrl, boolean disponible, Long idRestaurant) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.categorie = categorie;
        this.imageUrl = imageUrl;
        this.disponible = disponible;
        this.idRestaurant = idRestaurant;
    }

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
