import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, of, map, tap } from 'rxjs';
import { Plat } from '../models/dish';

@Injectable({ providedIn: 'root' })
export class DishesService {

  private readonly url = 'assets/mock/dishes.json';
  private readonly storageKey = 'INF1013_dishes';

  private plats: Plat[] = []; // stockage en mémoire seulement
  private charge = false;

  constructor(private readonly http: HttpClient) {}

  chargerPlats(): Observable<Plat[]> {

    // si déjà chargé en mémoire
    if (this.charge) {
      return of(this.plats);
    }

    // priorité au localStorage (persistance pour la démo)
    const cache = this.lireDepuisStorage();
    if (cache.length) {
      this.plats = cache;
      this.charge = true;
      return of(this.plats);
    }

    // sinon on charge depuis le fichier JSON
    return this.http.get<any[]>(this.url).pipe(
      map((data) => (data ?? []).map(d => this.convertirPlat(d))),
      tap((data) => {
        this.plats = data;
        this.charge = true;
        this.ecrireDansStorage(this.plats);
      }),
      catchError(() => {
        this.plats = [];
        this.charge = true;
        return of([]);
      })
    );
  }

  // Alias pour compatibilité
  loadDishes(): Observable<Plat[]> {
    return this.chargerPlats();
  }

  /**
   * Convertit un plat du format anglais vers français
   */
  private convertirPlat(d: any): Plat {
    return {
      id: d.id,
      nom: d.name,
      description: d.description,
      prix: d.price,
      categorie: d.category,
      imageUrl: d.imageUrl,
      disponible: d.available,
      idRestaurant: d.restaurantId
    };
  }

  getPlats(): Plat[] {
    return this.plats;
  }

  // Alias pour compatibilité
  getDishes(): Plat[] {
    return this.getPlats();
  }

  ajouterPlat(nouveauPlat: Plat): Observable<Plat[]> {
    return this.chargerPlats().pipe(
      map((liste) => {
        this.plats = [...liste, nouveauPlat];
        this.ecrireDansStorage(this.plats);
        return this.plats;
      })
    );
  }

  // Alias pour compatibilité
  addDish(newDish: Plat): Observable<Plat[]> {
    return this.ajouterPlat(newDish);
  }

  supprimerPlat(id: number): Observable<Plat[]> {
    return this.chargerPlats().pipe(
      map((liste) => {
        this.plats = liste.filter((p) => p.id !== id);
        this.ecrireDansStorage(this.plats);
        return this.plats;
      })
    );
  }

  // Alias pour compatibilité
  deleteDish(id: number): Observable<Plat[]> {
    return this.supprimerPlat(id);
  }

  mettreAJourPlat(platModifie: Plat): Observable<Plat[]> {
    return this.chargerPlats().pipe(
      map((liste) => {
        this.plats = liste.map((p) => (p.id === platModifie.id ? platModifie : p));
        this.ecrireDansStorage(this.plats);
        return this.plats;
      })
    );
  }

  // Alias pour compatibilité
  updateDish(updated: Plat): Observable<Plat[]> {
    return this.mettreAJourPlat(updated);
  }

  reinitialiserDepuisJson(): Observable<Plat[]> {
    this.charge = false;
    this.plats = [];
    try { localStorage.removeItem(this.storageKey); } catch {}
    return this.chargerPlats();
  }

  private lireDepuisStorage(): Plat[] {
    try {
      const raw = localStorage.getItem(this.storageKey);
      if (!raw) return [];
      const parsed = JSON.parse(raw);
      if (!Array.isArray(parsed)) return [];
      return parsed as Plat[];
    } catch {
      return [];
    }
  }

  private ecrireDansStorage(liste: Plat[]): void {
    try {
      localStorage.setItem(this.storageKey, JSON.stringify(liste));
    } catch {
      // ignore
    }
  }

  // Alias pour compatibilité
  resetToJson(): Observable<Plat[]> {
    return this.reinitialiserDepuisJson();
  }
}
