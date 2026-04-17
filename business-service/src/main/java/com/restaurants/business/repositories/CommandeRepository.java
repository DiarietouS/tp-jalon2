package com.restaurants.business.repositories;

import com.restaurants.business.models.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {
    List<Commande> findByCourrielClient(String courrielClient);
    List<Commande> findByRestaurantId(Long restaurantId);
    List<Commande> findByStatut(Commande.StatutCommande statut);
}
