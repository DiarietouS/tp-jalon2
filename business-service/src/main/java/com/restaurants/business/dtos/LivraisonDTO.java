package com.restaurants.business.dtos;

import java.time.LocalDateTime;

public class LivraisonDTO {

    private Long id;
    private Long commandeId;
    private String courrielLivreur;
    private String statut;
    private LocalDateTime dateAssignation;
    private LocalDateTime dateLivraison;
    private String notes;

    public LivraisonDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCommandeId() { return commandeId; }
    public void setCommandeId(Long commandeId) { this.commandeId = commandeId; }
    public String getCourrielLivreur() { return courrielLivreur; }
    public void setCourrielLivreur(String courrielLivreur) { this.courrielLivreur = courrielLivreur; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDateTime getDateAssignation() { return dateAssignation; }
    public void setDateAssignation(LocalDateTime dateAssignation) { this.dateAssignation = dateAssignation; }
    public LocalDateTime getDateLivraison() { return dateLivraison; }
    public void setDateLivraison(LocalDateTime dateLivraison) { this.dateLivraison = dateLivraison; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
