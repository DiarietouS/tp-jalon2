package com.restaurants.auth.repositories;

import com.restaurants.auth.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour l'accès aux données des utilisateurs.
 * Fournit les opérations CRUD standard plus des méthodes personnalisées.
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    /**
     * Recherche un utilisateur par son adresse courriel.
     *
     * @param courriel l'adresse courriel de l'utilisateur
     * @return un Optional contenant l'utilisateur si trouvé
     */
    Optional<Utilisateur> findByCourriel(String courriel);

    /**
     * Vérifie si un utilisateur existe avec l'adresse courriel donnée.
     *
     * @param courriel l'adresse courriel à vérifier
     * @return true si un utilisateur avec ce courriel existe
     */
    boolean existsByCourriel(String courriel);
}
