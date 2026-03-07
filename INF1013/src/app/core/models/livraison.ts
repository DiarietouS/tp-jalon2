export type StatutLivraison = 'EN_ATTENTE' | 'EN_COURS' | 'LIVREE';

export interface LigneLivraison {
  numPlat: number;
  libelle: string;
  quantite: number;
  prixUnitaire: number;
}

export interface LivraisonModel {
  id: number;
  client: string;
  adresse: string;
  telephone: string;
  statut: StatutLivraison;
  creeLe: string;
  lignes: LigneLivraison[];
}

// Alias pour compatibilité
export type LivraisonStatus = StatutLivraison;
export type LivraisonLine = LigneLivraison;
