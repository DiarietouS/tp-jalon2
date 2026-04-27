package com.restaurants.business.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "commandes")
public class CommandeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_client", nullable = false)
    private Long idClient;

    @Column(name = "id_restaurant", nullable = false)
    private Long idRestaurant;

    @Column(name = "nom_restaurant")
    private String nomRestaurant;

    @Lob
    @Column(name = "articles_json", nullable = false)
    private String articlesJson;

    @Column(nullable = false)
    private String statut;

    @Column(name = "sous_total", nullable = false)
    private Double sousTotal;

    @Column(name = "frais_livraison", nullable = false)
    private Double fraisLivraison;

    @Column(nullable = false)
    private Double total;

    @Column(name = "adresse_livraison")
    private String adresseLivraison;

    private String telephone;

    @Column(name = "cree_le", nullable = false)
    private String creeLe;

    @Column(name = "livraison_estimee")
    private String livraisonEstimee;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdClient() { return idClient; }
    public void setIdClient(Long idClient) { this.idClient = idClient; }

    public Long getIdRestaurant() { return idRestaurant; }
    public void setIdRestaurant(Long idRestaurant) { this.idRestaurant = idRestaurant; }

    public String getNomRestaurant() { return nomRestaurant; }
    public void setNomRestaurant(String nomRestaurant) { this.nomRestaurant = nomRestaurant; }

    public String getArticlesJson() { return articlesJson; }
    public void setArticlesJson(String articlesJson) { this.articlesJson = articlesJson; }

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
