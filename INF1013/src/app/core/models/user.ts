// Rôle de l'utilisateur dans l'application
export type RoleUtilisateur = 'client' | 'restaurateur' | 'livreur';

// Modèle représentant un utilisateur (client ou restaurateur)
export interface Utilisateur {
  id: number;
  courriel: string;
  motDePasse: string;        // Note: jamais stocké côté client en production - juste pour le mock
  prenom: string;
  nom: string;
  telephone: string;
  adresse: string;
  role: RoleUtilisateur;
  favoris: number[];         // Liste des IDs de restaurants favoris
}

// Alias pour compatibilité
export type UserRole = RoleUtilisateur;
export type User = Utilisateur;
