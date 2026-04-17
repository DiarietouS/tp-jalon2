package com.restaurants.auth.security;

import com.restaurants.auth.services.JwtService;
import com.restaurants.auth.services.UtilisateurDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre d'authentification JWT.
 * Intercepte chaque requête HTTP pour valider le jeton JWT dans l'en-tête Authorization.
 *
 * COURS INF1013 - Sécurité JWT :
 * "OncePerRequestFilter : exécuté une seule fois par requête"
 * "Authorization: Bearer <token>"
 * "Le filtre valide le jeton et établit le contexte de sécurité"
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Préfixe de l'en-tête d'autorisation */
    private static final String PREFIXE_BEARER = "Bearer ";

    /** Nom de l'en-tête d'autorisation */
    private static final String EN_TETE_AUTHORIZATION = "Authorization";

    private final JwtService jwtService;
    private final UtilisateurDetailsService utilisateurDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UtilisateurDetailsService utilisateurDetailsService
    ) {
        this.jwtService = jwtService;
        this.utilisateurDetailsService = utilisateurDetailsService;
    }

    /**
     * Filtre principal : valide le jeton JWT et établit l'authentification.
     *
     * @param request     la requête HTTP entrante
     * @param response    la réponse HTTP
     * @param filterChain la chaîne de filtres
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Extraire l'en-tête Authorization
        final String enTeteAuthorization = request.getHeader(EN_TETE_AUTHORIZATION);

        // Si l'en-tête est absent ou ne commence pas par "Bearer ", passer au filtre suivant
        if (enTeteAuthorization == null || !enTeteAuthorization.startsWith(PREFIXE_BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraire le jeton JWT (sans le préfixe "Bearer ")
        final String jeton = enTeteAuthorization.substring(PREFIXE_BEARER.length());

        // Extraire le courriel du jeton
        final String courriel = jwtService.extraireCourriel(jeton);

        // Si le courriel est présent et l'utilisateur n'est pas encore authentifié
        if (courriel != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Charger les détails de l'utilisateur depuis la base de données
            UserDetails detailsUtilisateur = utilisateurDetailsService.loadUserByUsername(courriel);

            // Valider le jeton
            if (jwtService.estJetonValide(jeton, detailsUtilisateur)) {
                // Créer le jeton d'authentification Spring Security
                UsernamePasswordAuthenticationToken jetonAuthentification =
                        new UsernamePasswordAuthenticationToken(
                                detailsUtilisateur,
                                null,
                                detailsUtilisateur.getAuthorities()
                        );

                // Ajouter les détails de la requête
                jetonAuthentification.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Définir l'authentification dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(jetonAuthentification);
            }
        }

        // Passer au filtre suivant
        filterChain.doFilter(request, response);
    }
}
