import { Injectable, signal, Signal } from '@angular/core';

/**
 * ============================================================================
 * SERVICE DE RECHERCHE - RechercheService
 * ============================================================================
 * 
 * COURS INF1013 - SIGNALS
 * -----------------------
 * "Un signal est un wrapper (fonction) autour d'une valeur qui notifie
 * les observateurs concernés lorsque cette valeur change."
 * 
 * "Les signaux peuvent contenir n'importe quelle valeur, des types
 * primitifs aux structures de données complexes."
 * 
 * Ce service permet de partager le terme de recherche entre le Header
 * et les composants qui doivent filtrer leurs données.
 * 
 * INJECTION DE DÉPENDANCES:
 * "En précisant providedIn: 'root', on permet à Angular de faire
 * l'injection dès le bootstrap de l'application"
 * 
 * @see Diapo: Signal Mutable, Injection de dépendances
 * ============================================================================
 */
@Injectable({
  providedIn: 'root'
})
export class RechercheService {
  
  /**
   * Signal mutable privé pour le terme de recherche
   * 
   * COURS INF1013: "Les signaux modifiables offrent des fonctionnalités
   * permettant de mettre à jour directement leurs valeurs avec set(T)"
   */
  private readonly _termeRecherche = signal<string>('');

  /**
   * Signal en lecture seule exposé aux composants
   * 
   * COURS INF1013: "On peut générer un signal immutable à partir d'un
   * signal mutable à l'aide de asReadonly()"
   */
  readonly termeRecherche: Signal<string> = this._termeRecherche.asReadonly();

  /**
   * Met à jour le terme de recherche
   * 
   * COURS INF1013: "Modifier la valeur d'un signal:
   * this.count.set(3)" - utilise set() pour définir la valeur
   */
  setTermeRecherche(terme: string): void {
    this._termeRecherche.set(terme);
  }

  /**
   * Réinitialise la recherche
   */
  reinitialiser(): void {
    this._termeRecherche.set('');
  }
}
