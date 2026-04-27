import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';

import { RestaurantModel } from '../models/restaurant';

@Injectable({ providedIn: 'root' })
export class RestaurantService {
  /** URL du business-service */
  private readonly BUSINESS_URL = 'http://localhost:8082/api/restaurants';
  /** Fallback mock local */
  private readonly mockUrl = 'assets/mock/restaurant.json';
  private readonly storageKey = 'INF1013_restaurants';
  private readonly storageVersionKey = 'INF1013_restaurants_version';
  private readonly storageVersion = '2026-04-27-v1';

  private restaurants: RestaurantModel[] = [];
  private restaurantsSubject = new BehaviorSubject<RestaurantModel[]>([]);
  restaurants$ = this.restaurantsSubject.asObservable();

  constructor(private readonly http: HttpClient) {
    this.assurerVersionCache();
  }

  loadRestaurants(): Observable<RestaurantModel[]> {
    if (this.restaurants.length > 0) {
      this.restaurantsSubject.next(this.restaurants);
      return of(this.restaurants);
    }

    return this.http.get<RestaurantModel[]>(this.BUSINESS_URL).pipe(
      switchMap(restaurants => {
        if (restaurants && restaurants.length > 0) {
          this.restaurants = restaurants;
          this.restaurantsSubject.next(this.restaurants);
          this.ecrireDansStorage(this.restaurants);
          return of(this.restaurants);
        }
        return this.chargerDepuisCache();
      }),
      catchError(() => this.chargerDepuisCache())
    );
  }

  private chargerDepuisCache(): Observable<RestaurantModel[]> {
    const cache = this.lireDepuisStorage();
    if (cache.length > 0) {
      this.restaurants = cache;
      this.restaurantsSubject.next(this.restaurants);
      return of(this.restaurants);
    }

    return this.http.get<any[]>(this.mockUrl).pipe(
      map(data => (data ?? []).map(r => this.convertir(r))),
      tap(data => {
        this.restaurants = data;
        this.restaurantsSubject.next(this.restaurants);
        this.ecrireDansStorage(this.restaurants);
      }),
      catchError(() => of([]))
    );
  }

  /**
   * Met à jour un restaurant via PUT /api/restaurants/{id}.
   * Repli sur mise à jour locale si le backend est indisponible.
   */
  mettreAJourRestaurant(idRestaurant: number, donnees: Partial<RestaurantModel>): Observable<RestaurantModel | undefined> {
    const existant = this.restaurants.find(r => r.id === idRestaurant);
    if (!existant) return of(undefined);

    const payload: RestaurantModel = { ...existant, ...donnees };

    return this.http.put<RestaurantModel>(`${this.BUSINESS_URL}/${idRestaurant}`, payload).pipe(
      tap(updated => this.mettreAJourEnMemoire(updated)),
      catchError(() => {
        this.mettreAJourEnMemoire(payload);
        return of(payload);
      })
    );
  }

  /**
   * Crée un restaurant via POST /api/restaurants.
   * Repli sur création locale avec ID généré si le backend est indisponible.
   */
  creerRestaurantPourProprietaire(donnees: {
    idProprietaire: number;
    courriel: string;
    telephone: string;
    adresse: string;
    nom?: string;
  }): Observable<RestaurantModel> {
    const payload: Partial<RestaurantModel> = {
      idProprietaire: donnees.idProprietaire,
      nom: donnees.nom ?? 'Nouveau restaurant',
      adresse: donnees.adresse,
      localisationTexte: donnees.adresse,
      telephone: donnees.telephone,
      courriel: donnees.courriel,
      imageUrl: 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=1200&q=60',
      typeCuisine: 'Autre',
      note: 0,
      tempsLivraison: '35',
      fraisLivraison: 0,
      commandeMinimum: 0,
    };

    return this.http.post<RestaurantModel>(this.BUSINESS_URL, payload).pipe(
      tap(created => {
        this.restaurants = [...this.restaurants, created];
        this.restaurantsSubject.next([...this.restaurants]);
        this.ecrireDansStorage(this.restaurants);
      }),
      catchError(() => {
        const restaurant: RestaurantModel = { ...payload as RestaurantModel, id: this.genererIdUnique() };
        this.restaurants = [...this.restaurants, restaurant];
        this.restaurantsSubject.next([...this.restaurants]);
        this.ecrireDansStorage(this.restaurants);
        return of(restaurant);
      })
    );
  }

  /**
   * Crée un restaurant complet via POST /api/restaurants.
   * Repli sur création locale si le backend est indisponible.
   */
  creerRestaurant(payload: Partial<RestaurantModel>): Observable<RestaurantModel> {
    return this.http.post<RestaurantModel>(this.BUSINESS_URL, payload).pipe(
      tap(created => {
        this.restaurants = [...this.restaurants, created];
        this.restaurantsSubject.next([...this.restaurants]);
        this.ecrireDansStorage(this.restaurants);
      }),
      catchError(() => {
        const restaurant: RestaurantModel = { ...payload as RestaurantModel, id: this.genererIdUnique() };
        this.restaurants = [...this.restaurants, restaurant];
        this.restaurantsSubject.next([...this.restaurants]);
        this.ecrireDansStorage(this.restaurants);
        return of(restaurant);
      })
    );
  }

  private mettreAJourEnMemoire(updated: RestaurantModel): void {
    const index = this.restaurants.findIndex(r => r.id === updated.id);
    if (index !== -1) {
      this.restaurants[index] = updated;
      this.restaurantsSubject.next([...this.restaurants]);
      this.ecrireDansStorage(this.restaurants);
    }
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
    } catch {}
  }

  private genererIdUnique(): number {
    return Math.max(...this.restaurants.map(r => r.id), 0) + 1;
  }

  private assurerVersionCache(): void {
    try {
      const version = localStorage.getItem(this.storageVersionKey);
      if (version !== this.storageVersion) {
        localStorage.removeItem(this.storageKey);
        localStorage.setItem(this.storageVersionKey, this.storageVersion);
      }
    } catch {
      // ignore
    }
  }

  /** Convertit les champs du mock JSON (anglais) vers RestaurantModel */
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

