package com.restaurants.business.security;

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

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest requete,
            @NonNull HttpServletResponse reponse,
            @NonNull FilterChain chaineFiltre
    ) throws ServletException, IOException {
        final String enteteAuthorization = requete.getHeader("Authorization");

        if (enteteAuthorization == null || !enteteAuthorization.startsWith("Bearer ")) {
            chaineFiltre.doFilter(requete, reponse);
            return;
        }

        String jwt = enteteAuthorization.substring(7);
        try {
            String courriel = jwtService.extraireCourriel(jwt);
            if (courriel != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtService.estTokenValide(jwt, courriel)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            courriel, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token invalide - continuer sans authentification
        }

        chaineFiltre.doFilter(requete, reponse);
    }
}
