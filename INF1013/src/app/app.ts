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


  onRecherche(terme: string): void {
    this.rechercheService.setTermeRecherche(terme);
  }
}
