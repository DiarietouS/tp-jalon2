import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';


export const restaurateurGuard: CanActivateFn = (route, state) => {

  const authService = inject(AuthService);
  const router = inject(Router);


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
    // sinon rediriger vers l'accueil
    router.navigate(['/restaurants']);
    return false;
  }

  // si oui autoriser l'accès
  return true;
};
