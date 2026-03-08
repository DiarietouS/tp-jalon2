// un plat au menu d'un restaurant
export interface Plat {
  id: number;
  nom: string;               // Nom du plat
  description: string;       // Description détaillée
  prix: number;              // Prix en dollars
  categorie: string;         // Catégorie (Pizza, Burger, etc.)
  imageUrl: string;          // URL de l'image du plat
  disponible: boolean;       // Disponibilité actuelle
  idRestaurant: number;      // ID du restaurant propriétaire
}


export type Dish = Plat;
