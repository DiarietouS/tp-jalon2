package com.restaurants.auth.controller;

import com.restaurants.auth.dto.*;
import com.restaurants.auth.entity.UtilisateurEntity;
import com.restaurants.auth.security.JwtUtils;
import com.restaurants.auth.service.MotDePasseService;
import com.restaurants.auth.service.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur d'authentification.
 * COURS INF1013: "Définir le contrôleur AuthController avec /api/auth
 * et accepter les CORS. Sécuriser toutes les routes autres que api/auth/**"
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UtilisateurService utilisateurService;
    private final MotDePasseService motDePasseService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils,
                          UtilisateurService utilisateurService,
                          MotDePasseService motDePasseService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.utilisateurService = utilisateurService;
        this.motDePasseService = motDePasseService;
    }

    /** POST /api/auth/signup — Création de compte */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDTO> signup(@RequestBody SignUpRequestDTO dto) {
        UtilisateurEntity user = utilisateurService.creerUtilisateur(dto);
        ConnectedUserDTO userDetails = new ConnectedUserDTO(user);
        String token = jwtUtils.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponseDTO(
                token, user.getId(), user.getCourriel(),
                user.getPrenom(), user.getNom(), user.getRole()));
    }

    /**
     * POST /api/auth/signin — Connexion.
     * COURS INF1013: authenticationManager.authenticate() avec
     * UsernamePasswordAuthenticationToken valide les credentials via
     * DaoAuthenticationProvider → UserDetailsService → PasswordEncoder.
     */
    @PostMapping("/signin")
    public ResponseEntity<AuthResponseDTO> signin(@RequestBody SignInRequestDTO dto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getCourriel(), dto.getMotDePasse())
        );
        ConnectedUserDTO userDetails = (ConnectedUserDTO) auth.getPrincipal();
        String token = jwtUtils.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponseDTO(
                token,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getPrenom(),
                userDetails.getNom(),
                userDetails.getRole()
        ));
    }

    /**
     * POST /api/auth/mot-de-passe-oublie — Demande de réinitialisation.
     * Génère un token sécurisé (UUID) valide 1h, stocké en base.
     * En production : envoi par email. En développement : retourné dans la réponse.
     */
    @PostMapping("/mot-de-passe-oublie")
    public ResponseEntity<MotDePasseOubliResponseDTO> motDePasseOubli(
            @RequestBody MotDePasseOubliRequestDTO dto) {
        return ResponseEntity.ok(motDePasseService.demanderReinitialisation(dto.getCourriel()));
    }

    /**
     * POST /api/auth/reinitialiser-mot-de-passe — Réinitialisation avec le token.
     * Valide le token (non expiré, non utilisé), puis met à jour le mot de passe.
     */
    @PostMapping("/reinitialiser-mot-de-passe")
    public ResponseEntity<Void> reinitialiserMotDePasse(
            @RequestBody ReinitMotDePasseRequestDTO dto) {
        motDePasseService.reinitialiserMotDePasse(dto.getJeton(), dto.getNouveauMotDePasse());
        return ResponseEntity.ok().build();
    }
}

