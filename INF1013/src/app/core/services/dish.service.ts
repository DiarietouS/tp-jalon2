import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, of, map, switchMap, tap } from 'rxjs';
import { Plat } from '../models/dish';

@Injectable({ providedIn: 'root' })
export class DishesService {
  /** URL du business-service */
  private readonly BUSINESS_URL = 'http://localhost:8082/api/plats';
  /** Fallback mock local */
  private readonly mockUrl = 'assets/mock/dishes.json';
  private readonly storageKey = 'INF1013_dishes';
  private readonly storageVersionKey = 'INF1013_dishes_version';
  private readonly storageVersion = '2026-04-27-v1';

  private plats: Plat[] = [];
  private charge = false;

  constructor(private readonly http: HttpClient) {
    this.assurerVersionCache();
  }

  /**
   * Charge tous les plats depuis le business-service.
   * Repli sur localStorage, puis mock JSON si indisponible.
   */
  chargerPlats(): Observable<Plat[]> {
    if (this.charge) return of(this.plats);

    return this.http.get<Plat[]>(this.BUSINESS_URL).pipe(
      switchMap(plats => {
        if (plats && plats.length > 0) {
          this.plats = plats;
          this.charge = true;
          this.ecrireDansStorage(this.plats);
          return of(this.plats);
        }
        return this.chargerDepuisCache();
      }),
      catchError(() => this.chargerDepuisCache())
    );
  }

  private chargerDepuisCache(): Observable<Plat[]> {
    const cache = this.lireDepuisStorage();
    if (cache.length > 0) {
      this.plats = cache;
      this.charge = true;
      return of(this.plats);
    }

    return this.http.get<any[]>(this.mockUrl).pipe(
      map(data => (data ?? []).map(d => this.convertirPlat(d))),
      tap(data => {
        this.plats = data;
        this.charge = true;
        this.ecrireDansStorage(this.plats);
      }),
      catchError(() => { this.plats = []; this.charge = true; return of([]); })
    );
  }

  loadDishes(): Observable<Plat[]> { return this.chargerPlats(); }

  getPlats(): Plat[] { return this.plats; }
  getDishes(): Plat[] { return this.plats; }

  /**
   * Ajoute un plat via POST /api/plats.
   * Repli local si indisponible.
   */
  ajouterPlat(nouveauPlat: Plat): Observable<Plat[]> {
    return this.http.post<Plat>(this.BUSINESS_URL, nouveauPlat).pipe(
      tap(created => {
        this.plats = [...this.plats, created];
        this.ecrireDansStorage(this.plats);
      }),
      map(() => this.plats),
      catchError(() => {
        this.plats = [...this.plats, nouveauPlat];
        this.ecrireDansStorage(this.plats);
        return of(this.plats);
      })
    );
  }

  addDish(newDish: Plat): Observable<Plat[]> { return this.ajouterPlat(newDish); }

  /**
   * Supprime un plat via DELETE /api/plats/{id}.
   * Repli local si indisponible.
   */
  supprimerPlat(id: number): Observable<Plat[]> {
    return this.http.delete<void>(`${this.BUSINESS_URL}/${id}`).pipe(
      tap(() => {
        this.plats = this.plats.filter(p => p.id !== id);
        this.ecrireDansStorage(this.plats);
      }),
      map(() => this.plats),
      catchError(() => {
        this.plats = this.plats.filter(p => p.id !== id);
        this.ecrireDansStorage(this.plats);
        return of(this.plats);
      })
    );
  }

  deleteDish(id: number): Observable<Plat[]> { return this.supprimerPlat(id); }

  /**
   * Met Ã  jour un plat via PUT /api/plats/{id}.
   * Repli local si indisponible.
   */
  mettreAJourPlat(platModifie: Plat): Observable<Plat[]> {
    return this.http.put<Plat>(`${this.BUSINESS_URL}/${platModifie.id}`, platModifie).pipe(
      tap(updated => {
        this.plats = this.plats.map(p => p.id === updated.id ? updated : p);
        this.ecrireDansStorage(this.plats);
      }),
      map(() => this.plats),
      catchError(() => {
        this.plats = this.plats.map(p => p.id === platModifie.id ? platModifie : p);
        this.ecrireDansStorage(this.plats);
        return of(this.plats);
      })
    );
  }

  updateDish(updated: Plat): Observable<Plat[]> { return this.mettreAJourPlat(updated); }

  reinitialiserDepuisJson(): Observable<Plat[]> {
    this.charge = false;
    this.plats = [];
    try { localStorage.removeItem(this.storageKey); } catch {}
    return this.chargerPlats();
  }

  resetToJson(): Observable<Plat[]> { return this.reinitialiserDepuisJson(); }

  private convertirPlat(d: any): Plat {
    return {
      id: d.id,
      nom: d.name || d.nom,
      description: d.description,
      prix: d.price ?? d.prix,
      categorie: d.category || d.categorie,
      imageUrl: d.imageUrl,
      disponible: d.available ?? d.disponible,
      idRestaurant: d.restaurantId || d.idRestaurant
    };
  }

  private lireDepuisStorage(): Plat[] {
    try {
      const raw = localStorage.getItem(this.storageKey);
      if (!raw) return [];
      const parsed = JSON.parse(raw);
      return Array.isArray(parsed) ? parsed as Plat[] : [];
    } catch { return []; }
  }

  private ecrireDansStorage(liste: Plat[]): void {
    try { localStorage.setItem(this.storageKey, JSON.stringify(liste)); } catch {}
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
}
