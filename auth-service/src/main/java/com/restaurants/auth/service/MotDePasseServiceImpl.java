package com.restaurants.auth.service;

import com.restaurants.auth.dto.MotDePasseOubliResponseDTO;
import com.restaurants.auth.entity.JetonReinitPassword;
import com.restaurants.auth.entity.UtilisateurEntity;
import com.restaurants.auth.repository.JetonReinitPasswordRepository;
import com.restaurants.auth.repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class MotDePasseServiceImpl implements MotDePasseService {

    private static final int EXPIRATION_HEURES = 1;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final UtilisateurRepository utilisateurRepository;
    private final JetonReinitPasswordRepository jetonRepository;
    private final PasswordEncoder passwordEncoder;

    public MotDePasseServiceImpl(UtilisateurRepository utilisateurRepository,
                                  JetonReinitPasswordRepository jetonRepository,
                                  PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.jetonRepository = jetonRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public MotDePasseOubliResponseDTO demanderReinitialisation(String courriel) {
        // Vérifie que l'utilisateur existe (sans révéler si le courriel est enregistré)
        if (!utilisateurRepository.existsByCourriel(courriel)) {
            // Sécurité: même réponse qu'un succès pour ne pas divulguer les comptes
            return messageSansJeton();
        }

        // Supprime les anciens jetons pour ce courriel
        jetonRepository.deleteByCourriel(courriel);

        // Génère un jeton UUID sécurisé
        String jeton = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime expireLe = LocalDateTime.now().plusHours(EXPIRATION_HEURES);

        jetonRepository.save(new JetonReinitPassword(courriel, jeton, expireLe));

        // NOTE: En production, envoyer le jeton par email via Spring Mail
        // ex: mailService.envoyerCourrielReinit(courriel, jeton);
        return new MotDePasseOubliResponseDTO(
                "Un token de réinitialisation a été généré. En production il serait envoyé par email.",
                jeton,
                expireLe.format(FORMATTER)
        );
    }

    @Override
    @Transactional
    public void reinitialiserMotDePasse(String jeton, String nouveauMotDePasse) {
        JetonReinitPassword jetonEntity = jetonRepository.findByJeton(jeton)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Token invalide ou inexistant"));

        if (jetonEntity.isUtilise()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce token a déjà été utilisé");
        }
        if (jetonEntity.estExpire()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Token expiré. Veuillez faire une nouvelle demande.");
        }
        if (nouveauMotDePasse == null || nouveauMotDePasse.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Le nouveau mot de passe doit contenir au moins 6 caractères");
        }

        UtilisateurEntity utilisateur = utilisateurRepository.findByCourriel(jetonEntity.getCourriel())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Utilisateur introuvable"));

        utilisateur.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        utilisateurRepository.save(utilisateur);

        jetonEntity.setUtilise(true);
        jetonRepository.save(jetonEntity);
    }

    private MotDePasseOubliResponseDTO messageSansJeton() {
        return new MotDePasseOubliResponseDTO(
                "Si ce courriel est associé à un compte, un token de réinitialisation sera généré.",
                null,
                null
        );
    }
}
