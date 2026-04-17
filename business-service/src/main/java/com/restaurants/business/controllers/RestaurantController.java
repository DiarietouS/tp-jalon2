package com.restaurants.business.controllers;

import com.restaurants.business.dtos.RestaurantDTO;
import com.restaurants.business.exceptions.BusinessException;
import com.restaurants.business.services.RestaurantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des restaurants.
 *
 * COURS INF1013 - Architecture REST :
 * "Verbes HTTP : GET (lecture), POST (création), PUT (mise à jour), DELETE (suppression)"
 *
 * Base URL : /api/restaurants
 */
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    /**
     * Récupère tous les restaurants.
     * GET /api/restaurants
     *
     * @return la liste de tous les restaurants
     */
    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> obtenirTousLesRestaurants() {
        return ResponseEntity.ok(restaurantService.obtenirTousLesRestaurants());
    }

    /**
     * Récupère un restaurant par son identifiant.
     * GET /api/restaurants/{id}
     *
     * @param id l'identifiant du restaurant
     * @return le restaurant trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> obtenirRestaurantParId(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.obtenirRestaurantParId(id));
    }

    /**
     * Récupère les restaurants d'un propriétaire.
     * GET /api/restaurants/proprietaire/{idProprietaire}
     *
     * @param idProprietaire l'identifiant du propriétaire
     * @return la liste des restaurants
     */
    @GetMapping("/proprietaire/{idProprietaire}")
    @PreAuthorize("hasRole('RESTAURATEUR') or hasRole('ADMIN')")
    public ResponseEntity<List<RestaurantDTO>> obtenirRestaurantsParProprietaire(
            @PathVariable Long idProprietaire
    ) {
        return ResponseEntity.ok(restaurantService.obtenirRestaurantsParProprietaire(idProprietaire));
    }

    /**
     * Crée un nouveau restaurant.
     * POST /api/restaurants
     *
     * @param dto les données du restaurant
     * @return le restaurant créé
     */
    @PostMapping
    @PreAuthorize("hasRole('RESTAURATEUR') or hasRole('ADMIN')")
    public ResponseEntity<RestaurantDTO> creerRestaurant(@RequestBody RestaurantDTO dto) {
        RestaurantDTO cree = restaurantService.creerRestaurant(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    }

    /**
     * Met à jour un restaurant existant.
     * PUT /api/restaurants/{id}
     *
     * @param id  l'identifiant du restaurant
     * @param dto les nouvelles données
     * @return le restaurant mis à jour
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURATEUR') or hasRole('ADMIN')")
    public ResponseEntity<RestaurantDTO> mettreAJourRestaurant(
            @PathVariable Long id,
            @RequestBody RestaurantDTO dto
    ) {
        return ResponseEntity.ok(restaurantService.mettreAJourRestaurant(id, dto));
    }

    /**
     * Supprime un restaurant.
     * DELETE /api/restaurants/{id}
     *
     * @param id l'identifiant du restaurant à supprimer
     * @return une réponse sans contenu
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimerRestaurant(@PathVariable Long id) {
        restaurantService.supprimerRestaurant(id);
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
