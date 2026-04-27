// Statut d'une commande dans le système
export type StatutCommande = 
  | 'enAttente'      // En attente de confirmation
  | 'confirmee'      // Confirmée par le restaurant
  | 'enPreparation'  // En préparation
  | 'prete'          // Prête pour livraison
  | 'enLivraison'    // En cours de livraison
  | 'livree'         // Livrée au client
  | 'annulee';       // Annulée

// Un plat commandé avec quantité et prix
export interface ArticleCommande {
  idPlat: number;
  nomPlat: string;
  quantite: number;
  prixUnitaire: number;      // Prix unitaire au moment de la commande
}

// Commande complète d'un client
export interface Commande {
  id: number;
  idClient: number;          // ID du client qui a passé la commande
  idRestaurant: number;      // ID du restaurant
  nomRestaurant: string;
  articles: ArticleCommande[];  // Liste des plats commandés
  statut: StatutCommande;    // Statut actuel de la commande
  sousTotal: number;         // Sous-total avant frais
  fraisLivraison: number;    // Frais de livraison
  total: number;             // Total à payer
  adresseLivraison: string;  // Adresse de livraison
  telephone: string;         // Téléphone du client
  creeLe: string;            // Date/heure de création
  livraisonEstimee: string;  // Date/heure estimée de livraison
}

// Alias pour compatibilité
export type OrderStatus = StatutCommande;
export type OrderItem = ArticleCommande;
export type Order = Commande;
