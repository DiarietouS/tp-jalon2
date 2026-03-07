import { Injectable, signal, computed, WritableSignal, Signal } from '@angular/core';
import { PanierModel, ArticlePanier } from '../models/cart';
import { Plat } from '../models/dish';

/**
 * ============================================================================
 * SERVICE PANIER - CartService
 * ============================================================================
 * 
 * COURS INF1013 - SIGNALS (Signal Mutable)
 * ----------------------------------------
 * "Un signal est un wrapper (fonction) autour d'une valeur qui notifie
 * les observateurs concernés lorsque cette valeur change."
 * 
 * "Les signaux modifiables offrent des fonctionnalités permettant de
 * mettre à jour directement leurs valeurs avec set(T) et update(T=>{})"
 * 
 * "On peut générer un signal immutable à partir d'un signal mutable
 * avec asReadonly()"
 * 
 * COMPUTED SIGNALS:
 * "Un signal calculé est un cas particulier de signal Immutable.
 * Quand le signal source change, sa dérivée (computed) est recalculée."
 * 
 * @see Diapo: Signal Mutable, Copie Immutable d'un Signal Mutable
 * ============================================================================
 */
@Injectable({ providedIn: 'root' })
export class CartService {
  
  /**
   * Signal mutable pour l'état du panier
   * 
   * COURS INF1013: "Les signaux modifiables offrent une des fonctionnalités
   * permettant de mettre à jour directement leurs valeurs."
   * 
   * Création: const count: WritableSignal<number> = signal(0);
   * Lecture: this.count()
   * Écriture: this.count.set(3) ou this.count.update(oldValue => oldValue + 4)
   */
  private readonly _panier: WritableSignal<PanierModel | null> = signal<PanierModel | null>(null);

  /**
   * Signal immutable (lecture seule) exposé aux composants
   * 
   * COURS INF1013: "On peut générer un signal immutable à partir d'un
   * signal mutable à l'aide de asReadonly()"
   */
  readonly panier: Signal<PanierModel | null> = this._panier.asReadonly();

  /**
   * Signal calculé pour le nombre d'articles
   * 
   * COURS INF1013: "Un signal calculé est un cas particulier de signal Immutable.
   * const doubleCount: Signal<number> = computed(() => this.count() * 2);
   * Quand count change, sa dérivée doubleCount est recalculée."
   */
  readonly nombreArticles: Signal<number> = computed(() => {
    const p = this._panier();
    if (!p) return 0;
    return p.articles.reduce((somme, article) => somme + article.quantite, 0);
  });

  /**
   * Signal calculé pour le sous-total (computed signal)
   * Se met à jour automatiquement quand le panier change
   */
  readonly sousTotal: Signal<number> = computed(() => {
    const p = this._panier();
    if (!p) return 0;
    return p.articles.reduce((somme, article) => somme + (article.plat.prix * article.quantite), 0);
  });

  /**
   * Signal calculé pour le total avec frais de livraison
   * Dérive de sousTotal et du panier
   */
  readonly total: Signal<number> = computed(() => {
    const p = this._panier();
    if (!p) return 0;
    return this.sousTotal() + p.fraisLivraison;
  });

  // Alias pour compatibilité avec l'ancien code
  get cart(): PanierModel | null { return this._panier(); }
  get itemCount(): number { return this.nombreArticles(); }
  get subtotal(): number { return this.sousTotal(); }

  /**
   * Ajoute un plat au panier
   * 
   * COURS INF1013 - Modification d'un signal:
   * "Sa valeur peut être changée en utilisant set(T) ou update(T=>{})"
   * Ici on utilise update() car la nouvelle valeur dépend de l'ancienne
   */
  ajouterAuPanier(plat: Plat, idRestaurant: number, nomRestaurant: string, fraisLivraison: number): void {
    this._panier.update(panierActuel => {
      // Si le panier est vide ou d'un autre restaurant, on le réinitialise
      if (!panierActuel || panierActuel.idRestaurant !== idRestaurant) {
        return {
          idRestaurant,
          nomRestaurant,
          articles: [{ plat, quantite: 1 }],
          fraisLivraison
        };
      }

      // Cherche si le plat existe déjà
      const articleExistant = panierActuel.articles.find(article => article.plat.id === plat.id);

      if (articleExistant) {
        // Augmente la quantité - crée une nouvelle référence pour déclencher la détection
        return {
          ...panierActuel,
          articles: panierActuel.articles.map(a => 
            a.plat.id === plat.id ? { ...a, quantite: a.quantite + 1 } : a
          )
        };
      } else {
        // Ajoute un nouvel article
        return {
          ...panierActuel,
          articles: [...panierActuel.articles, { plat, quantite: 1 }]
        };
      }
    });
  }

  // Alias pour compatibilité
  addToCart(dish: Plat, restaurantId: number, restaurantName: string, deliveryFee: number): void {
    this.ajouterAuPanier(dish, restaurantId, restaurantName, deliveryFee);
  }

  /**
   * Modifie la quantité d'un article
   * 
   * COURS INF1013: "this.count.update(oldValue => oldValue + 4)"
   * Utilise update() pour transformer l'état existant
   */
  modifierQuantite(idPlat: number, quantite: number): void {
    if (quantite <= 0) {
      this.supprimerDuPanier(idPlat);
      return;
    }

    this._panier.update(panier => {
      if (!panier) return null;
      return {
        ...panier,
        articles: panier.articles.map(a =>
          a.plat.id === idPlat ? { ...a, quantite } : a
        )
      };
    });
  }

  // Alias pour compatibilité
  updateQuantity(dishId: number, quantity: number): void {
    this.modifierQuantite(dishId, quantity);
  }

  /**
   * Supprime un article du panier
   */
  supprimerDuPanier(idPlat: number): void {
    this._panier.update(panier => {
      if (!panier) return null;

      const articlesRestants = panier.articles.filter(article => article.plat.id !== idPlat);

      // Si le panier est vide, on le réinitialise à null
      if (articlesRestants.length === 0) {
        return null;
      }

      return { ...panier, articles: articlesRestants };
    });
  }

  // Alias pour compatibilité
  removeFromCart(dishId: number): void {
    this.supprimerDuPanier(dishId);
  }

  /**
   * Vide le panier
   * 
   * COURS INF1013: "this.count.set(3)" - utilise set() pour une valeur fixe
   */
  viderPanier(): void {
    this._panier.set(null);
  }

  // Alias pour compatibilité
  clearCart(): void {
    this.viderPanier();
  }
}
