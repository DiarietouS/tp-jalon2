package com.restaurants.auth.services;

import com.restaurants.auth.dtos.AuthResponseDTO;
import com.restaurants.auth.dtos.LoginRequestDTO;
import com.restaurants.auth.dtos.RegisterRequestDTO;
import com.restaurants.auth.dtos.UtilisateurDTO;
import com.restaurants.auth.exceptions.AuthException;
import com.restaurants.auth.models.Utilisateur;
import com.restaurants.auth.repositories.UtilisateurRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service principal d'authentification.
 * Gère la connexion et l'inscription des utilisateurs.
 *
 * COURS INF1013 - Architecture :
 * "Controller → Service → Repository"
 * "Le service contient la logique métier"
 */
@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder encodeurMotDePasse;
    private final JwtService jwtService;
    private final AuthenticationManager gestionnaireAuthentification;

    public AuthService(
            UtilisateurRepository utilisateurRepository,
            PasswordEncoder encodeurMotDePasse,
            JwtService jwtService,
            AuthenticationManager gestionnaireAuthentification
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.encodeurMotDePasse = encodeurMotDePasse;
        this.jwtService = jwtService;
        this.gestionnaireAuthentification = gestionnaireAuthentification;
    }

    /**
     * Connecte un utilisateur et génère un jeton JWT.
     *
     * @param requete les informations de connexion (courriel + mot de passe)
     * @return la réponse d'authentification avec le jeton JWT
     * @throws AuthException si les identifiants sont incorrects
     */
    public AuthResponseDTO connexion(LoginRequestDTO requete) {
        try {
            // Authentifier l'utilisateur via Spring Security
            gestionnaireAuthentification.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requete.getCourriel(),
                            requete.getMotDePasse()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new AuthException("Courriel ou mot de passe incorrect");
        }

        // Charger l'utilisateur depuis la base de données
        Utilisateur utilisateur = utilisateurRepository.findByCourriel(requete.getCourriel())
                .orElseThrow(() -> new AuthException("Utilisateur introuvable"));

        // Ajouter le rôle dans les claims du JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", utilisateur.getRole().name());
        claims.put("prenom", utilisateur.getPrenom());
        claims.put("nom", utilisateur.getNom());

        // Générer le jeton JWT
        String jeton = jwtService.genererJetonAvecClaims(claims, utilisateur);

        return AuthResponseDTO.builder()
                .token(jeton)
                .typeToken("Bearer")
                .utilisateur(UtilisateurDTO.depuisEntite(utilisateur))
                .message("Connexion réussie")
                .build();
    }

    /**
     * Inscrit un nouvel utilisateur et génère un jeton JWT.
     *
     * @param requete les informations d'inscription
     * @return la réponse d'authentification avec le jeton JWT
     * @throws AuthException si le courriel est déjà utilisé
     */
    public AuthResponseDTO inscription(RegisterRequestDTO requete) {
        // Vérifier si le courriel existe déjà
        if (utilisateurRepository.existsByCourriel(requete.getCourriel())) {
            throw new AuthException(
                    "Un compte existe déjà avec le courriel : " + requete.getCourriel(),
                    HttpStatus.CONFLICT
            );
        }

        // Déterminer le rôle (CLIENT par défaut)
        Utilisateur.RoleUtilisateur role = Utilisateur.RoleUtilisateur.CLIENT;
        if (requete.getRole() != null) {
            try {
                role = Utilisateur.RoleUtilisateur.valueOf(requete.getRole().toUpperCase());
                // Interdire la création d'un compte ADMIN via l'API publique
                if (role == Utilisateur.RoleUtilisateur.ADMIN) {
                    role = Utilisateur.RoleUtilisateur.CLIENT;
                }
            } catch (IllegalArgumentException e) {
                // Rôle invalide, utiliser CLIENT par défaut
                role = Utilisateur.RoleUtilisateur.CLIENT;
            }
        }

        // Créer le nouvel utilisateur
        Utilisateur nouvelUtilisateur = Utilisateur.builder()
                .prenom(requete.getPrenom())
                .nom(requete.getNom())
                .courriel(requete.getCourriel())
                .motDePasse(encodeurMotDePasse.encode(requete.getMotDePasse()))
                .telephone(requete.getTelephone())
                .adresse(requete.getAdresse())
                .role(role)
                .build();

        // Sauvegarder l'utilisateur
        Utilisateur utilisateurSauvegarde = utilisateurRepository.save(nouvelUtilisateur);

        // Ajouter le rôle dans les claims du JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", utilisateurSauvegarde.getRole().name());
        claims.put("prenom", utilisateurSauvegarde.getPrenom());
        claims.put("nom", utilisateurSauvegarde.getNom());

        // Générer le jeton JWT
        String jeton = jwtService.genererJetonAvecClaims(claims, utilisateurSauvegarde);

        return AuthResponseDTO.builder()
                .token(jeton)
                .typeToken("Bearer")
                .utilisateur(UtilisateurDTO.depuisEntite(utilisateurSauvegarde))
                .message("Inscription réussie")
                .build();
    }

    /**
     * Valide un jeton JWT et retourne les informations de l'utilisateur.
     *
     * @param jeton le jeton JWT à valider
     * @return les informations de l'utilisateur
     * @throws AuthException si le jeton est invalide
     */
    public UtilisateurDTO validerJeton(String jeton) {
        try {
            String courriel = jwtService.extraireCourriel(jeton);
            Utilisateur utilisateur = utilisateurRepository.findByCourriel(courriel)
                    .orElseThrow(() -> new AuthException("Utilisateur introuvable"));

            if (!jwtService.estJetonValide(jeton, utilisateur)) {
                throw new AuthException("Jeton JWT invalide ou expiré");
            }

            return UtilisateurDTO.depuisEntite(utilisateur);
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthException("Jeton JWT invalide : " + e.getMessage());
        }
    }
}
