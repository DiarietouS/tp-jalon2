package com.restaurants.auth.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "utilisateurs")
public class UtilisateurEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true)
    private String courriel;

    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @Column
    private String telephone;

    @Column
    private String adresse;

    @Column(nullable = false)
    private String role = "client";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCourriel() { return courriel; }
    public void setCourriel(String courriel) { this.courriel = courriel; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
