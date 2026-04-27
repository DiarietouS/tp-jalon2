package com.restaurants.business.dto;

public class ArticleCommandeDTO {
    private Long idPlat;
    private String nomPlat;
    private Integer quantite;
    private Double prixUnitaire;

    public Long getIdPlat() { return idPlat; }
    public void setIdPlat(Long idPlat) { this.idPlat = idPlat; }

    public String getNomPlat() { return nomPlat; }
    public void setNomPlat(String nomPlat) { this.nomPlat = nomPlat; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }

    public Double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire = prixUnitaire; }
}
