import { Routes } from '@angular/router';

// Guards pour la protection des routes
import { authGuard, restaurateurGuard, livreurGuard } from './core/guards';

// Composants Auth (unifié: connexion + inscription)
import { Auth } from './features/auth/auth';

// Composants Client
import { Restaurants } from './features/client/restaurants/restaurants';
import { RestaurantMenu } from './features/client/restaurant-menu/restaurant-menu';
import { Panier } from './features/client/panier/panier';
import { Commandes } from './features/client/commandes/commandes';
import { Profil } from './features/client/profil/profil';

// Composants Restaurateur
import { Gestion } from './features/restaurateur/gestion/gestion';

// Composants Livreur
import { CommandesLivreur } from './features/livreur/commandes-livreur/commandes-livreur';

/**
 * ============================================================================
 * CONFIGURATION DES ROUTES
 * ============================================================================
 * 
 * COURS INF1013 - GUARDS (CanActivate)
 * ------------------------------------
 * "Les gardes sont des fonctions qui permettent de contrôler l'accès
 * à certaines routes de l'application."
 * 
 * "canActivate: [authGuard] - Vérifie si l'utilisateur peut accéder à la route"
 * 
 * ROUTES PROTÉGÉES:
 * - /panier, /commandes, /profil → Nécessitent une connexion (authGuard)
 * - /restaurateur/* → Nécessitent le rôle restaurateur (restaurateurGuard)
 * 
 * @see Diapo: Guards, Protection des routes
 * ============================================================================
 */
export const routes: Routes = [
  // Redirection par défaut vers restaurants
  { path: '', redirectTo: 'restaurants', pathMatch: 'full' },

  // Routes Auth (unifié) - Accessibles à tous
  { path: 'auth', component: Auth },
  { path: 'login', redirectTo: 'auth', pathMatch: 'full' },
  { path: 'register', redirectTo: 'auth', pathMatch: 'full' },

  // Routes Client publiques
  { path: 'restaurants', component: Restaurants },
  { path: 'accueil', redirectTo: 'restaurants', pathMatch: 'full' },
  { path: 'favoris', redirectTo: 'restaurants', pathMatch: 'full' },
  { path: 'restaurant/:id', component: RestaurantMenu },

  /**
   * COURS INF1013 - canActivate:
   * "canActivate: [authGuard] vérifie que l'utilisateur est connecté
   * avant d'autoriser l'accès à la route."
   */
  { path: 'panier', component: Panier, canActivate: [authGuard] },
  { path: 'commandes', component: Commandes, canActivate: [authGuard] },
  { path: 'profil', component: Profil, canActivate: [authGuard] },

  /**
   * COURS INF1013 - GARDE DE RÔLE:
   * "restaurateurGuard vérifie que l'utilisateur est connecté ET
   * qu'il possède le rôle 'restaurateur'."
   */
  {
    path: 'restaurateur',
    canActivate: [restaurateurGuard],
    children: [
      { path: 'gestion', component: Gestion },
      { path: 'livraison', redirectTo: '/commandes', pathMatch: 'full' },
      { path: 'dishes', redirectTo: 'gestion', pathMatch: 'full' },
      { path: 'restaurant', redirectTo: 'gestion', pathMatch: 'full' },
      { path: '', redirectTo: 'gestion', pathMatch: 'full' }
    ]
  },

  // Routes Livreur (fermeture de commande après livraison)
  {
    path: 'livreur',
    canActivate: [livreurGuard],
    children: [
      { path: 'commandes', component: CommandesLivreur },
      { path: '', redirectTo: 'commandes', pathMatch: 'full' }
    ]
  },

  // Route par défaut
  { path: '**', redirectTo: 'restaurants' }
];
