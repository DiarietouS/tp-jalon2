package com.restaurants.auth.controllers;

import com.restaurants.auth.dtos.AuthResponseDTO;
import com.restaurants.auth.dtos.ConnexionRequestDTO;
import com.restaurants.auth.dtos.InscriptionRequestDTO;
import com.restaurants.auth.dtos.UtilisateurDTO;
import com.restaurants.auth.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/connexion")
    public ResponseEntity<AuthResponseDTO> connexion(@Valid @RequestBody ConnexionRequestDTO requete) {
        return ResponseEntity.ok(authService.connexion(requete));
    }

    @PostMapping("/inscription")
    public ResponseEntity<AuthResponseDTO> inscription(@Valid @RequestBody InscriptionRequestDTO requete) {
        return ResponseEntity.ok(authService.inscription(requete));
    }

    @GetMapping("/moi")
    public ResponseEntity<UtilisateurDTO> obtenirMoi(Authentication authentication) {
        return ResponseEntity.ok(authService.obtenirUtilisateurCourant(authentication.getName()));
    }
}
