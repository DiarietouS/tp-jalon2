import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

import { routes } from './app.routes';
import { jwtInterceptor } from './core/interceptors';

/**
 * Configuration principale de l'application Angular.
 *
 * COURS INF1013 - Intercepteurs HTTP :
 * "withInterceptors() enregistre les intercepteurs fonctionnels"
 * "Le jwtInterceptor ajoute automatiquement le jeton JWT à chaque requête"
 */
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    // Enregistrer l'intercepteur JWT pour ajouter le token à chaque requête HTTP
    provideHttpClient(withFetch(), withInterceptors([jwtInterceptor])),
    provideAnimationsAsync()
  ]
};
