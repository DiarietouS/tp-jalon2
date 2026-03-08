import { Routes } from '@angular/router';


import { authGuard, restaurateurGuard, livreurGuard } from './core/guards';


import { Auth } from './features/auth/auth';


import { Restaurants } from './features/client/restaurants/restaurants';
import { RestaurantMenu } from './features/client/restaurant-menu/restaurant-menu';
import { Panier } from './features/client/panier/panier';
import { Commandes } from './features/client/commandes/commandes';
import { Profil } from './features/client/profil/profil';


import { Gestion } from './features/restaurateur/gestion/gestion';


import { CommandesLivreur } from './features/livreur/commandes-livreur/commandes-livreur';


export const routes: Routes = [
  // Redirection vers restaurants
  { path: '', redirectTo: 'restaurants', pathMatch: 'full' },

  // Routes Auth Accessibles à tous
  { path: 'auth', component: Auth },
  { path: 'login', redirectTo: 'auth', pathMatch: 'full' },
  { path: 'register', redirectTo: 'auth', pathMatch: 'full' },

  // Routes Client
  { path: 'restaurants', component: Restaurants },
  { path: 'accueil', redirectTo: 'restaurants', pathMatch: 'full' },
  { path: 'favoris', redirectTo: 'restaurants', pathMatch: 'full' },
  { path: 'restaurant/:id', component: RestaurantMenu },


  { path: 'panier', component: Panier, canActivate: [authGuard] },
  { path: 'commandes', component: Commandes, canActivate: [authGuard] },
  { path: 'profil', component: Profil, canActivate: [authGuard] },


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

  // Routes Livreur
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
