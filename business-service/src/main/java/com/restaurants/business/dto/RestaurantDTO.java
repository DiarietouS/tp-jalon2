package com.restaurants.business.dto;

/**
 * DTO pour les restaurants — miroir du modèle Angular RestaurantModel.
 */
public class RestaurantDTO {

    private Long id;
    private Long idProprietaire;
    private String nom;
    private String adresse;
    private String localisationTexte;
    private String telephone;
    private String courriel;
    private String imageUrl;
    private String typeCuisine;
    private Double note;
    private String tempsLivraison;
    private Double fraisLivraison;
    private Double commandeMinimum;

    public RestaurantDTO() {}

    public RestaurantDTO(Long id, Long idProprietaire, String nom, String adresse,
                         String localisationTexte, String telephone, String courriel,
                         String imageUrl, String typeCuisine, Double note,
                         String tempsLivraison, Double fraisLivraison, Double commandeMinimum) {
        this.id = id;
        this.idProprietaire = idProprietaire;
        this.nom = nom;
        this.adresse = adresse;
        this.localisationTexte = localisationTexte;
        this.telephone = telephone;
        this.courriel = courriel;
        this.imageUrl = imageUrl;
        this.typeCuisine = typeCuisine;
        this.note = note;
        this.tempsLivraison = tempsLivraison;
        this.fraisLivraison = fraisLivraison;
        this.commandeMinimum = commandeMinimum;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdProprietaire() { return idProprietaire; }
    public void setIdProprietaire(Long idProprietaire) { this.idProprietaire = idProprietaire; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getLocalisationTexte() { return localisationTexte; }
    public void setLocalisationTexte(String localisationTexte) { this.localisationTexte = localisationTexte; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getCourriel() { return courriel; }
    public void setCourriel(String courriel) { this.courriel = courriel; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getTypeCuisine() { return typeCuisine; }
    public void setTypeCuisine(String typeCuisine) { this.typeCuisine = typeCuisine; }

    public Double getNote() { return note; }
    public void setNote(Double note) { this.note = note; }

    public String getTempsLivraison() { return tempsLivraison; }
    public void setTempsLivraison(String tempsLivraison) { this.tempsLivraison = tempsLivraison; }

    public Double getFraisLivraison() { return fraisLivraison; }
    public void setFraisLivraison(Double fraisLivraison) { this.fraisLivraison = fraisLivraison; }

    public Double getCommandeMinimum() { return commandeMinimum; }
    public void setCommandeMinimum(Double commandeMinimum) { this.commandeMinimum = commandeMinimum; }
}
