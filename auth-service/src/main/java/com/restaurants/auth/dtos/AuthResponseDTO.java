package com.restaurants.auth.dtos;

public class AuthResponseDTO {

    private String token;
    private String typeToken;
    private Long id;
    private String courriel;
    private String prenom;
    private String nom;
    private String role;

    public AuthResponseDTO() {
        this.typeToken = "Bearer";
    }

    public AuthResponseDTO(String token, Long id, String courriel, String prenom, String nom, String role) {
        this.token = token;
        this.typeToken = "Bearer";
        this.id = id;
        this.courriel = courriel;
        this.prenom = prenom;
        this.nom = nom;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTypeToken() { return typeToken; }
    public void setTypeToken(String typeToken) { this.typeToken = typeToken; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCourriel() { return courriel; }
    public void setCourriel(String courriel) { this.courriel = courriel; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
