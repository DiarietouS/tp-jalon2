import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';


export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);


  if (authService.estConnecteSignal()) {
    // L'utilisateur est connecté, autoriser l'accès
    return true;
  }

  // L'utilisateur n'est pas connecté, rediriger vers la page de connexion

  router.navigate(['/auth'], {
    queryParams: { returnUrl: state.url }
  });

  return false;
};
