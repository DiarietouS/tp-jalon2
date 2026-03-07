import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormsModule} from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatTabsModule } from '@angular/material/tabs';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';
import { RoleUtilisateur } from '../../core/models';
import { FormFieldDirective, form, required, email as vEmail, minLength as vMinLength } from '../../shared/signal-forms';

export interface LoginDTO {
  courriel: string;
  motDePasse: string;
}

// Authentification
@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormFieldDirective,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatTabsModule,
    FormsModule
  ],
  templateUrl: './auth.html',
  styleUrl: './auth.css'
})
export class Auth {

  ongletActif = signal<number>(0);

  enChargementConnexion = signal<boolean>(false);
  enChargementInscription = signal<boolean>(false);

  masquerMotDePasseConnexion = signal<boolean>(true);
  masquerMotDePasseInscription = signal<boolean>(true);
  masquerConfirmation = signal<boolean>(true);

  // Formulaire de connexion
  loginModel = signal<LoginDTO>({ courriel: '', motDePasse: '' });
  loginForm = form(this.loginModel, (s) => [
    required(s.courriel, { message: 'Email requis' }),
    vEmail(s.courriel, { message: 'Email invalide' }),
    required(s.motDePasse, { message: 'Mot de passe requis' }),
    vMinLength(s.motDePasse, 6, { message: 'Min. 6 caractères' }),
  ]);

  // Formulaire d'inscription

  formulaireInscription: FormGroup;

  // Constructeur
  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly notification: NotificationService
  ) {
    this.formulaireInscription = this.fb.group({
      prenom: ['', [Validators.required, Validators.minLength(2)]],
      nom: ['', [Validators.required, Validators.minLength(2)]],
      courriel: ['', [Validators.required, Validators.email]],
      motDePasse: ['', [Validators.required, Validators.minLength(6)]],
      confirmationMotDePasse: ['', Validators.required],
      telephone: [''],
      adresse: [''],
      role: ['client' as RoleUtilisateur]
    });
  }

  seConnecter(): void {
    if (!this.loginForm().valid()) {
      // Marquer touchés pour afficher les erreurs
      this.loginForm['courriel']().markTouched();
      this.loginForm['motDePasse']().markTouched();
      this.notification.afficherErreur('Veuillez remplir tous les champs correctement');
      return;
    }

    this.enChargementConnexion.set(true);
    const { courriel, motDePasse } = this.loginModel();

    this.authService.connexion(courriel, motDePasse).subscribe(utilisateur => {
      this.enChargementConnexion.set(false);

      if (utilisateur) {
        this.notification.afficherBienvenue(utilisateur.prenom);
        if (utilisateur.role === 'restaurateur') {
          this.router.navigate(['/restaurateur/gestion']);
        } else if (utilisateur.role === 'livreur') {
          this.router.navigate(['/livreur/commandes']);
        } else {
          this.router.navigate(['/restaurants']);
        }
      } else {
        this.notification.afficherErreur('Email ou mot de passe incorrect');
      }
    });
  }


  sInscrire(): void {
    if (this.formulaireInscription.invalid) {
      this.formulaireInscription.markAllAsTouched();
      this.notification.afficherErreur('Veuillez remplir tous les champs obligatoires');
      return;
    }

    const valeurs = this.formulaireInscription.value;

    // Vérifier si les mots de passe correspondent
    if (valeurs.motDePasse !== valeurs.confirmationMotDePasse) {
      this.notification.afficherErreur('Les mots de passe ne correspondent pas');
      return;
    }

    // Vérifier si l'email existe déjà
    if (this.authService.courrielExiste(valeurs.courriel)) {
      this.notification.afficherErreur('Cet email est déjà utilisé');
      return;
    }

    this.enChargementInscription.set(true);

    this.authService.inscription({
      prenom: valeurs.prenom,
      nom: valeurs.nom,
      courriel: valeurs.courriel,
      motDePasse: valeurs.motDePasse,
      telephone: valeurs.telephone,
      adresse: valeurs.adresse,
      role: valeurs.role
    }).subscribe(utilisateur => {
      this.enChargementInscription.set(false);
      this.notification.afficherSucces(`Bienvenue ${utilisateur.prenom} ! Votre compte a été créé.`);

      // Redirection
      if (utilisateur.role === 'restaurateur') {
        this.router.navigate(['/restaurateur/gestion']);
      } else if (utilisateur.role === 'livreur') {
        this.router.navigate(['/livreur/commandes']);
      } else {
        this.router.navigate(['/restaurants']);
      }
    });
  }


  basculerVisibilite(champ: 'connexion' | 'inscription' | 'confirmation'): void {
    switch (champ) {
      case 'connexion':
        this.masquerMotDePasseConnexion.update(v => !v);
        break;
      case 'inscription':
        this.masquerMotDePasseInscription.update(v => !v);
        break;
      case 'confirmation':
        this.masquerConfirmation.update(v => !v);
        break;
    }
  }
}
