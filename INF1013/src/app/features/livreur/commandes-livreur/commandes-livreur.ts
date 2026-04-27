import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';

import { LivraisonModel, StatutLivraison } from '../../../core/models/livraison';
import { LivraisonService } from '../../../core/services/livraison.service';
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
  livraisons = signal<LivraisonModel[]>([]);

  constructor(
    private readonly livraisonService: LivraisonService,
    private readonly notification: NotificationService
  ) {}

  ngOnInit(): void {
    this.livraisonService.chargerLivraisons().subscribe((liste) => {
      // Un livreur voit les livraisons en attente et en cours
      const filtre = (liste ?? []).filter(
        (l) => l.statut === 'EN_ATTENTE' || l.statut === 'EN_COURS'
      );
      this.livraisons.set(filtre);
      this.enChargement.set(false);
    });
  }


  fermerCommande(livraison: LivraisonModel): void {
    this.livraisonService.mettreAJourStatut(livraison.id, 'LIVREE').subscribe(() => {
      this.notification.afficherSucces(`Livraison #${livraison.id} fermée.`);
      this.livraisons.update((liste) => liste.filter((l) => l.id !== livraison.id));
    });
  }

  etiquetteStatut(statut: StatutLivraison): string {
    switch (statut) {
      case 'EN_ATTENTE':
        return 'En attente';
      case 'EN_COURS':
        return 'En livraison';
      case 'LIVREE':
        return 'Livrée';
      default:
        return statut;
    }
  }

  totalLivraison(livraison: LivraisonModel): number {
    return (livraison.lignes || []).reduce((somme, ligne) => somme + (ligne.prixUnitaire * ligne.quantite), 0);
  }
}
