package com.restaurants.business.controllers;

import com.restaurants.business.dtos.LivraisonDTO;
import com.restaurants.business.exceptions.BusinessException;
import com.restaurants.business.models.Livraison;
import com.restaurants.business.services.LivraisonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des livraisons.
 *
 * Base URL : /api/livraisons
 */
@RestController
@RequestMapping("/api/livraisons")
public class LivraisonController {

    private final LivraisonService livraisonService;

    public LivraisonController(LivraisonService livraisonService) {
        this.livraisonService = livraisonService;
    }

    /**
     * Récupère les livraisons d'un livreur.
     * GET /api/livraisons/livreur/{idLivreur}
     *
     * @param idLivreur l'identifiant du livreur
     * @return la liste des livraisons
     */
    @GetMapping("/livreur/{idLivreur}")
    @PreAuthorize("hasRole('LIVREUR') or hasRole('ADMIN')")
    public ResponseEntity<List<LivraisonDTO>> obtenirLivraisonsParLivreur(@PathVariable Long idLivreur) {
        return ResponseEntity.ok(livraisonService.obtenirLivraisonsParLivreur(idLivreur));
    }

    /**
     * Récupère les livraisons en attente.
     * GET /api/livraisons/en-attente
     *
     * @return la liste des livraisons en attente
     */
    @GetMapping("/en-attente")
    @PreAuthorize("hasRole('LIVREUR') or hasRole('ADMIN')")
    public ResponseEntity<List<LivraisonDTO>> obtenirLivraisonsEnAttente() {
        return ResponseEntity.ok(livraisonService.obtenirLivraisonsEnAttente());
    }

    /**
     * Récupère une livraison par son identifiant.
     * GET /api/livraisons/{id}
     *
     * @param id l'identifiant de la livraison
     * @return la livraison trouvée
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LivraisonDTO> obtenirLivraisonParId(@PathVariable Long id) {
        return ResponseEntity.ok(livraisonService.obtenirLivraisonParId(id));
    }

    /**
     * Crée une nouvelle livraison.
     * POST /api/livraisons
     *
     * @param dto les données de la livraison
     * @return la livraison créée
     */
    @PostMapping
    @PreAuthorize("hasRole('LIVREUR') or hasRole('ADMIN')")
    public ResponseEntity<LivraisonDTO> creerLivraison(@RequestBody LivraisonDTO dto) {
        LivraisonDTO cree = livraisonService.creerLivraison(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    }

    /**
     * Met à jour le statut d'une livraison.
     * PUT /api/livraisons/{id}/statut
     *
     * @param id    l'identifiant de la livraison
     * @param corps le corps contenant le nouveau statut
     * @return la livraison mise à jour
     */
    @PutMapping("/{id}/statut")
    @PreAuthorize("hasRole('LIVREUR') or hasRole('ADMIN')")
    public ResponseEntity<LivraisonDTO> mettreAJourStatut(
            @PathVariable Long id,
            @RequestBody Map<String, String> corps
    ) {
        String statutStr = corps.get("statut");
        if (statutStr == null) {
            throw new BusinessException("Le statut est obligatoire");
        }

        try {
            Livraison.StatutLivraison statut = Livraison.StatutLivraison.valueOf(statutStr.toUpperCase());
            return ResponseEntity.ok(livraisonService.mettreAJourStatut(id, statut));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Statut invalide : " + statutStr);
        }
    }

    /**
     * Gestionnaire d'exceptions pour BusinessException.
     *
     * @param ex l'exception métier
     * @return la réponse d'erreur
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> gererBusinessException(BusinessException ex) {
        Map<String, String> erreur = new HashMap<>();
        erreur.put("erreur", ex.getMessage());
        return ResponseEntity.status(ex.getStatut()).body(erreur);
    }
}
