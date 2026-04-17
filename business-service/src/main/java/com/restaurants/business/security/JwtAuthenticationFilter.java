package com.restaurants.business.security;

import com.restaurants.business.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtre JWT pour le microservice métier.
 * Valide le jeton JWT et établit le contexte de sécurité.
 *
 * COURS INF1013 - Sécurité JWT :
 * "Le business-service valide le JWT généré par l'auth-service"
 * "Même clé secrète partagée entre les deux services"
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Préfixe de l'en-tête d'autorisation */
    private static final String PREFIXE_BEARER = "Bearer ";

    /** Nom de l'en-tête d'autorisation */
    private static final String EN_TETE_AUTHORIZATION = "Authorization";

    private final JwtConfig jwtConfig;

    public JwtAuthenticationFilter(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
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

        // Extraire le jeton JWT
        final String jeton = enTeteAuthorization.substring(PREFIXE_BEARER.length());

        try {
            // Analyser et valider le jeton JWT
            Claims claims = Jwts.parser()
                    .verifyWith(jwtConfig.getCleSignature())
                    .build()
                    .parseSignedClaims(jeton)
                    .getPayload();

            // Extraire le courriel (sujet)
            String courriel = claims.getSubject();

            // Extraire le rôle du jeton
            String role = claims.get("role", String.class);

            // Créer l'autorité Spring Security
            List<SimpleGrantedAuthority> autorites = role != null
                    ? List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    : List.of();

            // Établir l'authentification dans le contexte de sécurité
            if (courriel != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentification =
                        new UsernamePasswordAuthenticationToken(courriel, null, autorites);
                SecurityContextHolder.getContext().setAuthentication(authentification);
            }

        } catch (JwtException e) {
            // Jeton invalide : laisser passer sans authentification
            // La sécurité Spring refusera l'accès aux routes protégées
        }

        filterChain.doFilter(request, response);
    }
}
