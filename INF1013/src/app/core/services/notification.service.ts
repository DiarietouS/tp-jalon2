import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

/**
 * Service de notifications
 * Centralise l'affichage des messages à l'utilisateur via SnackBar
 */
@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private snackBar: MatSnackBar) {}

  /**
   * Affiche un message standard
   * @param message - Message à afficher
   * @param duree - Durée en ms (défaut: 3000)
   */
  afficher(message: string, duree: number = 3000): void {
    this.snackBar.open(message, 'OK', { duration: duree });
  }

  /**
   * Affiche un message de succès
   * @param message - Message à afficher
   */
  afficherSucces(message: string): void {
    this.snackBar.open(message, 'OK', { 
      duration: 3000,
      panelClass: ['succes-snackbar']
    });
  }

  /**
   * Affiche un message d'erreur
   * @param message - Message à afficher
   */
  afficherErreur(message: string): void {
    this.snackBar.open(message, 'OK', { 
      duration: 4000,
      panelClass: ['erreur-snackbar']
    });
  }

  /**
   * Affiche un message de bienvenue
   * @param prenom - Prénom de l'utilisateur
   */
  afficherBienvenue(prenom: string): void {
    this.snackBar.open(`Bienvenue ${prenom} !`, 'OK', { duration: 2000 });
  }
}
