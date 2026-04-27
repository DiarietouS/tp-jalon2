package com.restaurants.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Token de réinitialisation de mot de passe.
 * Stocké dans "jetons_reinitialisation", valide 1 heure.
 */
@Entity
@Table(name = "jetons_reinitialisation")
public class JetonReinitPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String courriel;

    @Column(nullable = false, unique = true)
    private String jeton;

    @Column(name = "expire_le", nullable = false)
    private LocalDateTime expireLe;

    @Column(nullable = false)
    private boolean utilise = false;

    public JetonReinitPassword() {}

    public JetonReinitPassword(String courriel, String jeton, LocalDateTime expireLe) {
        this.courriel = courriel;
        this.jeton = jeton;
        this.expireLe = expireLe;
    }

    public boolean estExpire() {
        return LocalDateTime.now().isAfter(expireLe);
    }

    public Long getId() { return id; }
    public String getCourriel() { return courriel; }
    public String getJeton() { return jeton; }
    public LocalDateTime getExpireLe() { return expireLe; }
    public boolean isUtilise() { return utilise; }
    public void setUtilise(boolean utilise) { this.utilise = utilise; }
}
