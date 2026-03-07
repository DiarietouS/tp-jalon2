import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * ============================================================================
 * GUARD DE RÔLE RESTAURATEUR - restaurateurGuard
 * ============================================================================
 * 
 * COURS INF1013 - GUARDS (CanActivate)
 * ------------------------------------
 * "Les gardes permettent de protéger des routes selon des conditions
 * spécifiques, comme le rôle de l'utilisateur."
 * 
 * "Un garde peut vérifier plusieurs conditions:
 * - L'utilisateur est-il connecté?
 * - A-t-il le bon rôle?
 * - A-t-il les permissions nécessaires?"
 * 
 * COMPOSITION DE GARDES:
 * "On peut combiner plusieurs gardes:
 * canActivate: [authGuard, restaurateurGuard]"
 * 
 * Ce garde vérifie que l'utilisateur:
 * 1. Est connecté
 * 2. A le rôle 'restaurateur'
 * 
 * @see Diapo: Guards, Protection des routes par rôle
 * ============================================================================
 */
export const restaurateurGuard: CanActivateFn = (route, state) => {
  /**
   * COURS INF1013 - INJECT():
   * "inject() permet d'injecter des dépendances dans des fonctions"
   */
  const authService = inject(AuthService);
  const router = inject(Router);

  /**
   * COURS INF1013 - SIGNALS CALCULÉS:
   * "On utilise les signals pour vérifier l'état de l'authentification
   * et le rôle de l'utilisateur de manière réactive."
   */
  const utilisateur = authService.utilisateurCourant();

  // Vérifier si l'utilisateur est connecté
  if (!utilisateur) {
    // Rediriger vers la page de connexion
    router.navigate(['/auth'], { 
      queryParams: { returnUrl: state.url } 
    });
    return false;
  }

  // Vérifier si l'utilisateur a le rôle restaurateur
  if (utilisateur.role !== 'restaurateur') {
    // L'utilisateur n'a pas le bon rôle, rediriger vers l'accueil
    router.navigate(['/restaurants']);
    return false;
  }

  // L'utilisateur est un restaurateur, autoriser l'accès
  return true;
};
