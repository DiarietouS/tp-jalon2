package com.restaurants.auth.dto;

/**
 * Réponse à une demande de réinitialisation de mot de passe.
 * NOTE: En production, le jeton serait envoyé par email (ex: via Spring Mail)
 * et ne serait PAS retourné dans la réponse HTTP.
 * Pour le développement, il est retourné ici pour faciliter les tests.
 */
public class MotDePasseOubliResponseDTO {
    private String message;
    private String jeton;      // Dev uniquement — serait envoyé par email en prod
    private String expireLe;

    public MotDePasseOubliResponseDTO(String message, String jeton, String expireLe) {
        this.message = message;
        this.jeton = jeton;
        this.expireLe = expireLe;
    }

    public String getMessage() { return message; }
    public String getJeton() { return jeton; }
    public String getExpireLe() { return expireLe; }
}
