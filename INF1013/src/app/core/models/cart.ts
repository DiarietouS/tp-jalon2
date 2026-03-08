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
  articles: ArticlePanier[];
  fraisLivraison: number;
}

// Alias pour compatibilité
export type CartItem = ArticlePanier;
export type Cart = PanierModel;
