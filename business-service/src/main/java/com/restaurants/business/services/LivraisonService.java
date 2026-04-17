package com.restaurants.business.services;

import com.restaurants.business.dtos.LivraisonDTO;
import com.restaurants.business.exceptions.BusinessException;
import com.restaurants.business.models.Commande;
import com.restaurants.business.models.Livraison;
import com.restaurants.business.repositories.CommandeRepository;
import com.restaurants.business.repositories.LivraisonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LivraisonService {

    private final LivraisonRepository livraisonRepository;
    private final CommandeRepository commandeRepository;

    public LivraisonService(LivraisonRepository livraisonRepository, CommandeRepository commandeRepository) {
        this.livraisonRepository = livraisonRepository;
        this.commandeRepository = commandeRepository;
    }

    public List<LivraisonDTO> obtenirToutesLesLivraisons() {
        return livraisonRepository.findAll().stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    public LivraisonDTO obtenirLivraisonParId(Long id) {
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Livraison non trouvée avec l'id: " + id));
        return convertirEnDTO(livraison);
    }

    public LivraisonDTO creerLivraison(LivraisonDTO dto) {
        Commande commande = commandeRepository.findById(dto.getCommandeId())
                .orElseThrow(() -> new BusinessException("Commande non trouvée avec l'id: " + dto.getCommandeId()));

        Livraison livraison = new Livraison();
        livraison.setCommande(commande);
        livraison.setCourrielLivreur(dto.getCourrielLivreur());
        if (dto.getNotes() != null) {
            livraison.setNotes(dto.getNotes());
        }
        return convertirEnDTO(livraisonRepository.save(livraison));
    }

    public LivraisonDTO assignerLivreur(Long id, String courrielLivreur) {
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Livraison non trouvée avec l'id: " + id));
        livraison.setCourrielLivreur(courrielLivreur);
        livraison.setStatut(Livraison.StatutLivraison.ASSIGNEE);
        livraison.setDateAssignation(LocalDateTime.now());
        return convertirEnDTO(livraisonRepository.save(livraison));
    }

    public LivraisonDTO mettreAJourStatut(Long id, String statut) {
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Livraison non trouvée avec l'id: " + id));
        try {
            Livraison.StatutLivraison nouveauStatut = Livraison.StatutLivraison.valueOf(statut.toUpperCase());
            livraison.setStatut(nouveauStatut);
            if (nouveauStatut == Livraison.StatutLivraison.LIVREE) {
                livraison.setDateLivraison(LocalDateTime.now());
            }
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Statut invalide: " + statut);
        }
        return convertirEnDTO(livraisonRepository.save(livraison));
    }

    public void supprimerLivraison(Long id) {
        if (!livraisonRepository.existsById(id)) {
            throw new BusinessException("Livraison non trouvée avec l'id: " + id);
        }
        livraisonRepository.deleteById(id);
    }

    private LivraisonDTO convertirEnDTO(Livraison livraison) {
        LivraisonDTO dto = new LivraisonDTO();
        dto.setId(livraison.getId());
        dto.setCommandeId(livraison.getCommande().getId());
        dto.setCourrielLivreur(livraison.getCourrielLivreur());
        dto.setStatut(livraison.getStatut().name());
        dto.setDateAssignation(livraison.getDateAssignation());
        dto.setDateLivraison(livraison.getDateLivraison());
        dto.setNotes(livraison.getNotes());
        return dto;
    }
}
