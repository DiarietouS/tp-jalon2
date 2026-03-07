// Modèle représentant un restaurant partenaire
export interface RestaurantModel {
  id: number;
  idProprietaire: number;    // ID du propriétaire (restaurateur)
  nom: string;               // Nom du restaurant
  adresse: string;           // Adresse complète
  localisationTexte: string; // Description de la localisation
  telephone: string;
  courriel: string;
  imageUrl: string;          // Image de couverture
  typeCuisine: string;       // Type de cuisine (Italien, Japonais, etc.)
  note: number;              // Note moyenne (sur 5)
  tempsLivraison: string;    // Temps de livraison estimé (ex: "20-30 min")
  fraisLivraison: number;    // Frais de livraison en dollars
  commandeMinimum: number;   // Commande minimum requise
}
