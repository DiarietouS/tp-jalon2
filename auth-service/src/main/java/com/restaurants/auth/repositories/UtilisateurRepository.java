package com.restaurants.auth.repositories;

import com.restaurants.auth.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByCourriel(String courriel);
    boolean existsByCourriel(String courriel);
}
