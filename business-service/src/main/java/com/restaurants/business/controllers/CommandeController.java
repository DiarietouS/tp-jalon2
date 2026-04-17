package com.restaurants.business.controllers;

import com.restaurants.business.dtos.CommandeDTO;
import com.restaurants.business.exceptions.BusinessException;
import com.restaurants.business.models.Commande;
import com.restaurants.business.services.CommandeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des commandes.
 *
 * Base URL : /api/commandes
 */
@RestController
@RequestMapping("/api/commandes")
public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    /**
     * Récupère les commandes d'un client.
     * GET /api/commandes/client/{idClient}
     *
     * @param idClient l'identifiant du client
     * @return la liste des commandes
     */
    @GetMapping("/client/{idClient}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<List<CommandeDTO>> obtenirCommandesParClient(@PathVariable Long idClient) {
        return ResponseEntity.ok(commandeService.obtenirCommandesParClient(idClient));
    }

    /**
     * Récupère les commandes d'un restaurant.
     * GET /api/commandes/restaurant/{idRestaurant}
     *
     * @param idRestaurant l'identifiant du restaurant
     * @return la liste des commandes
     */
    @GetMapping("/restaurant/{idRestaurant}")
    @PreAuthorize("hasRole('RESTAURATEUR') or hasRole('ADMIN')")
    public ResponseEntity<List<CommandeDTO>> obtenirCommandesParRestaurant(
            @PathVariable Long idRestaurant
    ) {
        return ResponseEntity.ok(commandeService.obtenirCommandesParRestaurant(idRestaurant));
    }

    /**
     * Récupère une commande par son identifiant.
     * GET /api/commandes/{id}
     *
     * @param id l'identifiant de la commande
     * @return la commande trouvée
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommandeDTO> obtenirCommandeParId(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.obtenirCommandeParId(id));
    }

    /**
     * Crée une nouvelle commande.
     * POST /api/commandes
     *
     * @param dto les données de la commande
     * @return la commande créée
     */
    @PostMapping
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<CommandeDTO> creerCommande(@RequestBody CommandeDTO dto) {
        CommandeDTO cree = commandeService.creerCommande(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    }

    /**
     * Met à jour le statut d'une commande.
     * PUT /api/commandes/{id}/statut
     *
     * @param id     l'identifiant de la commande
     * @param corps  le corps contenant le nouveau statut
     * @return la commande mise à jour
     */
    @PutMapping("/{id}/statut")
    @PreAuthorize("hasRole('RESTAURATEUR') or hasRole('LIVREUR') or hasRole('ADMIN')")
    public ResponseEntity<CommandeDTO> mettreAJourStatut(
            @PathVariable Long id,
            @RequestBody Map<String, String> corps
    ) {
        String statutStr = corps.get("statut");
        if (statutStr == null) {
            throw new BusinessException("Le statut est obligatoire");
        }

        try {
            Commande.StatutCommande statut = Commande.StatutCommande.valueOf(statutStr.toUpperCase());
            return ResponseEntity.ok(commandeService.mettreAJourStatut(id, statut));
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
