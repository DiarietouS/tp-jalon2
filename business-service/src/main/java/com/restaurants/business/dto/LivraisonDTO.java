package com.restaurants.business.dto;

import java.util.ArrayList;
import java.util.List;

public class LivraisonDTO {
    private Long id;
    private String client;
    private String adresse;
    private String telephone;
    private String statut;
    private String creeLe;
    private List<LigneLivraisonDTO> lignes = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClient() { return client; }
    public void setClient(String client) { this.client = client; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getCreeLe() { return creeLe; }
    public void setCreeLe(String creeLe) { this.creeLe = creeLe; }

    public List<LigneLivraisonDTO> getLignes() { return lignes; }
    public void setLignes(List<LigneLivraisonDTO> lignes) { this.lignes = lignes; }
}
