package com.restaurants.business.controllers;

import com.restaurants.business.dtos.CommandeDTO;
import com.restaurants.business.services.CommandeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    @GetMapping
    public ResponseEntity<List<CommandeDTO>> obtenirToutesLesCommandes() {
        return ResponseEntity.ok(commandeService.obtenirToutesLesCommandes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeDTO> obtenirCommandeParId(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.obtenirCommandeParId(id));
    }

    @GetMapping("/mes-commandes")
    public ResponseEntity<List<CommandeDTO>> obtenirMesCommandes(Authentication authentication) {
        return ResponseEntity.ok(commandeService.obtenirCommandesParClient(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<CommandeDTO> creerCommande(@RequestBody CommandeDTO dto, Authentication authentication) {
        dto.setCourrielClient(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(commandeService.creerCommande(dto));
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<CommandeDTO> mettreAJourStatut(@PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(commandeService.mettreAJourStatut(id, statut));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerCommande(@PathVariable Long id) {
        commandeService.supprimerCommande(id);
        return ResponseEntity.noContent().build();
    }
}
