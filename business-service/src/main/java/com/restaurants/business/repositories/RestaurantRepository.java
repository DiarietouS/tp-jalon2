package com.restaurants.business.repositories;

import com.restaurants.business.models.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'accès aux données des restaurants.
 */
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    /**
     * Recherche les restaurants d'un propriétaire.
     *
     * @param idProprietaire l'identifiant du propriétaire
     * @return la liste des restaurants
     */
    List<Restaurant> findByIdProprietaire(Long idProprietaire);

    /**
     * Recherche les restaurants par type de cuisine.
     *
     * @param typeCuisine le type de cuisine
     * @return la liste des restaurants
     */
    List<Restaurant> findByTypeCuisineIgnoreCase(String typeCuisine);

    /**
     * Recherche les restaurants dont le nom contient le terme de recherche.
     *
     * @param nom le terme de recherche
     * @return la liste des restaurants
     */
    List<Restaurant> findByNomContainingIgnoreCase(String nom);
}
