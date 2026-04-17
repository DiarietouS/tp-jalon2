package com.restaurants.business.services;

import com.restaurants.business.dtos.CommandeDTO;
import com.restaurants.business.dtos.LigneCommandeDTO;
import com.restaurants.business.exceptions.BusinessException;
import com.restaurants.business.models.Commande;
import com.restaurants.business.models.LigneCommande;
import com.restaurants.business.models.Plat;
import com.restaurants.business.models.Restaurant;
import com.restaurants.business.repositories.CommandeRepository;
import com.restaurants.business.repositories.PlatRepository;
import com.restaurants.business.repositories.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final RestaurantRepository restaurantRepository;
    private final PlatRepository platRepository;

    public CommandeService(CommandeRepository commandeRepository,
                           RestaurantRepository restaurantRepository,
                           PlatRepository platRepository) {
        this.commandeRepository = commandeRepository;
        this.restaurantRepository = restaurantRepository;
        this.platRepository = platRepository;
    }

    public List<CommandeDTO> obtenirToutesLesCommandes() {
        return commandeRepository.findAll().stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    public List<CommandeDTO> obtenirCommandesParClient(String courrielClient) {
        return commandeRepository.findByCourrielClient(courrielClient).stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    public CommandeDTO obtenirCommandeParId(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Commande non trouvée avec l'id: " + id));
        return convertirEnDTO(commande);
    }

    @Transactional
    public CommandeDTO creerCommande(CommandeDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new BusinessException("Restaurant non trouvé avec l'id: " + dto.getRestaurantId()));

        Commande commande = new Commande();
        commande.setCourrielClient(dto.getCourrielClient());
        commande.setAdresseLivraison(dto.getAdresseLivraison());
        commande.setRestaurant(restaurant);

        List<LigneCommande> lignes = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        if (dto.getLignesCommande() != null) {
            for (LigneCommandeDTO ligneDTO : dto.getLignesCommande()) {
                Plat plat = platRepository.findById(ligneDTO.getPlatId())
                        .orElseThrow(() -> new BusinessException("Plat non trouvé avec l'id: " + ligneDTO.getPlatId()));
                LigneCommande ligne = new LigneCommande();
                ligne.setPlat(plat);
                ligne.setQuantite(ligneDTO.getQuantite());
                ligne.setPrixUnitaire(plat.getPrix());
                ligne.setSousTotal(plat.getPrix().multiply(BigDecimal.valueOf(ligneDTO.getQuantite())));
                ligne.setCommande(commande);
                lignes.add(ligne);
                total = total.add(ligne.getSousTotal());
            }
        }

        commande.setLignesCommande(lignes);
        commande.setMontantTotal(total);
        return convertirEnDTO(commandeRepository.save(commande));
    }

    public CommandeDTO mettreAJourStatut(Long id, String statut) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Commande non trouvée avec l'id: " + id));
        try {
            commande.setStatut(Commande.StatutCommande.valueOf(statut.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Statut invalide: " + statut);
        }
        return convertirEnDTO(commandeRepository.save(commande));
    }

    public void supprimerCommande(Long id) {
        if (!commandeRepository.existsById(id)) {
            throw new BusinessException("Commande non trouvée avec l'id: " + id);
        }
        commandeRepository.deleteById(id);
    }

    private CommandeDTO convertirEnDTO(Commande commande) {
        CommandeDTO dto = new CommandeDTO();
        dto.setId(commande.getId());
        dto.setCourrielClient(commande.getCourrielClient());
        dto.setAdresseLivraison(commande.getAdresseLivraison());
        dto.setDateCommande(commande.getDateCommande());
        dto.setStatut(commande.getStatut().name());
        dto.setMontantTotal(commande.getMontantTotal());
        dto.setRestaurantId(commande.getRestaurant().getId());

        List<LigneCommandeDTO> lignesDTO = commande.getLignesCommande().stream()
                .map(l -> {
                    LigneCommandeDTO lDto = new LigneCommandeDTO();
                    lDto.setId(l.getId());
                    lDto.setPlatId(l.getPlat().getId());
                    lDto.setNomPlat(l.getPlat().getNom());
                    lDto.setQuantite(l.getQuantite());
                    lDto.setPrixUnitaire(l.getPrixUnitaire());
                    lDto.setSousTotal(l.getSousTotal());
                    return lDto;
                })
                .collect(Collectors.toList());
        dto.setLignesCommande(lignesDTO);
        return dto;
    }
}
