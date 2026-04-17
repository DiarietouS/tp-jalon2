package com.restaurants.business.services;

import com.restaurants.business.dtos.LivraisonDTO;
import com.restaurants.business.exceptions.BusinessException;
import com.restaurants.business.models.Commande;
import com.restaurants.business.models.Livraison;
import com.restaurants.business.repositories.CommandeRepository;
import com.restaurants.business.repositories.LivraisonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des livraisons.
 */
@Service
@Transactional
public class LivraisonService {

    private final LivraisonRepository livraisonRepository;
    private final CommandeRepository commandeRepository;

    public LivraisonService(
            LivraisonRepository livraisonRepository,
            CommandeRepository commandeRepository
    ) {
        this.livraisonRepository = livraisonRepository;
        this.commandeRepository = commandeRepository;
    }

    /**
     * Récupère toutes les livraisons d'un livreur.
     *
     * @param idLivreur l'identifiant du livreur
     * @return la liste des livraisons
     */
    @Transactional(readOnly = true)
    public List<LivraisonDTO> obtenirLivraisonsParLivreur(Long idLivreur) {
        return livraisonRepository.findByIdLivreur(idLivreur)
                .stream()
                .map(LivraisonDTO::depuisEntite)
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les livraisons en attente.
     *
     * @return la liste des livraisons en attente
     */
    @Transactional(readOnly = true)
    public List<LivraisonDTO> obtenirLivraisonsEnAttente() {
        return livraisonRepository.findByStatut(Livraison.StatutLivraison.EN_ATTENTE)
                .stream()
                .map(LivraisonDTO::depuisEntite)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une livraison par son identifiant.
     *
     * @param id l'identifiant de la livraison
     * @return la livraison trouvée
     * @throws BusinessException si la livraison n'existe pas
     */
    @Transactional(readOnly = true)
    public LivraisonDTO obtenirLivraisonParId(Long id) {
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Livraison introuvable avec l'identifiant : " + id,
                        HttpStatus.NOT_FOUND
                ));
        return LivraisonDTO.depuisEntite(livraison);
    }

    /**
     * Crée une nouvelle livraison pour une commande.
     *
     * @param dto les données de la livraison
     * @return la livraison créée
     * @throws BusinessException si la commande n'existe pas
     */
    public LivraisonDTO creerLivraison(LivraisonDTO dto) {
        Commande commande = commandeRepository.findById(dto.getIdCommande())
                .orElseThrow(() -> new BusinessException(
                        "Commande introuvable avec l'identifiant : " + dto.getIdCommande(),
                        HttpStatus.NOT_FOUND
                ));

        Livraison livraison = Livraison.builder()
                .commande(commande)
                .idLivreur(dto.getIdLivreur())
                .adresseLivraison(dto.getAdresseLivraison())
                .statut(Livraison.StatutLivraison.EN_ATTENTE)
                .creeLe(LocalDateTime.now())
                .build();

        // Mettre à jour le statut de la commande
        commande.setStatut(Commande.StatutCommande.EN_LIVRAISON);
        commandeRepository.save(commande);

        Livraison sauvegarde = livraisonRepository.save(livraison);
        return LivraisonDTO.depuisEntite(sauvegarde);
    }

    /**
     * Met à jour le statut d'une livraison.
     *
     * @param id     l'identifiant de la livraison
     * @param statut le nouveau statut
     * @return la livraison mise à jour
     * @throws BusinessException si la livraison n'existe pas
     */
    public LivraisonDTO mettreAJourStatut(Long id, Livraison.StatutLivraison statut) {
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Livraison introuvable avec l'identifiant : " + id,
                        HttpStatus.NOT_FOUND
                ));

        livraison.setStatut(statut);

        // Si la livraison est terminée, enregistrer la date
        if (statut == Livraison.StatutLivraison.LIVREE) {
            livraison.setLivreLe(LocalDateTime.now());
            // Mettre à jour le statut de la commande
            livraison.getCommande().setStatut(Commande.StatutCommande.LIVREE);
            commandeRepository.save(livraison.getCommande());
        }

        Livraison sauvegarde = livraisonRepository.save(livraison);
        return LivraisonDTO.depuisEntite(sauvegarde);
    }
}
