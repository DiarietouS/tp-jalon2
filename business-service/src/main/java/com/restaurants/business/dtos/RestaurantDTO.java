package com.restaurants.business.dtos;

import java.math.BigDecimal;

public class RestaurantDTO {

    private Long id;
    private String nom;
    private String description;
    private String adresse;
    private String telephone;
    private String typeCuisine;
    private BigDecimal fraisLivraison;
    private BigDecimal commandeMinimum;

    public RestaurantDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getTypeCuisine() { return typeCuisine; }
    public void setTypeCuisine(String typeCuisine) { this.typeCuisine = typeCuisine; }
    public BigDecimal getFraisLivraison() { return fraisLivraison; }
    public void setFraisLivraison(BigDecimal fraisLivraison) { this.fraisLivraison = fraisLivraison; }
    public BigDecimal getCommandeMinimum() { return commandeMinimum; }
    public void setCommandeMinimum(BigDecimal commandeMinimum) { this.commandeMinimum = commandeMinimum; }
}
