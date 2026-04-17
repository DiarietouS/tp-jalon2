package com.restaurants.business.dtos;

import java.math.BigDecimal;

public class LigneCommandeDTO {

    private Long id;
    private Long platId;
    private String nomPlat;
    private int quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal sousTotal;

    public LigneCommandeDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPlatId() { return platId; }
    public void setPlatId(Long platId) { this.platId = platId; }
    public String getNomPlat() { return nomPlat; }
    public void setNomPlat(String nomPlat) { this.nomPlat = nomPlat; }
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    public BigDecimal getSousTotal() { return sousTotal; }
    public void setSousTotal(BigDecimal sousTotal) { this.sousTotal = sousTotal; }
}
