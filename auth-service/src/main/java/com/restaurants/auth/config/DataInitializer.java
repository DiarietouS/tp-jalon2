package com.restaurants.auth.config;

import com.restaurants.auth.entity.UtilisateurEntity;
import com.restaurants.auth.repository.UtilisateurRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        creerUtilisateurSiAbsent("Jean", "Dupont", "client@test.com", "password123", "514-111-1111", "100 Rue Client, Montréal", "client");
        creerUtilisateurSiAbsent("Marie", "Tremblay", "resto@test.com", "password123", "819-222-2222", "200 Rue Resto, Trois-Rivières", "restaurateur");
        creerUtilisateurSiAbsent("Pierre", "Martin", "livreur@test.com", "password123", "438-333-3333", "300 Rue Livreur, Québec", "livreur");
    }

    private void creerUtilisateurSiAbsent(String prenom, String nom, String courriel,
                                           String motDePasse, String telephone, String adresse, String role) {
        if (utilisateurRepository.existsByCourriel(courriel)) {
            return;
        }
        UtilisateurEntity utilisateur = new UtilisateurEntity();
        utilisateur.setPrenom(prenom);
        utilisateur.setNom(nom);
        utilisateur.setCourriel(courriel);
        utilisateur.setMotDePasse(passwordEncoder.encode(motDePasse));
        utilisateur.setTelephone(telephone);
        utilisateur.setAdresse(adresse);
        utilisateur.setRole(role);
        utilisateurRepository.save(utilisateur);
    }
}
