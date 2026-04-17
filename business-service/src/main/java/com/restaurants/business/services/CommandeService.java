package com.restaurants.business.services;

import com.restaurants.business.dtos.CommandeDTO;
import com.restaurants.business.dtos.LigneCommandeDTO;
import com.restaurants.business.exceptions.BusinessException;
import com.restaurants.business.models.Commande;
import com.restaurants.business.models.LigneCommande;
import com.restaurants.business.repositories.CommandeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des commandes.
 */
@Service
@Transactional
public class CommandeService {

    private final CommandeRepository commandeRepository;

    public CommandeService(CommandeRepository commandeRepository) {
        this.commandeRepository = commandeRepository;
    }

    /**
     * Récupère toutes les commandes d'un client.
     *
     * @param idClient l'identifiant du client
     * @return la liste des commandes
     */
    @Transactional(readOnly = true)
    public List<CommandeDTO> obtenirCommandesParClient(Long idClient) {
        return commandeRepository.findByIdClientOrderByCreeLe_Desc(idClient)
                .stream()
                .map(CommandeDTO::depuisEntite)
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les commandes d'un restaurant.
     *
     * @param idRestaurant l'identifiant du restaurant
     * @return la liste des commandes
     */
    @Transactional(readOnly = true)
    public List<CommandeDTO> obtenirCommandesParRestaurant(Long idRestaurant) {
        return commandeRepository.findByIdRestaurantOrderByCreeLe_Desc(idRestaurant)
                .stream()
                .map(CommandeDTO::depuisEntite)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une commande par son identifiant.
     *
     * @param id l'identifiant de la commande
     * @return la commande trouvée
     * @throws BusinessException si la commande n'existe pas
     */
    @Transactional(readOnly = true)
    public CommandeDTO obtenirCommandeParId(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Commande introuvable avec l'identifiant : " + id,
                        HttpStatus.NOT_FOUND
                ));
        return CommandeDTO.depuisEntite(commande);
    }

    /**
     * Crée une nouvelle commande.
     *
     * @param dto les données de la commande
     * @return la commande créée
     */
    public CommandeDTO creerCommande(CommandeDTO dto) {
        // Créer la commande principale
        Commande commande = Commande.builder()
                .idClient(dto.getIdClient())
                .idRestaurant(dto.getIdRestaurant())
                .nomRestaurant(dto.getNomRestaurant())
                .statut(Commande.StatutCommande.EN_ATTENTE)
                .sousTotal(dto.getSousTotal())
                .fraisLivraison(dto.getFraisLivraison())
                .total(dto.getTotal())
                .adresseLivraison(dto.getAdresseLivraison())
                .telephone(dto.getTelephone())
                .creeLe(LocalDateTime.now())
                .livraisonEstimee(LocalDateTime.now().plusMinutes(35))
                .build();

        // Ajouter les lignes de commande
        if (dto.getLignes() != null) {
            List<LigneCommande> lignes = dto.getLignes().stream()
                    .map(ligneDto -> creerLigneCommande(ligneDto, commande))
                    .collect(Collectors.toList());
            commande.setLignes(lignes);
        }

        Commande sauvegarde = commandeRepository.save(commande);
        return CommandeDTO.depuisEntite(sauvegarde);
    }

    /**
     * Crée une ligne de commande à partir d'un DTO.
     *
     * @param dto      le DTO de la ligne
     * @param commande la commande parente
     * @return la ligne de commande
     */
    private LigneCommande creerLigneCommande(LigneCommandeDTO dto, Commande commande) {
        return LigneCommande.builder()
                .commande(commande)
                .idPlat(dto.getIdPlat())
                .nomPlat(dto.getNomPlat())
                .quantite(dto.getQuantite())
                .prixUnitaire(dto.getPrixUnitaire())
                .build();
    }

    /**
     * Met à jour le statut d'une commande.
     *
     * @param id     l'identifiant de la commande
     * @param statut le nouveau statut
     * @return la commande mise à jour
     * @throws BusinessException si la commande n'existe pas
     */
    public CommandeDTO mettreAJourStatut(Long id, Commande.StatutCommande statut) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Commande introuvable avec l'identifiant : " + id,
                        HttpStatus.NOT_FOUND
                ));

        commande.setStatut(statut);
        Commande sauvegarde = commandeRepository.save(commande);
        return CommandeDTO.depuisEntite(sauvegarde);
    }
}
