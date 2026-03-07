import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { Header } from './shared/components/header/header';
import { BottomNav } from './shared/components/bottom-nav/bottom-nav';
import { AuthService } from './core/services/auth.service';
import { RechercheService } from './core/services/recherche.service';
import { filter } from 'rxjs/operators';

/**
 * ============================================================================
 * COMPOSANT RACINE - App
 * ============================================================================
 * 
 * COURS INF1013 - INJECTION DE DÉPENDANCES
 * ----------------------------------------
 * "Il s'agit d'injecter les dépendances au moment de la création (run time)
 * plutôt que lors de l'écriture du code"
 * 
 * "Il faut préférer la manière explicite de l'injection (dans le constructeur)"
 * 
 * @see Diapo: Injection de dépendances
 * ============================================================================
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Header, BottomNav],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  // Rôle affiché dans la navigation (dépend de la route actuelle)
  roleActuel: 'client' | 'restaurateur' | 'livreur' = 'client';

  constructor(
    private readonly serviceAuth: AuthService,
    private readonly rechercheService: RechercheService,
    private readonly routeur: Router
  ) {}

  ngOnInit(): void {
    // Détecte automatiquement le mode client/restaurateur selon la route
    this.routeur.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      if (event.url.startsWith('/restaurateur')) {
        this.roleActuel = 'restaurateur';
      } else if (event.url.startsWith('/livreur')) {
        this.roleActuel = 'livreur';
      } else {
        this.roleActuel = 'client';
      }
    });
  }

  /**
   * Gère le changement de recherche émis par le Header
   * 
   * COURS INF1013 - SERVICES:
   * "Les services sont des classes javascript ordinaires qui fonctionnent
   * de manière autonome"
   * 
   * On utilise le RechercheService pour propager le terme de recherche
   * à tous les composants qui en ont besoin.
   */
  onRecherche(terme: string): void {
    this.rechercheService.setTermeRecherche(terme);
  }
}
