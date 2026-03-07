import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * ============================================================================
 * GUARD D'AUTHENTIFICATION - authGuard
 * ============================================================================
 * 
 * COURS INF1013 - GUARDS (CanActivate)
 * ------------------------------------
 * "Les gardes sont des fonctions qui permettent de contrôler l'accès
 * à certaines routes de l'application."
 * 
 * "CanActivate: Détermine si une route peut être activée.
 * Si le garde retourne false, la navigation est annulée."
 * 
 * "Les gardes modernes utilisent des fonctions (functional guards)
 * plutôt que des classes, ce qui simplifie l'injection de dépendances."
 * 
 * UTILISATION:
 * Dans app.routes.ts:
 * { path: 'panier', component: Panier, canActivate: [authGuard] }
 * 
 * @see Diapo: Guards, Protection des routes
 * ============================================================================
 */
export const authGuard: CanActivateFn = (route, state) => {
  /**
   * COURS INF1013 - INJECT():
   * "inject() permet d'injecter des dépendances dans des fonctions
   * en dehors du constructeur d'un composant ou service."
   */
  const authService = inject(AuthService);
  const router = inject(Router);

  /**
   * COURS INF1013 - LECTURE DE SIGNAL:
   * "La valeur d'un signal se lit en appelant sa fonction d'accès (getter)"
   * 
   * On utilise le signal estConnecteSignal() pour vérifier l'authentification.
   */
  if (authService.estConnecteSignal()) {
    // L'utilisateur est connecté, autoriser l'accès
    return true;
  }

  // L'utilisateur n'est pas connecté, rediriger vers la page de connexion
  // On sauvegarde l'URL demandée pour y retourner après connexion
  router.navigate(['/auth'], { 
    queryParams: { returnUrl: state.url } 
  });
  
  return false;
};
