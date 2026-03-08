import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';


export const livreurGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const utilisateur = authService.utilisateurCourant();

  if (!utilisateur) {
    router.navigate(['/auth'], { queryParams: { returnUrl: state.url } });
    return false;
  }

  if (utilisateur.role !== 'livreur') {
    router.navigate(['/restaurants']);
    return false;
  }

  return true;
};
