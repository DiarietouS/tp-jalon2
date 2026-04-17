package com.restaurants.business.controllers;

import com.restaurants.business.dtos.RestaurantDTO;
import com.restaurants.business.services.RestaurantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> obtenirTousLesRestaurants() {
        return ResponseEntity.ok(restaurantService.obtenirTousLesRestaurants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> obtenirRestaurantParId(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.obtenirRestaurantParId(id));
    }

    @PostMapping
    public ResponseEntity<RestaurantDTO> creerRestaurant(@RequestBody RestaurantDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantService.creerRestaurant(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantDTO> mettreAJourRestaurant(@PathVariable Long id, @RequestBody RestaurantDTO dto) {
        return ResponseEntity.ok(restaurantService.mettreAJourRestaurant(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerRestaurant(@PathVariable Long id) {
        restaurantService.supprimerRestaurant(id);
        return ResponseEntity.noContent().build();
    }
}
