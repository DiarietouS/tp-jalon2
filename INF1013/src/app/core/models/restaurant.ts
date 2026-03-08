// Modèle représentant un restaurant partenaire
export interface RestaurantModel {
  id: number;
  idProprietaire: number;
  nom: string;
  adresse: string;
  localisationTexte: string;
  telephone: string;
  courriel: string;
  imageUrl: string;
  typeCuisine: string;
  note: number;
  tempsLivraison: string;
  fraisLivraison: number;
  commandeMinimum: number;
}
