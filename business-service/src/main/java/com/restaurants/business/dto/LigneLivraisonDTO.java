package com.restaurants.business.dto;

public class LigneLivraisonDTO {
    private Long numPlat;
    private String libelle;
    private Integer quantite;
    private Double prixUnitaire;

    public Long getNumPlat() { return numPlat; }
    public void setNumPlat(Long numPlat) { this.numPlat = numPlat; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }

    public Double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire = prixUnitaire; }
}
