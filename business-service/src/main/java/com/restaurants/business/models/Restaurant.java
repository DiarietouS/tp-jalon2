package com.restaurants.business.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurant")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "description")
    private String description;

    @Column(name = "adresse")
    private String adresse;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "type_cuisine")
    private String typeCuisine;

    @Column(name = "frais_livraison", precision = 10, scale = 2)
    private BigDecimal fraisLivraison;

    @Column(name = "commande_minimum", precision = 10, scale = 2)
    private BigDecimal commandeMinimum;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Plat> plats = new ArrayList<>();

    public Restaurant() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getTypeCuisine() { return typeCuisine; }
    public void setTypeCuisine(String typeCuisine) { this.typeCuisine = typeCuisine; }
    public BigDecimal getFraisLivraison() { return fraisLivraison; }
    public void setFraisLivraison(BigDecimal fraisLivraison) { this.fraisLivraison = fraisLivraison; }
    public BigDecimal getCommandeMinimum() { return commandeMinimum; }
    public void setCommandeMinimum(BigDecimal commandeMinimum) { this.commandeMinimum = commandeMinimum; }
    public List<Plat> getPlats() { return plats; }
    public void setPlats(List<Plat> plats) { this.plats = plats; }
}
