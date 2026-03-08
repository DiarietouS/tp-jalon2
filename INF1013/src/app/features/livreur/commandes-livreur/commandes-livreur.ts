import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';

import { Commande } from '../../../core/models';
import { OrderService } from '../../../core/services/order.service';
import { NotificationService } from '../../../core/services/notification.service';


@Component({
  selector: 'app-commandes-livreur',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatListModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatDividerModule,
  ],
  templateUrl: './commandes-livreur.html',
  styleUrl: './commandes-livreur.css',
})
export class CommandesLivreur implements OnInit {
  enChargement = signal<boolean>(true);
  commandes = signal<Commande[]>([]);

  constructor(
    private readonly orderService: OrderService,
    private readonly notification: NotificationService
  ) {}

  ngOnInit(): void {
    this.orderService.getCommandes().subscribe((liste) => {
      // Un livreur voit les commandes prêtes ou en livraison
      const filtre = (liste ?? []).filter(
        (c) => c.statut === 'prete' || c.statut === 'enLivraison'
      );
      this.commandes.set(filtre);
      this.enChargement.set(false);
    });
  }


  fermerCommande(commande: Commande): void {
    this.orderService.mettreAJourStatut(commande.id, 'livree').subscribe(() => {
      this.notification.afficherSucces(`Commande #${commande.id} fermée (livrée).`);
      // Retirer de la liste "à livrer"
      this.commandes.update((liste) => liste.filter((c) => c.id !== commande.id));
    });
  }

  etiquetteStatut(statut: Commande['statut']): string {
    switch (statut) {
      case 'prete':
        return 'Prête';
      case 'enLivraison':
        return 'En livraison';
      case 'livree':
        return 'Livrée';
      default:
        return statut;
    }
  }
}
