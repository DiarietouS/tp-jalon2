package com.restaurants.business.repositories;

import com.restaurants.business.models.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivraisonRepository extends JpaRepository<Livraison, Long> {
    Optional<Livraison> findByCommandeId(Long commandeId);
    List<Livraison> findByCourrielLivreur(String courrielLivreur);
    List<Livraison> findByStatut(Livraison.StatutLivraison statut);
}
