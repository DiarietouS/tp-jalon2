import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { Header } from './shared/components/header/header';
import { BottomNav } from './shared/components/bottom-nav/bottom-nav';
import { AuthService } from './core/services/auth.service';
import { RechercheService } from './core/services/recherche.service';
import { filter } from 'rxjs/operators';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Header, BottomNav],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {

  roleActuel: 'client' | 'restaurateur' | 'livreur' = 'client';

  constructor(
    private readonly serviceAuth: AuthService,
    private readonly rechercheService: RechercheService,
    private readonly routeur: Router
  ) {}

  ngOnInit(): void {
    this.mettreAJourRoleActuel(this.routeur.url);

    this.routeur.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.mettreAJourRoleActuel(event.urlAfterRedirects || event.url);
    });
  }


  onRecherche(terme: string): void {
    this.rechercheService.setTermeRecherche(terme);
  }

  private mettreAJourRoleActuel(url: string): void {
    const utilisateur = this.serviceAuth.getUtilisateurCourant();

    if (url.startsWith('/restaurateur')) {
      this.roleActuel = 'restaurateur';
      return;
    }

    if (url.startsWith('/livreur')) {
      this.roleActuel = 'livreur';
      return;
    }

    if (utilisateur?.role) {
      this.roleActuel = utilisateur.role;
      return;
    }

    this.roleActuel = 'client';
  }
}
