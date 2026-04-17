package com.restaurants.business.repositories;

import com.restaurants.business.models.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'accès aux données des livraisons.
 */
@Repository
public interface LivraisonRepository extends JpaRepository<Livraison, Long> {

    /**
     * Recherche les livraisons d'un livreur.
     *
     * @param idLivreur l'identifiant du livreur
     * @return la liste des livraisons
     */
    List<Livraison> findByIdLivreur(Long idLivreur);

    /**
     * Recherche la livraison d'une commande.
     *
     * @param idCommande l'identifiant de la commande
     * @return la livraison si trouvée
     */
    Optional<Livraison> findByCommandeId(Long idCommande);

    /**
     * Recherche les livraisons par statut.
     *
     * @param statut le statut de la livraison
     * @return la liste des livraisons
     */
    List<Livraison> findByStatut(Livraison.StatutLivraison statut);
}
