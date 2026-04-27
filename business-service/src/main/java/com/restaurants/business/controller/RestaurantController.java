package com.restaurants.business.controller;

import com.restaurants.business.dto.RestaurantDTO;
import com.restaurants.business.service.RestaurantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * COURS INF1013: contrôleur REST suivant le pattern Angular HttpClient.
 * GET /api/restaurants → liste publique (après auth JWT).
 * POST/PUT/DELETE → réservé aux restaurateurs (@PreAuthorize).
 */
@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = "http://localhost:4200")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> getAll() {
        return ResponseEntity.ok(restaurantService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.findById(id));
    }

    @GetMapping("/proprietaire/{idProprietaire}")
    public ResponseEntity<List<RestaurantDTO>> getByProprietaire(@PathVariable Long idProprietaire) {
        return ResponseEntity.ok(restaurantService.findByProprietaire(idProprietaire));
    }

    @PostMapping
    @PreAuthorize("hasRole('RESTAURATEUR')")
    public ResponseEntity<RestaurantDTO> create(@RequestBody RestaurantDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURATEUR')")
    public ResponseEntity<RestaurantDTO> update(@PathVariable Long id, @RequestBody RestaurantDTO dto) {
        return ResponseEntity.ok(restaurantService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURATEUR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        restaurantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
