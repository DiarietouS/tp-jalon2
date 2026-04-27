package com.restaurants.business.dto;

import java.util.ArrayList;
import java.util.List;

public class CommandeDTO {
    private Long id;
    private Long idClient;
    private Long idRestaurant;
    private String nomRestaurant;
    private List<ArticleCommandeDTO> articles = new ArrayList<>();
    private String statut;
    private Double sousTotal;
    private Double fraisLivraison;
    private Double total;
    private String adresseLivraison;
    private String telephone;
    private String creeLe;
    private String livraisonEstimee;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdClient() { return idClient; }
    public void setIdClient(Long idClient) { this.idClient = idClient; }

    public Long getIdRestaurant() { return idRestaurant; }
    public void setIdRestaurant(Long idRestaurant) { this.idRestaurant = idRestaurant; }

    public String getNomRestaurant() { return nomRestaurant; }
    public void setNomRestaurant(String nomRestaurant) { this.nomRestaurant = nomRestaurant; }

    public List<ArticleCommandeDTO> getArticles() { return articles; }
    public void setArticles(List<ArticleCommandeDTO> articles) { this.articles = articles; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Double getSousTotal() { return sousTotal; }
    public void setSousTotal(Double sousTotal) { this.sousTotal = sousTotal; }

    public Double getFraisLivraison() { return fraisLivraison; }
    public void setFraisLivraison(Double fraisLivraison) { this.fraisLivraison = fraisLivraison; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getAdresseLivraison() { return adresseLivraison; }
    public void setAdresseLivraison(String adresseLivraison) { this.adresseLivraison = adresseLivraison; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getCreeLe() { return creeLe; }
    public void setCreeLe(String creeLe) { this.creeLe = creeLe; }

    public String getLivraisonEstimee() { return livraisonEstimee; }
    public void setLivraisonEstimee(String livraisonEstimee) { this.livraisonEstimee = livraisonEstimee; }
}
