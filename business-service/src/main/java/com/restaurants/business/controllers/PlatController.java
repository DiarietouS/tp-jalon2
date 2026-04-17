package com.restaurants.business.controllers;

import com.restaurants.business.dtos.PlatDTO;
import com.restaurants.business.services.PlatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plats")
public class PlatController {

    private final PlatService platService;

    public PlatController(PlatService platService) {
        this.platService = platService;
    }

    @GetMapping
    public ResponseEntity<List<PlatDTO>> obtenirTousLesPlats() {
        return ResponseEntity.ok(platService.obtenirTousLesPlats());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlatDTO> obtenirPlatParId(@PathVariable Long id) {
        return ResponseEntity.ok(platService.obtenirPlatParId(id));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<PlatDTO>> obtenirPlatsParRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(platService.obtenirPlatsParRestaurant(restaurantId));
    }

    @PostMapping
    public ResponseEntity<PlatDTO> creerPlat(@RequestBody PlatDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(platService.creerPlat(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlatDTO> mettreAJourPlat(@PathVariable Long id, @RequestBody PlatDTO dto) {
        return ResponseEntity.ok(platService.mettreAJourPlat(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerPlat(@PathVariable Long id) {
        platService.supprimerPlat(id);
        return ResponseEntity.noContent().build();
    }
}
