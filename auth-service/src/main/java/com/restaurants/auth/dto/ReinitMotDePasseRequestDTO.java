package com.restaurants.auth.dto;

public class ReinitMotDePasseRequestDTO {
    private String jeton;
    private String nouveauMotDePasse;

    public ReinitMotDePasseRequestDTO() {}
    public String getJeton() { return jeton; }
    public void setJeton(String jeton) { this.jeton = jeton; }
    public String getNouveauMotDePasse() { return nouveauMotDePasse; }
    public void setNouveauMotDePasse(String nouveauMotDePasse) { this.nouveauMotDePasse = nouveauMotDePasse; }
}
