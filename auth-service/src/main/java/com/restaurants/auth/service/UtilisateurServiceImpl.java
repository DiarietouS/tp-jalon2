package com.restaurants.auth.service;

import com.restaurants.auth.dto.SignUpRequestDTO;
import com.restaurants.auth.entity.UtilisateurEntity;
import com.restaurants.auth.repository.UtilisateurRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implémentation du service utilisateur.
 * COURS INF1013: le @Service est injectable et utilise le @Repository.
 * Le mot de passe est encodé avec BCryptPasswordEncoder avant insertion.
 */
@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository,
                                  PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UtilisateurEntity creerUtilisateur(SignUpRequestDTO dto) {
        if (utilisateurRepository.existsByCourriel(dto.getCourriel())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Courriel déjà utilisé");
        }

        UtilisateurEntity entity = new UtilisateurEntity();
        entity.setPrenom(dto.getPrenom());
        entity.setNom(dto.getNom());
        entity.setCourriel(dto.getCourriel());
        // COURS INF1013: "le mot de passe doit être encodé avant insertion en BD"
        entity.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        entity.setTelephone(dto.getTelephone());
        entity.setAdresse(dto.getAdresse());
        entity.setRole(dto.getRole() != null ? dto.getRole() : "client");

        return utilisateurRepository.save(entity);
    }
}
