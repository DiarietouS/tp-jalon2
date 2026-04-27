import { HttpInterceptorFn } from '@angular/common/http';

/**
 * Intercepteur JWT fonctionnel.
 * COURS INF1013 (HttpClient): "Les intercepteurs permettent d'inspecter et
 * transformer les requêtes HTTP avant qu'elles ne soient envoyées au serveur."
 *
 * Ajoute automatiquement le header Authorization: Bearer <token>
 * à toutes les requêtes si un token JWT est présent dans le localStorage.
 */
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('jwtToken');

  if (token) {
    const authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    return next(authReq);
  }

  return next(req);
};
