import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { AuthService } from '../../../core/services/auth.service';
import { Utilisateur } from '../../../core/models';
import { NotificationService } from '../../../core/services/notification.service';

// Page de profil utilisateur

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatListModule,
    MatDividerModule,
    MatFormFieldModule,
    MatInputModule
  ],
  templateUrl: './profil.html',
  styleUrl: './profil.css'
})
export class Profil implements OnInit {
  utilisateur: Utilisateur | null = null;

  modeEdition = false;

  // Données du formulaire d'édition
  formulaire = {
    prenom: '',
    nom: '',
    telephone: '',
    adresse: ''
  };

  constructor(
    private readonly serviceAuth: AuthService,
    private readonly routeur: Router,
    private readonly notification: NotificationService
  ) {}

  ngOnInit(): void {
    this.utilisateur = this.serviceAuth.getUtilisateurCourant();
    if (this.utilisateur) {
      this.formulaire = {
        prenom: this.utilisateur.prenom,
        nom: this.utilisateur.nom,
        telephone: this.utilisateur.telephone,
        adresse: this.utilisateur.adresse
      };
    }
  }

  // Active le mode édition

  activerEdition(): void {
    this.modeEdition = true;
  }

  // Annule l'édition

  annulerEdition(): void {
    this.modeEdition = false;
    if (this.utilisateur) {
      this.formulaire = {
        prenom: this.utilisateur.prenom,
        nom: this.utilisateur.nom,
        telephone: this.utilisateur.telephone,
        adresse: this.utilisateur.adresse
      };
    }
  }

  // Sauvegarde les modifications du profil
  sauvegarder(): void {
    if (!this.utilisateur) return;

    this.serviceAuth.mettreAJourProfil({
      prenom: this.formulaire.prenom,
      nom: this.formulaire.nom,
      telephone: this.formulaire.telephone,
      adresse: this.formulaire.adresse
    });

    this.utilisateur = this.serviceAuth.getUtilisateurCourant();
    this.modeEdition = false;
    this.notification.afficherSucces('Profil mis à jour');
  }

  // Déconnecte l'utilisateur et redirige vers auth

  deconnecter(): void {
    this.serviceAuth.deconnexion();
    this.notification.afficher('Déconnexion réussie');
    this.routeur.navigate(['/auth']);
  }

  // Retourne le nom complet de l'utilisateur

  get nomComplet(): string {
    if (!this.utilisateur) return '';
    return `${this.utilisateur.prenom} ${this.utilisateur.nom}`;
  }

  // Retourne le libellé du rôle en français

  get libelleRole(): string {
    if (!this.utilisateur) return '';
    if (this.utilisateur.role === 'client') return 'Client';
    if (this.utilisateur.role === 'livreur') return 'Livreur';
    return 'Restaurateur';
  }
}
