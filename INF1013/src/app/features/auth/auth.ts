import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
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

/**
 * ============================================================================
 * COMPOSANT AUTH - Authentification (Connexion/Inscription)
 * ============================================================================
 * 
 * COURS INF1013 - REACTIVE FORMS (Controller Driven Forms)
 * --------------------------------------------------------
 * "Angular utilise 2 approches pour gérer les formulaires:
 * Les Reactive Forms (RF) - Plus structurées, mise à l'échelle plus facile"
 * 
 * "La construction des RF débute dans le contrôleur.
 * On les appelle aussi Controller Driven Forms."
 * 
 * FORMGROUP ET FORMCONTROL:
 * "Les RF permettent un regroupement logique des formulaires avec FormGroup.
 * L'objectif est d'obtenir une structure de données cohérente (json)"
 * 
 * FORMBUILDER (Pattern Fabrique):
 * "Pour éviter les constructions lourdes des instances de chaque formControl,
 * on utilise un service de fabrique à l'aide du composant FormBuilder"
 * 
 * VALIDATION:
 * "Le formulaire a une variable d'état valid. Pour n'activer la soumission
 * que si le formulaire est valide: [disabled]='!studentForm.valid'"
 * 
 * @see Diapo: Les Reactive Forms, Validation des Formulaires RF
 * ============================================================================
 */
@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,  // COURS INF1013: Module pour Reactive Forms
    FormFieldDirective,   // COURS INF1013: Signal Forms (pattern) - [formField]
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatTabsModule
  ],
  templateUrl: './auth.html',
  styleUrl: './auth.css'
})
export class Auth {
  
  /**
   * Index de l'onglet actif (signal mutable)
   * COURS INF1013: "Les signaux modifiables offrent des fonctionnalités
   * permettant de mettre à jour directement leurs valeurs"
   */
  ongletActif = signal<number>(0);

  /** États de chargement (signals) */
  enChargementConnexion = signal<boolean>(false);
  enChargementInscription = signal<boolean>(false);
  
  /** Masquage des mots de passe (signals) */
  masquerMotDePasseConnexion = signal<boolean>(true);
  masquerMotDePasseInscription = signal<boolean>(true);
  masquerConfirmation = signal<boolean>(true);

  /**
   * Formulaire de connexion (Signal Forms - pattern)
   *
   * COURS INF1013 - SIGNAL FORMS:
   * "Créer un model signal mutable typé avec votre DTO"
   * "Créer un formulaire signal à partir du modèle de signal"
   * "Validation centralisée" + "UI réactif"
   */
  loginModel = signal<LoginDTO>({ courriel: '', motDePasse: '' });
  loginForm = form(this.loginModel, (s) => [
    required(s.courriel, { message: 'Email requis' }),
    vEmail(s.courriel, { message: 'Email invalide' }),
    required(s.motDePasse, { message: 'Mot de passe requis' }),
    vMinLength(s.motDePasse, 6, { message: 'Min. 6 caractères' }),
  ]);

  /**
   * Formulaire d'inscription (Reactive Form avec validation)
   * COURS INF1013 - VALIDATION:
   * "fname: ['', [Validators.required, Validators.minLength(2)]]"
   */
  formulaireInscription: FormGroup;

  /**
   * Constructeur avec injection de dépendances
   * COURS INF1013: "Le composant FormBuilder est injecté"
   */
  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly notification: NotificationService
  ) {
    // COURS INF1013: Création des FormGroups avec FormBuilder (RF)
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

  /**
   * Soumettre le formulaire de connexion
   * COURS INF1013: "[disabled]='!form.valid'" pour n'activer que si valide
   */
  seConnecter(): void {
    if (!this.loginForm().valid()) {
      // Marquer touchés pour afficher les erreurs
      this.loginForm.courriel().markTouched();
      this.loginForm.motDePasse().markTouched();
      this.notification.afficherErreur('Veuillez remplir tous les champs correctement');
      return;
    }

    this.enChargementConnexion.set(true);
    const { courriel, motDePasse } = this.loginModel();

    this.authService.connexion(courriel, motDePasse).subscribe(utilisateur => {
      this.enChargementConnexion.set(false);
      
      if (utilisateur) {
        this.notification.afficherBienvenue(utilisateur.prenom);
        // Rediriger selon le rôle
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

  /**
   * Soumettre le formulaire d'inscription
   * COURS INF1013 - Validation:
   * "Le formulaire a une variable d'état valid qui détermine
   * l'état de validité du formulaire à chaque modification"
   */
  sInscrire(): void {
    if (this.formulaireInscription.invalid) {
      this.formulaireInscription.markAllAsTouched();
      this.notification.afficherErreur('Veuillez remplir tous les champs obligatoires');
      return;
    }

    const valeurs = this.formulaireInscription.value;

    // Vérifier que les mots de passe correspondent
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
      
      // Rediriger selon le rôle
      if (utilisateur.role === 'restaurateur') {
        this.router.navigate(['/restaurateur/gestion']);
      } else if (utilisateur.role === 'livreur') {
        this.router.navigate(['/livreur/commandes']);
      } else {
        this.router.navigate(['/restaurants']);
      }
    });
  }

  /**
   * Bascule la visibilité du mot de passe
   * COURS INF1013: "this.count.update(oldValue => !oldValue)"
   */
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
