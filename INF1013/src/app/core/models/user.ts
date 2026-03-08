// Rôle de l'utilisateur dans l'application
export type RoleUtilisateur = 'client' | 'restaurateur' | 'livreur';

// Modèle représentant un utilisateur (client ou restaurateur)
export interface Utilisateur {
  id: number;
  courriel: string;
  motDePasse: string;
  prenom: string;
  nom: string;
  telephone: string;
  adresse: string;
  role: RoleUtilisateur;
  favoris: number[];
}

// Alias pour compatibilité
export type UserRole = RoleUtilisateur;
export type User = Utilisateur;
