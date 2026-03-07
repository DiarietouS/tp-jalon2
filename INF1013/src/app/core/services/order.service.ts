import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import { Commande } from '../models';

/**
 * Service de gestion des commandes
 * Permet de récupérer et créer des commandes
 */
@Injectable({
  providedIn: 'root'
})
export class OrderService {
  // Liste des commandes
  private commandes: Commande[] = [];
  private readonly storageKey = 'INF1013_orders';
  private commandesSubject = new BehaviorSubject<Commande[]>([]);
  commandes$ = this.commandesSubject.asObservable();
  // Alias pour compatibilité
  orders$ = this.commandes$;

  constructor(private http: HttpClient) {
    this.chargerCommandes();
  }

  /**
   * Charge les commandes depuis le fichier mock
   */
  private chargerCommandes(): void {
    // 1) Priorité au localStorage
    const cache = this.lireDepuisStorage();
    if (cache.length) {
      this.commandes = cache;
      this.commandesSubject.next(this.commandes);
      return;
    }

    // 2) Sinon charger depuis JSON et persister
    this.http.get<any[]>('assets/mock/orders.json').pipe(
      catchError(() => of([]))
    ).subscribe(orders => {
      this.commandes = (orders ?? []).map(o => this.convertirCommande(o));
      this.commandesSubject.next(this.commandes);
      this.ecrireDansStorage(this.commandes);
    });
  }

  /**
   * Convertit une commande du format anglais vers français
   */
  private convertirCommande(o: any): Commande {
    return {
      id: o.id,
      idClient: o.clientId,
      idRestaurant: o.restaurantId,
      nomRestaurant: o.restaurantName,
      articles: (o.items || []).map((i: any) => ({
        idPlat: i.dishId,
        nomPlat: i.dishName,
        quantite: i.quantity,
        prixUnitaire: i.unitPrice
      })),
      statut: this.convertirStatut(o.status),
      sousTotal: o.subtotal,
      fraisLivraison: o.deliveryFee,
      total: o.total,
      adresseLivraison: o.deliveryAddress,
      telephone: o.phone,
      creeLe: o.createdAt,
      livraisonEstimee: o.estimatedDelivery
    };
  }

  /**
   * Convertit les statuts anglais vers français
   */
  private convertirStatut(statut: string): any {
    const mapping: { [key: string]: string } = {
      'pending': 'enAttente',
      'confirmed': 'confirmee',
      'preparing': 'enPreparation',
      'ready': 'prete',
      'delivering': 'enLivraison',
      'delivered': 'livree',
      'cancelled': 'annulee'
    };
    return mapping[statut] || statut;
  }

  /**
   * Récupère toutes les commandes
   */
  getCommandes(): Observable<Commande[]> {
    if (this.commandes.length === 0) {
      const cache = this.lireDepuisStorage();
      if (cache.length) {
        this.commandes = cache;
        this.commandesSubject.next(this.commandes);
        return of(this.commandes);
      }

      return this.http.get<any[]>('assets/mock/orders.json').pipe(
        tap(orders => {
          this.commandes = orders.map(o => this.convertirCommande(o));
          this.commandesSubject.next(this.commandes);
          this.ecrireDansStorage(this.commandes);
        }),
        map(() => this.commandes),
        catchError(() => of([]))
      );
    }
    return of(this.commandes);
  }

  // Alias pour compatibilité
  getOrders(): Observable<Commande[]> {
    return this.getCommandes();
  }

  /**
   * Récupère les commandes d'un utilisateur
   * @param idClient - ID du client
   */
  getCommandesParUtilisateur(idClient: number): Observable<Commande[]> {
    return this.getCommandes().pipe(
      map(commandes => commandes.filter(c => c.idClient === idClient))
    );
  }

  // Alias pour compatibilité
  getOrdersByUser(clientId: number): Observable<Commande[]> {
    return this.getCommandesParUtilisateur(clientId);
  }

