package com.restaurants.auth.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Entité représentant un utilisateur dans la base de données.
 * Implémente UserDetails pour intégration avec Spring Security.
 *
 * Rôles disponibles :
 * - CLIENT : utilisateur qui passe des commandes
 * - RESTAURATEUR : propriétaire d'un restaurant
 * - LIVREUR : personne qui effectue les livraisons
 * - ADMIN : administrateur du système
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "utilisateur")
public class Utilisateur implements UserDetails {

    /** Identifiant unique de l'utilisateur */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Prénom de l'utilisateur */
    @Column(nullable = false)
    private String prenom;

    /** Nom de famille de l'utilisateur */
    @Column(nullable = false)
    private String nom;

    /** Adresse courriel unique (utilisée comme identifiant de connexion) */
    @Column(nullable = false, unique = true)
    private String courriel;

    /** Mot de passe chiffré avec BCrypt */
    @Column(nullable = false)
    private String motDePasse;

    /** Numéro de téléphone */
    private String telephone;

    /** Adresse postale */
    private String adresse;

    /**
     * Rôle de l'utilisateur dans le système.
     * Valeurs possibles : CLIENT, RESTAURATEUR, LIVREUR, ADMIN
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleUtilisateur role;

    /**
     * Retourne les autorités accordées à l'utilisateur.
     * Le rôle est préfixé avec "ROLE_" selon la convention Spring Security.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /** Retourne le mot de passe chiffré */
    @Override
    public String getPassword() {
        return motDePasse;
    }

    /** Retourne le courriel comme identifiant de connexion */
    @Override
    public String getUsername() {
        return courriel;
    }

    /** Le compte n'expire jamais */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /** Le compte n'est jamais verrouillé */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /** Les informations d'identification n'expirent jamais */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** Le compte est toujours activé */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Énumération des rôles possibles dans le système.
     */
    public enum RoleUtilisateur {
        CLIENT,
        RESTAURATEUR,
        LIVREUR,
        ADMIN
    }
}
