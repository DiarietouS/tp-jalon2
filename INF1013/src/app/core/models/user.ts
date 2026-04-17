// Rôle de l'utilisateur dans l'application
// Accepte les valeurs en minuscules (existant) et en majuscules (API Spring Boot)
export type RoleUtilisateur = 'client' | 'restaurateur' | 'livreur' | 'CLIENT' | 'RESTAURATEUR' | 'LIVREUR' | 'ADMIN';

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
