import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

import { RestaurantModel } from '../models/restaurant';

@Injectable({ providedIn: 'root' })
export class RestaurantService {
  private readonly url = 'assets/mock/restaurant.json';
  private readonly storageKey = 'INF1013_restaurants';
  private restaurants: RestaurantModel[] = [];
  private restaurantsSubject = new BehaviorSubject<RestaurantModel[]>([]);
  restaurants$ = this.restaurantsSubject.asObservable();

  constructor(private readonly http: HttpClient) {}

  loadRestaurants(): Observable<RestaurantModel[]> {
    // 1) Priorité au localStorage (persistance pour la démo)
    const cache = this.lireDepuisStorage();
    if (cache.length) {
      this.restaurants = cache;
      this.restaurantsSubject.next(this.restaurants);
      return of(this.restaurants);
    }

    // 2) Sinon charger depuis le JSON et persister
    return this.http.get<any[]>(this.url).pipe(
      map(data => (data ?? []).map(r => this.convertir(r))),
      tap(restaurants => {
        this.restaurants = restaurants;
        this.restaurantsSubject.next(this.restaurants);
        this.ecrireDansStorage(this.restaurants);
      }),
      catchError(() => of([]))
    );
  }

  /**
   * Met à jour les informations d'un restaurant
   * @param idRestaurant - ID du restaurant
   * @param donnees - Données à mettre à jour
   */
  mettreAJourRestaurant(idRestaurant: number, donnees: Partial<RestaurantModel>): Observable<RestaurantModel | undefined> {
    const index = this.restaurants.findIndex(r => r.id === idRestaurant);
    if (index !== -1) {
      this.restaurants[index] = { ...this.restaurants[index], ...donnees };
      this.restaurantsSubject.next([...this.restaurants]);
      this.ecrireDansStorage(this.restaurants);
      return of(this.restaurants[index]);
    }
    return of(undefined);
  }

  /**
   * Crée un restaurant associé à un restaurateur.
   * Utilisé à l'inscription pour respecter "les restaurateurs peuvent créer un compte et exposer leurs plats".
   */
  creerRestaurantPourProprietaire(donnees: {
    idProprietaire: number;
    courriel: string;
    telephone: string;
    adresse: string;
    nom?: string;
  }): Observable<RestaurantModel> {
    const id = this.genererIdUnique();

    const restaurant: RestaurantModel = {
      id,
      idProprietaire: donnees.idProprietaire,
      nom: donnees.nom ?? 'Nouveau restaurant',
      adresse: donnees.adresse,
      localisationTexte: donnees.adresse,
      telephone: donnees.telephone,
      courriel: donnees.courriel,
      imageUrl: 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=1200&q=60',
      typeCuisine: 'Autre',
      note: 0,
      tempsLivraison: "35",
      fraisLivraison: 0,
      commandeMinimum: 0,
    };

    this.restaurants = [...this.restaurants, restaurant];
    this.restaurantsSubject.next([...this.restaurants]);
    this.ecrireDansStorage(this.restaurants);

    return of(restaurant);
  }

  private lireDepuisStorage(): RestaurantModel[] {
    try {
      const raw = localStorage.getItem(this.storageKey);
      if (!raw) return [];
      const parsed = JSON.parse(raw);
      if (!Array.isArray(parsed)) return [];
      return parsed as RestaurantModel[];
    } catch {
      return [];
    }
  }

  private ecrireDansStorage(liste: RestaurantModel[]): void {
    try {
      localStorage.setItem(this.storageKey, JSON.stringify(liste));
    } catch {
      // ignore
    }
  }

  private genererIdUnique(): number {
    return Math.max(...this.restaurants.map(r => r.id), 0) + 1;
  }

  /**
   * Convertit les données JSON (anglais) vers le modèle français
   */
  private convertir(data: any): RestaurantModel {
    return {
      id: data.id,
      idProprietaire: data.ownerId || data.idProprietaire,
      nom: data.nom,
      adresse: data.adresse,
      localisationTexte: data.localisationTexte,
      telephone: data.telephone,
      courriel: data.email || data.courriel,
      imageUrl: data.imageUrl,
      typeCuisine: data.cuisineType || data.typeCuisine,
      note: data.rating || data.note,
      tempsLivraison: data.deliveryTime || data.tempsLivraison,
      fraisLivraison: data.deliveryFee || data.fraisLivraison,
      commandeMinimum: data.minimumOrder || data.commandeMinimum
    };
  }
}