  /**
   * Récupère les commandes d'un restaurant
   * @param idRestaurant - ID du restaurant
   */
  getCommandesParRestaurant(idRestaurant: number): Observable<Commande[]> {
    return this.getCommandes().pipe(
      map(commandes => commandes.filter(c => c.idRestaurant === idRestaurant))
    );
  }

  // Alias pour compatibilité
  getOrdersByRestaurant(restaurantId: number): Observable<Commande[]> {
    return this.getCommandesParRestaurant(restaurantId);
  }

  /**
   * Récupère une commande par son ID
   * @param idCommande - ID de la commande
   */
  getCommandeParId(idCommande: number): Observable<Commande | undefined> {
    return this.getCommandes().pipe(
      map(commandes => commandes.find(c => c.id === idCommande))
    );
  }

  // Alias pour compatibilité
  getOrderById(orderId: number): Observable<Commande | undefined> {
    return this.getCommandeParId(orderId);
  }

  /**
   * Crée une nouvelle commande
   * @param commande - Données de la commande
   */
  creerCommande(commande: Partial<Commande>): Observable<Commande> {
    const nouvelleCommande: Commande = {
      id: this.genererIdUnique(),
      idClient: commande.idClient || 0,
      idRestaurant: commande.idRestaurant || 0,
      nomRestaurant: commande.nomRestaurant || '',
      articles: commande.articles || [],
      sousTotal: commande.sousTotal || 0,
      fraisLivraison: commande.fraisLivraison || 0,
      total: commande.total || 0,
      statut: 'enAttente',
      adresseLivraison: commande.adresseLivraison || '',
      telephone: commande.telephone || '',
      creeLe: new Date().toISOString(),
      livraisonEstimee: this.calculerLivraisonEstimee()
    };

    this.commandes.push(nouvelleCommande);
    this.commandesSubject.next([...this.commandes]);
    this.ecrireDansStorage(this.commandes);
    
    return of(nouvelleCommande);
  }

  // Alias pour compatibilité
  createOrder(order: any): Observable<Commande> {
    return this.creerCommande({
      idClient: order.clientId,
      idRestaurant: order.restaurantId,
      nomRestaurant: order.restaurantName,
      articles: (order.items || []).map((i: any) => ({
        idPlat: i.dishId,
        nomPlat: i.dishName,
        quantite: i.quantity,
        prixUnitaire: i.unitPrice
      })),
      sousTotal: order.subtotal,
      fraisLivraison: order.deliveryFee,
      total: order.total,
      adresseLivraison: order.deliveryAddress,
      telephone: order.phone
    });
  }

  /**
   * Met à jour le statut d'une commande
   * @param idCommande - ID de la commande
   * @param statut - Nouveau statut
   */
  mettreAJourStatut(idCommande: number, statut: Commande['statut']): Observable<Commande | undefined> {
    const commande = this.commandes.find(c => c.id === idCommande);
    if (commande) {
      commande.statut = statut;
      this.commandesSubject.next([...this.commandes]);
      this.ecrireDansStorage(this.commandes);
    }
    return of(commande);
  }

  private lireDepuisStorage(): Commande[] {
    try {
      const raw = localStorage.getItem(this.storageKey);
      if (!raw) return [];
      const parsed = JSON.parse(raw);
      if (!Array.isArray(parsed)) return [];
      return parsed as Commande[];
    } catch {
      return [];
    }
  }

  private ecrireDansStorage(liste: Commande[]): void {
    try {
      localStorage.setItem(this.storageKey, JSON.stringify(liste));
    } catch {
      // ignore
    }
  }

  // Alias pour compatibilité
  updateOrderStatus(orderId: number, status: any): Observable<Commande | undefined> {
    return this.mettreAJourStatut(orderId, this.convertirStatut(status) as any);
  }

  /**
   * Génère un ID unique
   */
  private genererIdUnique(): number {
    return Math.max(...this.commandes.map(c => c.id), 0) + 1;
  }

  /**
   * Calcule l'heure de livraison estimée (30-45 min)
   */
  private calculerLivraisonEstimee(): string {
    const maintenant = new Date();
    maintenant.setMinutes(maintenant.getMinutes() + 35);
    return maintenant.toISOString();
  }
}
