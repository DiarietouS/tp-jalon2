package com.restaurants.business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée principal du microservice métier.
 * Ce service gère les restaurants, les plats, les commandes et les livraisons.
 * Port : 8082
 */
@SpringBootApplication
public class BusinessServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessServiceApplication.class, args);
    }
}
