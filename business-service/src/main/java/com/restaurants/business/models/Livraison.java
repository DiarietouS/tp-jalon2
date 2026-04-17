package com.restaurants.business.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "livraison")
public class Livraison {

    public enum StatutLivraison {
        EN_ATTENTE, ASSIGNEE, EN_COURS, LIVREE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false, unique = true)
    private Commande commande;

    @Column(name = "courriel_livreur")
    private String courrielLivreur;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutLivraison statut;

    @Column(name = "date_assignation")
    private LocalDateTime dateAssignation;

    @Column(name = "date_livraison")
    private LocalDateTime dateLivraison;

    @Column(name = "notes")
    private String notes;

    public Livraison() {
        this.statut = StatutLivraison.EN_ATTENTE;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Commande getCommande() { return commande; }
    public void setCommande(Commande commande) { this.commande = commande; }
    public String getCourrielLivreur() { return courrielLivreur; }
    public void setCourrielLivreur(String courrielLivreur) { this.courrielLivreur = courrielLivreur; }
    public StatutLivraison getStatut() { return statut; }
    public void setStatut(StatutLivraison statut) { this.statut = statut; }
    public LocalDateTime getDateAssignation() { return dateAssignation; }
    public void setDateAssignation(LocalDateTime dateAssignation) { this.dateAssignation = dateAssignation; }
    public LocalDateTime getDateLivraison() { return dateLivraison; }
    public void setDateLivraison(LocalDateTime dateLivraison) { this.dateLivraison = dateLivraison; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
