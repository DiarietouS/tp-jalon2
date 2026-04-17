package com.restaurants.business.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CommandeDTO {

    private Long id;
    private String courrielClient;
    private String adresseLivraison;
    private LocalDateTime dateCommande;
    private String statut;
    private BigDecimal montantTotal;
    private Long restaurantId;
    private List<LigneCommandeDTO> lignesCommande;

    public CommandeDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCourrielClient() { return courrielClient; }
    public void setCourrielClient(String courrielClient) { this.courrielClient = courrielClient; }
    public String getAdresseLivraison() { return adresseLivraison; }
    public void setAdresseLivraison(String adresseLivraison) { this.adresseLivraison = adresseLivraison; }
    public LocalDateTime getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDateTime dateCommande) { this.dateCommande = dateCommande; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public BigDecimal getMontantTotal() { return montantTotal; }
    public void setMontantTotal(BigDecimal montantTotal) { this.montantTotal = montantTotal; }
    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
    public List<LigneCommandeDTO> getLignesCommande() { return lignesCommande; }
    public void setLignesCommande(List<LigneCommandeDTO> lignesCommande) { this.lignesCommande = lignesCommande; }
}
