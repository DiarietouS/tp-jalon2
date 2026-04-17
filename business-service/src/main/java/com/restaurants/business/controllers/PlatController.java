package com.restaurants.business.controllers;

import com.restaurants.business.dtos.PlatDTO;
import com.restaurants.business.exceptions.BusinessException;
import com.restaurants.business.services.PlatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des plats.
 *
 * Base URL : /api/plats
 */
@RestController
@RequestMapping("/api/plats")
public class PlatController {

    private final PlatService platService;

    public PlatController(PlatService platService) {
        this.platService = platService;
    }

    /**
     * Récupère les plats d'un restaurant.
     * GET /api/plats/restaurant/{idRestaurant}
     *
     * @param idRestaurant l'identifiant du restaurant
     * @return la liste des plats
     */
    @GetMapping("/restaurant/{idRestaurant}")
    public ResponseEntity<List<PlatDTO>> obtenirPlatsParRestaurant(@PathVariable Long idRestaurant) {
        return ResponseEntity.ok(platService.obtenirPlatsParRestaurant(idRestaurant));
    }

    /**
     * Récupère un plat par son identifiant.
     * GET /api/plats/{id}
     *
     * @param id l'identifiant du plat
     * @return le plat trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlatDTO> obtenirPlatParId(@PathVariable Long id) {
        return ResponseEntity.ok(platService.obtenirPlatParId(id));
    }

    /**
     * Crée un nouveau plat.
     * POST /api/plats
     *
     * @param dto les données du plat
     * @return le plat créé
     */
    @PostMapping
    @PreAuthorize("hasRole('RESTAURATEUR') or hasRole('ADMIN')")
    public ResponseEntity<PlatDTO> creerPlat(@RequestBody PlatDTO dto) {
        PlatDTO cree = platService.creerPlat(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    }

    /**
     * Met à jour un plat existant.
     * PUT /api/plats/{id}
     *
     * @param id  l'identifiant du plat
     * @param dto les nouvelles données
     * @return le plat mis à jour
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURATEUR') or hasRole('ADMIN')")
    public ResponseEntity<PlatDTO> mettreAJourPlat(
            @PathVariable Long id,
            @RequestBody PlatDTO dto
    ) {
        return ResponseEntity.ok(platService.mettreAJourPlat(id, dto));
    }

    /**
     * Supprime un plat.
     * DELETE /api/plats/{id}
     *
     * @param id l'identifiant du plat à supprimer
     * @return une réponse sans contenu
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURATEUR') or hasRole('ADMIN')")
    public ResponseEntity<Void> supprimerPlat(@PathVariable Long id) {
        platService.supprimerPlat(id);
        return ResponseEntity.noContent().build();
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
