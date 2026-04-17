package com.restaurants.business.repositories;

import com.restaurants.business.models.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'accès aux données des commandes.
 */
@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    /**
     * Recherche les commandes d'un client.
     *
     * @param idClient l'identifiant du client
     * @return la liste des commandes
     */
    List<Commande> findByIdClientOrderByCreeLe_Desc(Long idClient);

    /**
     * Recherche les commandes d'un restaurant.
     *
     * @param idRestaurant l'identifiant du restaurant
     * @return la liste des commandes
     */
    List<Commande> findByIdRestaurantOrderByCreeLe_Desc(Long idRestaurant);

    /**
     * Recherche les commandes par statut.
     *
     * @param statut le statut de la commande
     * @return la liste des commandes
     */
    List<Commande> findByStatut(Commande.StatutCommande statut);
}
