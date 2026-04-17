import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpEvent } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable } from 'rxjs';

/**
 * ============================================================================
 * INTERCEPTEUR JWT - JwtInterceptor
 * ============================================================================
 *
 * COURS INF1013 - Intercepteurs HTTP :
 * "Les intercepteurs HTTP interceptent toutes les requêtes sortantes
 * pour ajouter le jeton JWT dans l'en-tête Authorization."
 *
 * "Authorization: Bearer <token>"
 *
 * Cet intercepteur fonctionne avec le pattern fonctionnel Angular (v15+).
 * Il est enregistré dans app.config.ts avec withInterceptors().
 *
 * @see COURS INF1013 : Intercepteurs, JWT
 * ============================================================================
 */
export const jwtInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {

  // Récupérer le jeton JWT depuis le localStorage
  const jeton = localStorage.getItem('jwt_token');

  // Si un jeton existe, l'ajouter à l'en-tête Authorization
  if (jeton) {
    // Cloner la requête avec l'en-tête Authorization
    const requeteAvecJeton = req.clone({
      setHeaders: {
        Authorization: `Bearer ${jeton}`
      }
    });
    return next(requeteAvecJeton);
  }

  // Sinon, passer la requête sans modification
  return next(req);
};
