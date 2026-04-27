package com.restaurants.auth.dto;

public class AuthResponseDTO {
    private String token;
    private Long id;
    private String courriel;
    private String prenom;
    private String nom;
    private String role;

    public AuthResponseDTO(String token, Long id, String courriel,
                           String prenom, String nom, String role) {
        this.token = token;
        this.id = id;
        this.courriel = courriel;
        this.prenom = prenom;
        this.nom = nom;
        this.role = role;
    }

    public String getToken() { return token; }
    public Long getId() { return id; }
    public String getCourriel() { return courriel; }
    public String getPrenom() { return prenom; }
    public String getNom() { return nom; }
    public String getRole() { return role; }
}
