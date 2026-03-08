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

  // Charge le restaurant et ses plats

  private chargerDonnees$(id: number) {
    this.enChargement = true;


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


  getPlatsParCategorie(categorie: string): Plat[] {
    return this.plats.filter(p => p.categorie === categorie);
  }

  // Ajoute un plat au panier

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
