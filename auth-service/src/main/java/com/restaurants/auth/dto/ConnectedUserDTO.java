package com.restaurants.auth.dto;

import com.restaurants.auth.entity.UtilisateurEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * DTO qui implémente UserDetails pour Spring Security.
 * Joue le rôle d'adaptateur entre l'entité JPA et le système d'auth.
 */
public class ConnectedUserDTO implements UserDetails {

    private final Long id;
    private final String courriel;
    private final String motDePasse;
    private final String prenom;
    private final String nom;
    private final String role;
    private final Collection<? extends GrantedAuthority> authorities;

    public ConnectedUserDTO(UtilisateurEntity entity) {
        this.id = entity.getId();
        this.courriel = entity.getCourriel();
        this.motDePasse = entity.getMotDePasse();
        this.prenom = entity.getPrenom();
        this.nom = entity.getNom();
        this.role = entity.getRole();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + entity.getRole().toUpperCase()));
    }

    @Override
    public String getUsername() { return courriel; }

    @Override
    public String getPassword() { return motDePasse; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public Long getId() { return id; }
    public String getPrenom() { return prenom; }
    public String getNom() { return nom; }
    public String getRole() { return role; }
}
