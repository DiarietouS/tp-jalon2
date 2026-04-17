package com.restaurants.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ConnexionRequestDTO {

    @NotBlank(message = "Le courriel est obligatoire")
    @Email(message = "Le courriel doit être valide")
    private String courriel;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;

    public ConnexionRequestDTO() {}

    public String getCourriel() { return courriel; }
    public void setCourriel(String courriel) { this.courriel = courriel; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
}
