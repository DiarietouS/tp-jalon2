import { Injectable } from '@angular/core';
import { StatutCommande, StatutLivraison } from '../models';

/**
 * Service de formatage
 * Centralise les fonctions de formatage pour dates, statuts, etc.
 */
@Injectable({
  providedIn: 'root'
})
export class FormateurService {

  /**
   * Retourne le libellé français d'un statut de commande
   * @param statut - Statut à traduire
   */
  getLibelleStatutCommande(statut: StatutCommande | string): string {
    const libelles: { [key: string]: string } = {
      'enAttente': 'En attente',
      'confirmee': 'Confirmée',
      'enPreparation': 'En préparation',
      'prete': 'Prête',
      'enLivraison': 'En livraison',
      'livree': 'Livrée',
      'annulee': 'Annulée',
      // Anciens statuts en anglais pour compatibilité
      'pending': 'En attente',
      'confirmed': 'Confirmée',
      'preparing': 'En préparation',
      'ready': 'Prête',
      'delivering': 'En livraison',
      'delivered': 'Livrée',
      'cancelled': 'Annulée'
    };
    return libelles[statut] || statut;
  }

  /**
   * Retourne le libellé français d'un statut de livraison
   * @param statut - Statut à traduire
   */
  getLibelleStatutLivraison(statut: StatutLivraison | string): string {
    const libelles: { [key: string]: string } = {
      'EN_ATTENTE': 'En attente',
      'EN_COURS': 'En cours',
      'LIVREE': 'Livrée',
      // Anciens statuts en anglais pour compatibilité
      'PENDING': 'En attente',
      'IN_PROGRESS': 'En cours',
      'DELIVERED': 'Livrée'
    };
    return libelles[statut] || statut;
  }

  /**
   * Retourne la couleur du chip selon le statut
   * @param statut - Statut de la commande
   */
  getCouleurStatut(statut: string): string {
    const couleurs: { [key: string]: string } = {
      'enAttente': 'warn',
      'confirmee': 'primary',
      'enPreparation': 'accent',
      'prete': 'primary',
      'enLivraison': 'accent',
      'livree': 'primary',
      'annulee': 'warn',
      // Anciens statuts
      'pending': 'warn',
      'confirmed': 'primary',
      'preparing': 'accent',
      'ready': 'primary',
      'delivering': 'accent',
      'delivered': 'primary',
      'cancelled': 'warn',
      // Statuts livraison
      'EN_ATTENTE': 'warn',
      'EN_COURS': 'accent',
      'LIVREE': 'primary',
      'PENDING': 'warn',
      'IN_PROGRESS': 'accent',
      'DELIVERED': 'primary'
    };
    return couleurs[statut] || '';
  }

  /**
   * Formate une date en format français
   * @param dateStr - Date ISO string
   */
  formaterDate(dateStr: string): string {
    const date = new Date(dateStr);
    return date.toLocaleDateString('fr-CA', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  /**
   * Retourne l'icône Material correspondant à la catégorie
   * @param categorie - Catégorie de cuisine
   */
  getIconeCategorie(categorie: string): string {
    const icones: { [key: string]: string } = {
      'Tous': 'apps',
      'Italien': 'local_pizza',
      'Fast Food': 'lunch_dining',
      'Japonais': 'ramen_dining',
      'Québécois': 'restaurant',
      'Africain': 'dinner_dining'
    };
    return icones[categorie] || 'restaurant';
  }
}
