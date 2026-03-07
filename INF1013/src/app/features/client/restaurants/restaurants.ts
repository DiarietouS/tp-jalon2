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

/**
 * ============================================================================
 * COMPOSANT RESTAURANTS - Liste des restaurants
 * ============================================================================
 * 
 * COURS INF1013 - EFFECT()
 * ------------------------
 * "effect() permet de surveiller le changement d'un signal pour ensuite
 * exécuter une action. C'est assimilable à un subscribe() sur un observable."
 * 
 * "effect(() => console.log(`La valeur du signal à changer pour: ${sig()}`))"
 * 
 * Nous utilisons effect() pour réagir aux changements du terme de recherche.
 * 
 * @see Diapo: effect()
 * ============================================================================
 */
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

  // Catégories de cuisine pour le filtre
  categories: string[] = ['Tous', 'Italien', 'Fast Food', 'Japonais', 'Québécois', 'Africain'];
  categorieSelectionnee = 'Tous';

  // Terme de recherche actuel
  termeRecherche = '';

  // COURS INF1013: garder la référence de souscription pour désinscription
  private subscription?: { unsubscribe: () => void };

  constructor(
    private restaurantService: RestaurantService,
    private authService: AuthService,
    private rechercheService: RechercheService,
    private formateur: FormateurService,
    private notification: NotificationService,
    private cdr: ChangeDetectorRef
  ) {
    /**
     * COURS INF1013 - EFFECT():
     * "effect() permet de surveiller le changement d'un signal pour ensuite
     * exécuter une action."
     * 
     * Ici, on surveille le signal termeRecherche du RechercheService.
     * Quand le terme change, on met à jour la propriété locale.
     */
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

  /**
   * Charge la liste des restaurants
   */
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

  /**
   * Charge les restaurants favoris de l'utilisateur
   */
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

  /**
   * Filtre les restaurants par catégorie
   */
  /**
   * Filtre les restaurants par catégorie et par terme de recherche
   * 
   * COURS INF1013 - GETTERS:
   * Utilisation d'un getter pour le filtrage dynamique des restaurants.
   * Le filtre combine la catégorie sélectionnée ET le terme de recherche.
   */
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

  /**
   * Change la catégorie sélectionnée
   */
  selectionnerCategorie(categorie: string): void {
    this.categorieSelectionnee = categorie;
  }

  /**
   * Retourne l'icône Material correspondant à la catégorie
   */
  getIconeCategorie(categorie: string): string {
    return this.formateur.getIconeCategorie(categorie);
  }

  /**
   * Retire un restaurant des favoris
   */
  retirerDesFavoris(restaurant: RestaurantModel): void {
    this.authService.retirerDesFavoris(restaurant.id);
    this.restaurantsFavoris = this.restaurantsFavoris.filter(r => r.id !== restaurant.id);
    this.notification.afficher(`${restaurant.nom} retiré des favoris`);
  }

  /**
   * Change d'onglet
   */
  onChangementOnglet(index: number): void {
    this.ongletActif = index;
    if (index === 1) {
      // Recharger les favoris quand on va sur l'onglet favoris
      this.chargerFavoris();
    }
  }
}
