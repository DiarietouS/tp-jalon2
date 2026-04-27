package com.restaurants.business.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurants.business.dto.ArticleCommandeDTO;
import com.restaurants.business.dto.LigneLivraisonDTO;
import com.restaurants.business.dto.LivraisonDTO;
import com.restaurants.business.entity.CommandeEntity;
import com.restaurants.business.repository.CommandeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/livraisons")
@CrossOrigin(origins = "http://localhost:4200")
public class LivraisonController {

    private final CommandeRepository commandeRepository;
    private final ObjectMapper objectMapper;

    public LivraisonController(CommandeRepository commandeRepository, ObjectMapper objectMapper) {
        this.commandeRepository = commandeRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<List<LivraisonDTO>> getAll() {
        List<CommandeEntity> commandes = commandeRepository.findAll();
        commandes.sort((a, b) -> Long.compare(b.getId(), a.getId()));

        List<LivraisonDTO> result = new ArrayList<>();
        for (CommandeEntity commande : commandes) {
            if (isLivraisonCandidate(commande.getStatut())) {
                result.add(toLivraisonDTO(commande));
            }
        }

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('LIVREUR','RESTAURATEUR')")
    public ResponseEntity<LivraisonDTO> updateStatut(@PathVariable Long id, @RequestParam String statut) {
        CommandeEntity commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commande introuvable: " + id));

        commande.setStatut(mapLivraisonStatutToCommandeStatut(statut));
        CommandeEntity saved = commandeRepository.save(commande);
        return ResponseEntity.ok(toLivraisonDTO(saved));
    }

    private boolean isLivraisonCandidate(String statutCommande) {
        return "prete".equals(statutCommande) || "enLivraison".equals(statutCommande) || "livree".equals(statutCommande);
    }

    private LivraisonDTO toLivraisonDTO(CommandeEntity commande) {
        LivraisonDTO dto = new LivraisonDTO();
        dto.setId(commande.getId());
        dto.setClient(commande.getNomRestaurant());
        dto.setAdresse(commande.getAdresseLivraison());
        dto.setTelephone(commande.getTelephone());
        dto.setCreeLe(commande.getCreeLe());
        dto.setStatut(mapCommandeStatutToLivraisonStatut(commande.getStatut()));
        dto.setLignes(toLignes(commande.getArticlesJson()));
        return dto;
    }

    private List<LigneLivraisonDTO> toLignes(String articlesJson) {
        if (articlesJson == null || articlesJson.isBlank()) {
            return List.of();
        }
        try {
            List<ArticleCommandeDTO> articles = objectMapper.readValue(
                    articlesJson,
                    new TypeReference<List<ArticleCommandeDTO>>() {}
            );
            List<LigneLivraisonDTO> lignes = new ArrayList<>();
            for (ArticleCommandeDTO article : articles) {
                LigneLivraisonDTO ligne = new LigneLivraisonDTO();
                ligne.setNumPlat(article.getIdPlat());
                ligne.setLibelle(article.getNomPlat());
                ligne.setQuantite(article.getQuantite());
                ligne.setPrixUnitaire(article.getPrixUnitaire());
                lignes.add(ligne);
            }
            return lignes;
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }

    private String mapCommandeStatutToLivraisonStatut(String statutCommande) {
        if ("enLivraison".equals(statutCommande)) return "EN_COURS";
        if ("livree".equals(statutCommande)) return "LIVREE";
        return "EN_ATTENTE";
    }

    private String mapLivraisonStatutToCommandeStatut(String statutLivraison) {
        if ("EN_COURS".equals(statutLivraison)) return "enLivraison";
        if ("LIVREE".equals(statutLivraison)) return "livree";
        return "prete";
    }
}
