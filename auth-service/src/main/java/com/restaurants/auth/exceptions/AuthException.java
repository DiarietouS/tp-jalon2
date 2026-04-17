package com.restaurants.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception personnalisée pour les erreurs d'authentification.
 * Retourne un code HTTP 401 (Non autorisé) par défaut.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthException extends RuntimeException {

    /** Code HTTP associé à l'erreur */
    private final HttpStatus statut;

    /**
     * Constructeur avec message d'erreur.
     *
     * @param message description de l'erreur en français
     */
    public AuthException(String message) {
        super(message);
        this.statut = HttpStatus.UNAUTHORIZED;
    }

    /**
     * Constructeur avec message et code HTTP personnalisé.
     *
     * @param message description de l'erreur en français
     * @param statut  code HTTP de l'erreur
     */
    public AuthException(String message, HttpStatus statut) {
        super(message);
        this.statut = statut;
    }

    /**
     * Retourne le code HTTP associé à l'erreur.
     *
     * @return le statut HTTP
     */
    public HttpStatus getStatut() {
        return statut;
    }
}
