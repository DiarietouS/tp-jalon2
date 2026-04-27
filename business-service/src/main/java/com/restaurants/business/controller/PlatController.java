package com.restaurants.business.controller;

import com.restaurants.business.dto.PlatDTO;
import com.restaurants.business.service.PlatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plats")
@CrossOrigin(origins = "http://localhost:4200")
public class PlatController {

    private final PlatService platService;

    public PlatController(PlatService platService) {
        this.platService = platService;
    }

    @GetMapping
    public ResponseEntity<List<PlatDTO>> getAll() {
        return ResponseEntity.ok(platService.findAll());
    }

    @GetMapping("/restaurant/{idRestaurant}")
    public ResponseEntity<List<PlatDTO>> getByRestaurant(@PathVariable Long idRestaurant) {
        return ResponseEntity.ok(platService.findByRestaurant(idRestaurant));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlatDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(platService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('RESTAURATEUR')")
    public ResponseEntity<PlatDTO> create(@RequestBody PlatDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(platService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURATEUR')")
    public ResponseEntity<PlatDTO> update(@PathVariable Long id, @RequestBody PlatDTO dto) {
        return ResponseEntity.ok(platService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURATEUR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        platService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
