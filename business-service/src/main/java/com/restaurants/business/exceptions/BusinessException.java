package com.restaurants.business.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception personnalisée pour les erreurs métier.
 * Retourne un code HTTP 400 (Mauvaise requête) par défaut.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {

    /** Code HTTP associé à l'erreur */
    private final HttpStatus statut;

    /**
     * Constructeur avec message d'erreur.
     *
     * @param message description de l'erreur en français
     */
    public BusinessException(String message) {
        super(message);
        this.statut = HttpStatus.BAD_REQUEST;
    }

    /**
     * Constructeur avec message et code HTTP personnalisé.
     *
     * @param message description de l'erreur en français
     * @param statut  code HTTP de l'erreur
     */
    public BusinessException(String message, HttpStatus statut) {
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
