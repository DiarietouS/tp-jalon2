package com.restaurants.auth.controllers;

import com.restaurants.auth.dtos.AuthResponseDTO;
import com.restaurants.auth.dtos.LoginRequestDTO;
import com.restaurants.auth.dtos.RegisterRequestDTO;
import com.restaurants.auth.dtos.UtilisateurDTO;
import com.restaurants.auth.exceptions.AuthException;
import com.restaurants.auth.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur REST pour l'authentification.
 * Expose les endpoints de connexion et d'inscription.
 *
 * COURS INF1013 - Architecture REST :
 * "Controller → Service → Repository"
 * "Verbes HTTP : POST pour créer/authentifier"
 * "Réponses avec codes HTTP appropriés"
 *
 * Base URL : /api/auth
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint de connexion.
     * POST /api/auth/connexion
     *
     * @param requete les informations de connexion
     * @return le jeton JWT et les informations de l'utilisateur
     */
    @PostMapping("/connexion")
    public ResponseEntity<AuthResponseDTO> connexion(@Valid @RequestBody LoginRequestDTO requete) {
        AuthResponseDTO reponse = authService.connexion(requete);
        return ResponseEntity.ok(reponse);
    }

    /**
     * Endpoint d'inscription.
     * POST /api/auth/inscription
     *
     * @param requete les informations d'inscription
     * @return le jeton JWT et les informations du nouvel utilisateur
     */
    @PostMapping("/inscription")
    public ResponseEntity<AuthResponseDTO> inscription(@Valid @RequestBody RegisterRequestDTO requete) {
        AuthResponseDTO reponse = authService.inscription(requete);
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }

    /**
     * Endpoint de validation du jeton JWT.
     * POST /api/auth/valider
     *
     * @param corps le corps de la requête contenant le jeton
     * @return les informations de l'utilisateur si le jeton est valide
     */
    @PostMapping("/valider")
    public ResponseEntity<UtilisateurDTO> validerJeton(@RequestBody Map<String, String> corps) {
        String jeton = corps.get("token");
        if (jeton == null || jeton.isBlank()) {
            throw new AuthException("Le jeton est obligatoire", HttpStatus.BAD_REQUEST);
        }
        UtilisateurDTO utilisateur = authService.validerJeton(jeton);
        return ResponseEntity.ok(utilisateur);
    }

    /**
     * Endpoint de vérification de santé du service.
     * GET /api/auth/sante
     *
     * @return un message confirmant que le service fonctionne
     */
    @GetMapping("/sante")
    public ResponseEntity<Map<String, String>> verifierSante() {
        Map<String, String> reponse = new HashMap<>();
        reponse.put("statut", "En fonctionnement");
        reponse.put("service", "auth-service");
        reponse.put("version", "1.0.0");
        return ResponseEntity.ok(reponse);
    }

    /**
     * Gestionnaire d'exceptions pour AuthException.
     *
     * @param ex l'exception d'authentification
     * @return la réponse d'erreur avec le code HTTP approprié
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, String>> gererAuthException(AuthException ex) {
        Map<String, String> erreur = new HashMap<>();
        erreur.put("erreur", ex.getMessage());
        return ResponseEntity.status(ex.getStatut()).body(erreur);
    }
}
