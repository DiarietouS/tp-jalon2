import { Injectable, signal, computed, Signal, WritableSignal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, catchError, tap, finalize } from 'rxjs';

/**
 * ============================================================================
 * PATTERN RESOURCE - Chargement async avec signals
 * ============================================================================
 * 
 * COURS INF1013 - RESOURCE() PATTERN
 * ----------------------------------
 * "resource() permet de charger des données asynchrones tout en gérant
 * automatiquement les états de chargement, erreur et valeur."
 * 
 * "La structure d'un resource:
 * - value: Signal<T | undefined> - Les données chargées
 * - isLoading: Signal<boolean> - État de chargement
 * - error: Signal<Error | undefined> - Erreur éventuelle
 * - reload(): void - Fonction pour recharger"
 * 
 * NOTE: Cette implémentation est un pattern basé sur signals qui émule
 * le comportement de resource() pour les versions d'Angular qui ne
 * l'incluent pas nativement.
 * 
 * @see Diapo: Signals avancés, Gestion d'état async
 * ============================================================================
 */

/**
 * Interface représentant un Resource générique
 */
export interface Resource<T> {
  /** Les données chargées (undefined si pas encore chargées ou erreur) */
  value: Signal<T | undefined>;
  /** État de chargement */
  isLoading: Signal<boolean>;
  /** Erreur survenue lors du chargement */
  error: Signal<Error | undefined>;
  /** Recharge les données */
  reload: () => void;
}

/**
 * Crée un Resource à partir d'une fonction de chargement Observable
 * 
 * COURS INF1013 - FACTORY PATTERN:
 * "Une factory est une fonction qui crée et retourne un objet.
 * Ici, on crée un Resource avec gestion d'état intégrée."
 * 
 * @param loaderFn Fonction qui retourne un Observable des données
 * @returns Un Resource avec signals pour value, isLoading, error
 */
export function createResource<T>(loaderFn: () => Observable<T>): Resource<T> {
  // Signals internes pour l'état
  const _value: WritableSignal<T | undefined> = signal<T | undefined>(undefined);
  const _isLoading: WritableSignal<boolean> = signal<boolean>(false);
  const _error: WritableSignal<Error | undefined> = signal<Error | undefined>(undefined);

  /**
   * COURS INF1013 - SIGNAL IMMUTABLE:
   * "asReadonly() expose une version immutable du signal"
   */
  const value: Signal<T | undefined> = _value.asReadonly();
  const isLoading: Signal<boolean> = _isLoading.asReadonly();
  const error: Signal<Error | undefined> = _error.asReadonly();

  /**
   * Fonction de rechargement
   * 
   * COURS INF1013 - SET():
   * "set() permet de définir directement la valeur d'un signal mutable"
   */
  const reload = (): void => {
    _isLoading.set(true);
    _error.set(undefined);

    loaderFn().pipe(
      tap(data => _value.set(data)),
      catchError(err => {
        _error.set(err);
        return of(undefined);
      }),
      finalize(() => _isLoading.set(false))
    ).subscribe();
  };

  // Charger automatiquement au création
  reload();

  return { value, isLoading, error, reload };
}

/**
 * ============================================================================
 * SERVICE EXEMPLE - Utilisation du pattern Resource
 * ============================================================================
 */
@Injectable({
  providedIn: 'root'
})
export class ResourceExempleService {
  
  constructor(private http: HttpClient) {}

  /**
   * Crée un Resource pour charger les restaurants
   * 
   * COURS INF1013 - RESOURCE PATTERN:
   * "Le resource gère automatiquement les états loading/error/value"
   * 
   * Utilisation dans un composant:
   * ```
   * restaurantsResource = this.service.getRestaurantsResource();
   * 
   * // Dans le template:
   * @if (restaurantsResource.isLoading()) {
   *   <p>Chargement...</p>
   * } @else if (restaurantsResource.error()) {
   *   <p>Erreur: {{ restaurantsResource.error()?.message }}</p>
   * } @else {
   *   @for (resto of restaurantsResource.value(); track resto.id) {
   *     ...
   *   }
   * }
   * ```
   */
  getRestaurantsResource(): Resource<any[]> {
    return createResource(() => 
      this.http.get<any[]>('assets/mock/restaurant.json')
    );
  }
}
