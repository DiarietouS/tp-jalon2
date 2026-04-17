package com.restaurants.business.repositories;

import com.restaurants.business.models.Plat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'accès aux données des plats.
 */
@Repository
public interface PlatRepository extends JpaRepository<Plat, Long> {

    /**
     * Recherche les plats d'un restaurant.
     *
     * @param idRestaurant l'identifiant du restaurant
     * @return la liste des plats
     */
    List<Plat> findByRestaurantId(Long idRestaurant);

    /**
     * Recherche les plats disponibles d'un restaurant.
     *
     * @param idRestaurant l'identifiant du restaurant
     * @return la liste des plats disponibles
     */
    List<Plat> findByRestaurantIdAndDisponibleTrue(Long idRestaurant);

    /**
     * Recherche les plats par catégorie dans un restaurant.
     *
     * @param idRestaurant l'identifiant du restaurant
     * @param categorie    la catégorie
     * @return la liste des plats
     */
    List<Plat> findByRestaurantIdAndCategorieIgnoreCase(Long idRestaurant, String categorie);
}
