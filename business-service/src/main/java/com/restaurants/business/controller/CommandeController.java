package com.restaurants.business.controller;

import com.restaurants.business.dto.CommandeDTO;
import com.restaurants.business.service.CommandeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
@CrossOrigin(origins = "http://localhost:4200")
public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    @GetMapping
    public ResponseEntity<List<CommandeDTO>> getAll(
            @RequestParam(required = false) Long idClient,
            @RequestParam(required = false) Long idRestaurant
    ) {
        return ResponseEntity.ok(commandeService.findAll(idClient, idRestaurant));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENT','RESTAURATEUR','LIVREUR')")
    public ResponseEntity<CommandeDTO> create(@RequestBody CommandeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandeService.create(dto));
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('RESTAURATEUR','LIVREUR')")
    public ResponseEntity<CommandeDTO> updateStatut(@PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(commandeService.updateStatut(id, statut));
    }
}
