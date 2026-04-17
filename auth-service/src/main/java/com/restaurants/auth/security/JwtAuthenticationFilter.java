package com.restaurants.auth.security;

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

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UtilisateurDetailsService utilisateurDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UtilisateurDetailsService utilisateurDetailsService) {
        this.jwtService = jwtService;
        this.utilisateurDetailsService = utilisateurDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest requete,
            @NonNull HttpServletResponse reponse,
            @NonNull FilterChain chaineFiltre
    ) throws ServletException, IOException {
        final String enteteAuthorization = requete.getHeader("Authorization");
        final String jwt;
        final String courriel;

        if (enteteAuthorization == null || !enteteAuthorization.startsWith("Bearer ")) {
            chaineFiltre.doFilter(requete, reponse);
            return;
        }

        jwt = enteteAuthorization.substring(7);
        try {
            courriel = jwtService.extraireCourriel(jwt);
        } catch (Exception e) {
            chaineFiltre.doFilter(requete, reponse);
            return;
        }

        if (courriel != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = utilisateurDetailsService.loadUserByUsername(courriel);
            if (jwtService.estTokenValide(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(requete));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chaineFiltre.doFilter(requete, reponse);
    }
}
