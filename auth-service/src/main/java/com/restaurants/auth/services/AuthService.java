package com.restaurants.auth.services;

import com.restaurants.auth.dtos.AuthResponseDTO;
import com.restaurants.auth.dtos.ConnexionRequestDTO;
import com.restaurants.auth.dtos.InscriptionRequestDTO;
import com.restaurants.auth.dtos.UtilisateurDTO;
import com.restaurants.auth.exceptions.AuthException;
import com.restaurants.auth.models.Utilisateur;
import com.restaurants.auth.repositories.UtilisateurRepository;
import com.restaurants.auth.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder encodeurMotDePasse;
    private final JwtService jwtService;
    private final AuthenticationManager gestionnaireAuthentification;

    public AuthService(UtilisateurRepository utilisateurRepository,
                       PasswordEncoder encodeurMotDePasse,
                       JwtService jwtService,
                       AuthenticationManager gestionnaireAuthentification) {
        this.utilisateurRepository = utilisateurRepository;
        this.encodeurMotDePasse = encodeurMotDePasse;
        this.jwtService = jwtService;
        this.gestionnaireAuthentification = gestionnaireAuthentification;
    }

    public AuthResponseDTO connexion(ConnexionRequestDTO requete) {
        try {
            gestionnaireAuthentification.authenticate(
                    new UsernamePasswordAuthenticationToken(requete.getCourriel(), requete.getMotDePasse())
            );
        } catch (AuthenticationException e) {
            throw new AuthException("Courriel ou mot de passe invalide");
        }

        Utilisateur utilisateur = utilisateurRepository.findByCourriel(requete.getCourriel())
                .orElseThrow(() -> new AuthException("Utilisateur non trouvé"));

        String token = jwtService.genererToken(utilisateur);
        return new AuthResponseDTO(token, utilisateur.getId(), utilisateur.getCourriel(),
                utilisateur.getPrenom(), utilisateur.getNom(), utilisateur.getRole().name());
    }

    public AuthResponseDTO inscription(InscriptionRequestDTO requete) {
        if (utilisateurRepository.existsByCourriel(requete.getCourriel())) {
            throw new AuthException("Ce courriel est déjà utilisé");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setPrenom(requete.getPrenom());
        utilisateur.setNom(requete.getNom());
        utilisateur.setCourriel(requete.getCourriel());
        utilisateur.setMotDePasse(encodeurMotDePasse.encode(requete.getMotDePasse()));
        utilisateur.setTelephone(requete.getTelephone());
        utilisateur.setAdresse(requete.getAdresse());

        Utilisateur.Role role = Utilisateur.Role.CLIENT;
        if (requete.getRole() != null && !requete.getRole().isEmpty()) {
            try {
                role = Utilisateur.Role.valueOf(requete.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                role = Utilisateur.Role.CLIENT;
            }
        }
        utilisateur.setRole(role);

        utilisateurRepository.save(utilisateur);
        String token = jwtService.genererToken(utilisateur);
        return new AuthResponseDTO(token, utilisateur.getId(), utilisateur.getCourriel(),
                utilisateur.getPrenom(), utilisateur.getNom(), utilisateur.getRole().name());
    }

    public UtilisateurDTO obtenirUtilisateurCourant(String courriel) {
        Utilisateur utilisateur = utilisateurRepository.findByCourriel(courriel)
                .orElseThrow(() -> new AuthException("Utilisateur non trouvé"));

        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setId(utilisateur.getId());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setNom(utilisateur.getNom());
        dto.setCourriel(utilisateur.getCourriel());
        dto.setTelephone(utilisateur.getTelephone());
        dto.setAdresse(utilisateur.getAdresse());
        dto.setRole(utilisateur.getRole().name());
        return dto;
    }
}
