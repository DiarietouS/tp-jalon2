package com.restaurants.auth.services;

import com.restaurants.auth.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service de gestion des jetons JWT.
 * Génère, valide et extrait les informations des jetons JWT.
 *
 * COURS INF1013 - JWT :
 * "Un JWT est composé de 3 parties : Header.Payload.Signature"
 * "Header : algorithme de signature (HMAC256)"
 * "Payload : données (claims) : sub, iat, exp, rôle..."
 * "Signature : HMAC256(base64(header) + '.' + base64(payload), secret)"
 */
@Service
public class JwtService {

    private final JwtConfig jwtConfig;

    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    /**
     * Génère un jeton JWT pour un utilisateur.
     *
     * @param detailsUtilisateur les détails de l'utilisateur
     * @return le jeton JWT signé
     */
    public String genererJeton(UserDetails detailsUtilisateur) {
        return genererJetonAvecClaims(new HashMap<>(), detailsUtilisateur);
    }

    /**
     * Génère un jeton JWT avec des claims supplémentaires.
     *
     * @param claimsSupplementaires claims additionnels à inclure
     * @param detailsUtilisateur    les détails de l'utilisateur
     * @return le jeton JWT signé
     */
    public String genererJetonAvecClaims(
            Map<String, Object> claimsSupplementaires,
            UserDetails detailsUtilisateur
    ) {
        return Jwts.builder()
                // Claims supplémentaires (ex: rôle)
                .claims(claimsSupplementaires)
                // Sujet : courriel de l'utilisateur
                .subject(detailsUtilisateur.getUsername())
                // Date d'émission
                .issuedAt(new Date(System.currentTimeMillis()))
                // Date d'expiration
                .expiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
                // Signer avec HMAC256
                .signWith(jwtConfig.getCleSignature())
                .compact();
    }

    /**
     * Valide un jeton JWT pour un utilisateur donné.
     *
     * @param jeton              le jeton à valider
     * @param detailsUtilisateur les détails de l'utilisateur attendu
     * @return true si le jeton est valide
     */
    public boolean estJetonValide(String jeton, UserDetails detailsUtilisateur) {
        final String courriel = extraireCourriel(jeton);
        return courriel.equals(detailsUtilisateur.getUsername()) && !estJetonExpire(jeton);
    }

    /**
     * Extrait le courriel (sujet) du jeton JWT.
     *
     * @param jeton le jeton JWT
     * @return le courriel de l'utilisateur
     */
    public String extraireCourriel(String jeton) {
        return extraireClaim(jeton, Claims::getSubject);
    }

    /**
     * Extrait la date d'expiration du jeton JWT.
     *
     * @param jeton le jeton JWT
     * @return la date d'expiration
     */
    public Date extraireExpiration(String jeton) {
        return extraireClaim(jeton, Claims::getExpiration);
    }

    /**
     * Extrait un claim spécifique du jeton JWT.
     *
     * @param jeton          le jeton JWT
     * @param extracteurClaim la fonction d'extraction du claim
     * @param <T>            le type du claim
     * @return la valeur du claim
     */
    public <T> T extraireClaim(String jeton, Function<Claims, T> extracteurClaim) {
        final Claims claims = extraireTousClaims(jeton);
        return extracteurClaim.apply(claims);
    }

    /**
     * Extrait tous les claims du jeton JWT.
     *
     * @param jeton le jeton JWT
     * @return les claims du jeton
     */
    private Claims extraireTousClaims(String jeton) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getCleSignature())
                .build()
                .parseSignedClaims(jeton)
                .getPayload();
    }

    /**
     * Vérifie si le jeton JWT est expiré.
     *
     * @param jeton le jeton JWT
     * @return true si le jeton est expiré
     */
    private boolean estJetonExpire(String jeton) {
        return extraireExpiration(jeton).before(new Date());
    }
}
