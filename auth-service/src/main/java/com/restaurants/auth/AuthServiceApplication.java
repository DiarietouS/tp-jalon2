package com.restaurants.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée principal du microservice d'authentification.
 * Ce service gère la création de comptes, la connexion et la génération de jetons JWT.
 * Port : 8081
 */
@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
