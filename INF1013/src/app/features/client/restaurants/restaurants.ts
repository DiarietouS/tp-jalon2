import { Component, OnDestroy, OnInit, ChangeDetectorRef, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatChipsModule } from '@angular/material/chips';

import { RestaurantModel } from '../../../core/models/restaurant';
import { RestaurantService } from '../../../core/services/restaurant.service';
import { AuthService } from '../../../core/services/auth.service';
import { RechercheService } from '../../../core/services/recherche.service';
import { FormateurService } from '../../../core/services/formateur.service';
import { NotificationService } from '../../../core/services/notification.service';


@Component({
  selector: 'app-restaurants',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTabsModule,
    MatChipsModule
  ],
  templateUrl: './restaurants.html',
  styleUrl: './restaurants.css',
})
export class Restaurants implements OnInit, OnDestroy {
  // Index de l'onglet actif (0 = tous, 1 = favoris)
  ongletActif = 0;

  // Liste des restaurants
  restaurants: RestaurantModel[] = [];
  restaurantsFavoris: RestaurantModel[] = [];

  // État de chargement
  enChargement = true;
  estConnecte = false;

  // Catégories de cuisine
  categories: string[] = ['Tous', 'Italien', 'Fast Food', 'Japonais', 'Québécois', 'Africain'];
  categorieSelectionnee = 'Tous';

  // Terme de recherche actuel
  termeRecherche = '';


  private subscription?: { unsubscribe: () => void };

  constructor(
    private restaurantService: RestaurantService,
    private authService: AuthService,
    private rechercheService: RechercheService,
    private formateur: FormateurService,
    private notification: NotificationService,
    private cdr: ChangeDetectorRef
  ) {

    effect(() => {
      this.termeRecherche = this.rechercheService.termeRecherche();
      this.cdr.detectChanges();
    });
  }

  ngOnInit(): void {
    this.estConnecte = this.authService.estConnecte();
    this.chargerRestaurants();
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  // Charge la liste des restaurants

  private chargerRestaurants(): void {
    this.enChargement = true;
    this.subscription = this.restaurantService.loadRestaurants().subscribe({
      next: (data) => {
        this.restaurants = data;
        this.chargerFavoris();
        this.enChargement = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.enChargement = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Charge les restaurants favoris de l'utilisateur

  private chargerFavoris(): void {
    const utilisateur = this.authService.getUtilisateurCourant();

    if (utilisateur) {
      this.estConnecte = true;
      const idsFavoris = utilisateur.favoris || [];
      this.restaurantsFavoris = this.restaurants.filter(r => idsFavoris.includes(r.id));
    } else {
      this.estConnecte = false;
      this.restaurantsFavoris = [];
    }
  }


  get restaurantsFiltres(): RestaurantModel[] {
    let resultat = this.restaurants;

    // Filtrer par catégorie
    if (this.categorieSelectionnee !== 'Tous') {
      resultat = resultat.filter(r => r.typeCuisine === this.categorieSelectionnee);
    }

    // Filtrer par terme de recherche
    if (this.termeRecherche.trim()) {
      const terme = this.termeRecherche.toLowerCase().trim();
      resultat = resultat.filter(r =>
        r.nom.toLowerCase().includes(terme) ||
        r.typeCuisine.toLowerCase().includes(terme) ||
        r.adresse.toLowerCase().includes(terme)
      );
    }

    return resultat;
  }

  // Change la catégorie sélectionnée

  selectionnerCategorie(categorie: string): void {
    this.categorieSelectionnee = categorie;
  }

  getIconeCategorie(categorie: string): string {
    return this.formateur.getIconeCategorie(categorie);
  }

  // Retirer un restaurant des favoris

  retirerDesFavoris(restaurant: RestaurantModel): void {
    this.authService.retirerDesFavoris(restaurant.id);
    this.restaurantsFavoris = this.restaurantsFavoris.filter(r => r.id !== restaurant.id);
    this.notification.afficher(`${restaurant.nom} retiré des favoris`);
  }


  onChangementOnglet(index: number): void {
    this.ongletActif = index;
    if (index === 1) {
      // Recharger les favoris quand on va sur l'onglet favoris
      this.chargerFavoris();
    }
  }
}
