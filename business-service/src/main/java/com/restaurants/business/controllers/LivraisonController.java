package com.restaurants.business.controllers;

import com.restaurants.business.dtos.LivraisonDTO;
import com.restaurants.business.services.LivraisonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livraisons")
public class LivraisonController {

    private final LivraisonService livraisonService;

    public LivraisonController(LivraisonService livraisonService) {
        this.livraisonService = livraisonService;
    }

    @GetMapping
    public ResponseEntity<List<LivraisonDTO>> obtenirToutesLesLivraisons() {
        return ResponseEntity.ok(livraisonService.obtenirToutesLesLivraisons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivraisonDTO> obtenirLivraisonParId(@PathVariable Long id) {
        return ResponseEntity.ok(livraisonService.obtenirLivraisonParId(id));
    }

    @PostMapping
    public ResponseEntity<LivraisonDTO> creerLivraison(@RequestBody LivraisonDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(livraisonService.creerLivraison(dto));
    }

    @PutMapping("/{id}/assigner")
    public ResponseEntity<LivraisonDTO> assignerLivreur(@PathVariable Long id, @RequestParam String courrielLivreur) {
        return ResponseEntity.ok(livraisonService.assignerLivreur(id, courrielLivreur));
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<LivraisonDTO> mettreAJourStatut(@PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(livraisonService.mettreAJourStatut(id, statut));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerLivraison(@PathVariable Long id) {
        livraisonService.supprimerLivraison(id);
        return ResponseEntity.noContent().build();
    }
}
