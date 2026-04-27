package com.restaurants.auth.dto;

public class SignUpRequestDTO {
    private String prenom;
    private String nom;
    private String courriel;
    private String motDePasse;
    private String telephone;
    private String adresse;
    private String role;

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
