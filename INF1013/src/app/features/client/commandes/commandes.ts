import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { OrderService } from '../../../core/services/order.service';
import { AuthService } from '../../../core/services/auth.service';
import { FormateurService } from '../../../core/services/formateur.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Commande } from '../../../core/models';
import { RestaurantService } from '../../../core/services/restaurant.service';
import { RestaurantModel } from '../../../core/models/restaurant';
import { DateRelativePipe, PrixPipe } from '../../../shared/pipes';


@Component({
  selector: 'app-commandes',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatDividerModule,
    DateRelativePipe,
    PrixPipe
  ],
  templateUrl: './commandes.html',
  styleUrl: './commandes.css'
})
export class Commandes implements OnInit {
  // Données pour vue client
  commandes: Commande[] = [];

  // Données pour vue restaurateur
  restaurantRestaurateur: RestaurantModel | null = null;
  commandesRestaurateur: Commande[] = [];

  // États
  enChargement = true;
  messageErreur: string | null = null;
  estRestaurateur = false;

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private restaurantService: RestaurantService,
    private formateur: FormateurService,
    private notification: NotificationService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const utilisateur = this.authService.getUtilisateurCourant();
    this.estRestaurateur = utilisateur?.role === 'restaurateur';

    if (this.estRestaurateur) {
      this.chargerCommandesRestaurateur();
    } else {
      this.chargerCommandes();
    }
  }

  // Charge les commandes de l'utilisateur connecté

  private chargerCommandes(): void {
    const utilisateur = this.authService.getUtilisateurCourant();

    if (utilisateur) {
      this.orderService.getCommandesParUtilisateur(utilisateur.id).subscribe({
        next: (commandes) => {
          // Trier par date décroissante (plus récent en premier)
          this.commandes = commandes.sort((a, b) =>
            new Date(b.creeLe).getTime() - new Date(a.creeLe).getTime()
          );
          this.enChargement = false;
          this.cdr.detectChanges();
        },
        error: () => {
          this.commandes = [];
          this.enChargement = false;
          this.cdr.detectChanges();
        }
      });
    } else {
      // Si non connecté, afficher toutes les commandes
      this.orderService.getCommandes().subscribe({
        next: (commandes) => {
          this.commandes = commandes.sort((a, b) =>
            new Date(b.creeLe).getTime() - new Date(a.creeLe).getTime()
          );
          this.enChargement = false;
          this.cdr.detectChanges();
        },
        error: () => {
          this.commandes = [];
          this.enChargement = false;
          this.cdr.detectChanges();
        }
      });
    }
  }

  // Charge les commandes du restaurant appartenant au restaurateur connecté.

  private chargerCommandesRestaurateur(): void {
    this.enChargement = true;
    this.messageErreur = null;

    const utilisateur = this.authService.getUtilisateurCourant();
    if (!utilisateur) {
      this.messageErreur = 'Vous devez être connecté.';
      this.enChargement = false;
      return;
    }

    this.restaurantService.loadRestaurants().subscribe({
      next: (restaurants) => {
        this.restaurantRestaurateur = restaurants.find(r => r.idProprietaire === utilisateur.id) || null;
        if (!this.restaurantRestaurateur) {
          this.messageErreur = 'Aucun restaurant associé à votre compte.';
          this.enChargement = false;
          this.cdr.detectChanges();
          return;
        }

        this.orderService.getCommandesParRestaurant(this.restaurantRestaurateur.id).subscribe({
          next: (commandes) => {
            this.commandesRestaurateur = (commandes ?? []).sort((a, b) =>
              new Date(b.creeLe).getTime() - new Date(a.creeLe).getTime()
            );
            this.enChargement = false;
            this.cdr.detectChanges();
          },
          error: () => {
            this.commandesRestaurateur = [];
            this.enChargement = false;
            this.cdr.detectChanges();
          }
        });
      },
      error: () => {
        this.messageErreur = 'Erreur lors du chargement des restaurants.';
        this.enChargement = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Change le statut d'une commande

  changerStatutCommande(id: number, statut: Commande['statut']): void {
    this.orderService.mettreAJourStatut(id, statut).subscribe(() => {
      const idx = this.commandesRestaurateur.findIndex(c => c.id === id);
      if (idx !== -1) {
        this.commandesRestaurateur[idx] = { ...this.commandesRestaurateur[idx], statut };
        this.commandesRestaurateur = [...this.commandesRestaurateur];
      }
      this.notification.afficherSucces('Statut mis à jour');
      this.cdr.detectChanges();
    });
  }

  // Retourne la couleur du chip selon le statut
  getCouleurStatut(statut: string): string {
    return this.formateur.getCouleurStatut(statut);
  }

  // Retourne le libellé français du statut

  getLibelleStatut(statut: string): string {
    return this.formateur.getLibelleStatutCommande(statut);
  }


}
