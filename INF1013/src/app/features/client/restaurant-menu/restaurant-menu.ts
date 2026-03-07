import { Component, OnDestroy, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Subject, combineLatest, map, switchMap, takeUntil } from 'rxjs';

import { RestaurantModel } from '../../../core/models/restaurant';
import { Plat } from '../../../core/models/dish';
import { RestaurantService } from '../../../core/services/restaurant.service';
import { DishesService } from '../../../core/services/dish.service';
import { CartService } from '../../../core/services/cart.service';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { PrixPipe } from '../../../shared/pipes';

/**
 * ============================================================================
 * PAGE MENU RESTAURANT
 * ============================================================================
 * 
 * COURS INF1013 - PIPES PERSONNALISÉS:
 * "Les pipes permettent de transformer des valeurs dans les templates.
 * On peut créer des pipes personnalisés pour des besoins spécifiques."
 * 
 * Utilisation: {{ plat.prix | prix }} → "12,99 $"
 * 
 * @see Diapo: Pipes personnalisés
 * ============================================================================
 */
@Component({
  selector: 'app-restaurant-menu',
  imports: [RouterLink, MatIconModule, MatButtonModule, PrixPipe],
  templateUrl: './restaurant-menu.html',
  styleUrl: './restaurant-menu.css',
})
export class RestaurantMenu implements OnInit, OnDestroy {
  // Restaurant affiché
  restaurant: RestaurantModel | null = null;
  
  // Plats du restaurant
  plats: Plat[] = [];
  
  // Catégories de plats disponibles
  categories: string[] = [];
  
  // État de chargement
  enChargement = true;

  /**
   * COURS INF1013 - OnDestroy
   * "Cleanup... Unsubscribe Observables" pour éviter les fuites.
   */
  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly route: ActivatedRoute,
    private readonly serviceRestaurant: RestaurantService,
    private readonly servicePlats: DishesService,
    public servicePanier: CartService,
    public serviceAuth: AuthService,
    private readonly notification: NotificationService,
    private readonly cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    /**
     * COURS INF1013 - Passage de paramètres + Observable paramMap
     * "ActivatedRoute.paramMap est une Observable".
     * On observe les changements d'ID et on recharge la page.
     */
    this.route.paramMap
      .pipe(
        map((pm) => Number(pm.get('id'))),
        switchMap((id) => this.chargerDonnees$(id)),
        takeUntil(this.destroy$)
      )
      .subscribe();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Charge le restaurant et ses plats
   */
  private chargerDonnees$(id: number) {
    this.enChargement = true;

    /**
     * COURS INF1013 - Composition de flux (combineLatest + map)
     * On charge restaurants + plats en parallèle et on combine.
     */
    return combineLatest([
      this.serviceRestaurant.loadRestaurants(),
      this.servicePlats.chargerPlats(),
    ]).pipe(
      map(([restaurants, tousLesPlats]) => {
        this.restaurant = restaurants.find((r) => r.id === id) || null;
        this.plats = tousLesPlats.filter((p) => p.idRestaurant === id);
        this.categories = [...new Set(this.plats.map((p) => p.categorie))];
        this.enChargement = false;
        this.cdr.detectChanges();
      })
    );
  }

  /**
   * Retourne les plats d'une catégorie
   */
  getPlatsParCategorie(categorie: string): Plat[] {
    return this.plats.filter(p => p.categorie === categorie);
  }

  /**
   * Ajoute un plat au panier
   */
  ajouterAuPanier(plat: Plat): void {
    if (!this.restaurant) return;
    
    this.servicePanier.ajouterAuPanier(
      plat,
      this.restaurant.id,
      this.restaurant.nom,
      this.restaurant.fraisLivraison
    );
    
    this.notification.afficher(`${plat.nom} ajouté au panier`);
  }

  /**
   * Bascule le statut favori du restaurant
   */
  basculerFavori(): void {
    if (!this.restaurant) return;
    
    if (!this.serviceAuth.estConnecte()) {
      this.notification.afficher('Connectez-vous pour ajouter aux favoris');
      return;
    }

    if (this.serviceAuth.estFavori(this.restaurant.id)) {
      this.serviceAuth.retirerDesFavoris(this.restaurant.id);
      this.notification.afficher('Retiré des favoris');
    } else {
      this.serviceAuth.ajouterAuxFavoris(this.restaurant.id);
      this.notification.afficher('Ajouté aux favoris');
    }
  }
}
