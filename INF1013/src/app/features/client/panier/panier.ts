import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

import { CartService } from '../../../core/services/cart.service';
import { AuthService } from '../../../core/services/auth.service';
import { OrderService } from '../../../core/services/order.service';
import { ArticlePanier } from '../../../core/models/cart';
import { NotificationService } from '../../../core/services/notification.service';

/**
 * ============================================================================
 * COMPOSANT PANIER - Page du panier d'achat
 * ============================================================================
 * 
 * COURS INF1013 - CONSOMMATION DE SIGNALS
 * ---------------------------------------
 * "La valeur d'un signal se lit en appelant sa fonction d'accès (getter),
 * ce qui permet à Angular de suivre son utilisation."
 * 
 * Dans ce composant, on consomme les signals du CartService:
 * - servicePanier.panier() pour lire le panier
 * - servicePanier.nombreArticles() pour le nombre d'articles
 * - servicePanier.sousTotal() et servicePanier.total() pour les montants
 * 
 * @see Diapo: C'est quoi un signal, Computed Signal
 * ============================================================================
 */
@Component({
  selector: 'app-panier',
  imports: [RouterLink, DecimalPipe, MatIconModule, MatButtonModule],
  templateUrl: './panier.html',
  styleUrl: './panier.css',
})
export class Panier {
  
  constructor(
    public servicePanier: CartService,
    private readonly serviceAuth: AuthService,
    private readonly serviceCommandes: OrderService,
    private readonly routeur: Router,
    private readonly notification: NotificationService
  ) {}

  /**
   * Augmente la quantité d'un article
   */
  augmenterQuantite(article: ArticlePanier): void {
    this.servicePanier.modifierQuantite(article.plat.id, article.quantite + 1);
  }

  /**
   * Diminue la quantité d'un article
   */
  diminuerQuantite(article: ArticlePanier): void {
    this.servicePanier.modifierQuantite(article.plat.id, article.quantite - 1);
  }

  /**
   * Supprime un article du panier
   */
  supprimerArticle(article: ArticlePanier): void {
    this.servicePanier.supprimerDuPanier(article.plat.id);
  }

  /**
   * Passe à la commande
   * Vérifie que l'utilisateur est connecté avant de commander
   * 
   * COURS INF1013: Lecture des signals avec ()
   */
  commander(): void {
    // Vérifier si l'utilisateur est connecté
    if (!this.serviceAuth.estConnecte()) {
      this.notification.afficherErreur('Vous devez être connecté pour commander');
      this.routeur.navigate(['/login']);
      return;
    }

    // COURS INF1013: Lecture du signal utilisateur avec ()
    const utilisateur = this.serviceAuth.utilisateurCourant();
    
    // COURS INF1013: Lecture du signal panier avec ()
    const panier = this.servicePanier.panier();

    if (!panier || panier.articles.length === 0) {
      this.notification.afficherErreur('Votre panier est vide');
      return;
    }

    // Créer la commande avec les valeurs des signals
    this.serviceCommandes.creerCommande({
      idClient: utilisateur?.id,
      idRestaurant: panier.idRestaurant,
      nomRestaurant: panier.nomRestaurant,
      articles: panier.articles.map(a => ({
        idPlat: a.plat.id,
        nomPlat: a.plat.nom,
        quantite: a.quantite,
        prixUnitaire: a.plat.prix
      })),
      sousTotal: this.servicePanier.sousTotal(),
      fraisLivraison: panier.fraisLivraison,
      total: this.servicePanier.total(),
      adresseLivraison: utilisateur?.adresse || '',
      telephone: utilisateur?.telephone || ''
    }).subscribe({
      next: () => {
        this.notification.afficherSucces('Commande passée avec succès !');
        this.servicePanier.viderPanier();
        this.routeur.navigate(['/commandes']);
      },
      error: () => {
        this.notification.afficherErreur('Erreur lors de la commande');
      }
    });
  }
}
