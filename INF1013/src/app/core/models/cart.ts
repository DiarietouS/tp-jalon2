import { Plat } from './dish';

// Un article dans le panier avec sa quantité
export interface ArticlePanier {
  plat: Plat;
  quantite: number;
}

// Panier d'achat lié à un restaurant
export interface PanierModel {
  idRestaurant: number;
  nomRestaurant: string;
  articles: ArticlePanier[];    // Liste des plats commandés
  fraisLivraison: number;       // Frais de livraison
}

// Alias pour compatibilité
export type CartItem = ArticlePanier;
export type Cart = PanierModel;