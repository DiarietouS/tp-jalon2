import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, map, catchError, tap } from 'rxjs';

import { LivraisonModel, StatutLivraison } from '../models/livraison';

@Injectable({
  providedIn: 'root',
})
export class LivraisonService {

  private readonly jsonUrl = 'assets/mock/livraisons.json';

  private livraisons: LivraisonModel[] = [];
  private charge = false;

  constructor(private readonly http: HttpClient) {}

  chargerLivraisons(): Observable<LivraisonModel[]> {

    // si déjà chargé en mémoire
    if (this.charge) {
      return of(this.livraisons);
    }

    // sinon charger depuis JSON
    return this.http.get<any[]>(this.jsonUrl).pipe(
      map((data) => (data ?? []).map(l => this.convertirLivraison(l))),
      tap((data) => {
        this.livraisons = data;
        this.charge = true;
      }),
      catchError(() => {
        this.livraisons = [];
        this.charge = true;
        return of([]);
      })
    );
  }

  // Alias pour compatibilité
  loadLivraisons(): Observable<LivraisonModel[]> {
    return this.chargerLivraisons();
  }

  /**
   * Convertit une livraison du format anglais vers français
   */
  private convertirLivraison(l: any): LivraisonModel {
    return {
      id: l.id,
      client: l.customer,
      adresse: l.address,
      telephone: l.phone,
      statut: this.convertirStatut(l.status),
      creeLe: l.createdAt,
      lignes: (l.lines || []).map((ligne: any) => ({
        numPlat: ligne.dishnum,
        libelle: ligne.label,
        quantite: ligne.quantity,
        prixUnitaire: ligne.unitPrice
      }))
    };
  }

  /**
   * Convertit les statuts anglais vers français
   */
  private convertirStatut(statut: string): StatutLivraison {
    const mapping: { [key: string]: StatutLivraison } = {
      'PENDING': 'EN_ATTENTE',
      'IN_PROGRESS': 'EN_COURS',
      'DELIVERED': 'LIVREE'
    };
    return mapping[statut] || statut as StatutLivraison;
  }

  mettreAJourStatut(id: number, statut: StatutLivraison): Observable<LivraisonModel[]> {
    return this.chargerLivraisons().pipe(
      map((liste) => {
        this.livraisons = liste.map(l =>
          l.id === id ? { ...l, statut } : l
        );

        return this.livraisons;
      })
    );
  }

  // Alias pour compatibilité
  updateStatus(id: number, status: any): Observable<LivraisonModel[]> {
    return this.mettreAJourStatut(id, this.convertirStatut(status));
  }

  supprimerLivraison(id: number): Observable<LivraisonModel[]> {
    return this.chargerLivraisons().pipe(
      map((liste) => {
        this.livraisons = liste.filter(l => l.id !== id);
        return this.livraisons;
      })
    );
  }

  // Alias pour compatibilité
  deleteLivraison(id: number): Observable<LivraisonModel[]> {
    return this.supprimerLivraison(id);
  }
}
