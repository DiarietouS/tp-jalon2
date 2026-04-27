import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormsModule} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
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

  // --- Recouvrement de mot de passe ---
  enChargementRecuveration = signal<boolean>(false);
  jetonRecut = signal<string | null>(null);      // affiché en dev uniquement
  etapeRecuperation = signal<'demande' | 'reinitialisation'>('demande');
  courrielRecuperation = signal<string>('');
  nouveauMotDePasse = signal<string>('');
  confirmNouveauMotDePasse = signal<string>('');
  jetonSaisi = signal<string>('');

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
    private readonly activatedRoute: ActivatedRoute,
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
        this.redirigerApresAuthentification(utilisateur.role);
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
      this.redirigerApresAuthentification(utilisateur.role);
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

  // --- Recouvrement de mot de passe ---

  demanderReinitialisationMotDePasse(): void {
    const courriel = this.courrielRecuperation().trim();
    if (!courriel) {
      this.notification.afficherErreur('Veuillez entrer votre adresse courriel');
      return;
    }
    this.enChargementRecuveration.set(true);
    this.authService.motDePasseOubli(courriel).subscribe({
      next: (response) => {
        this.enChargementRecuveration.set(false);
        if (response.jeton) {
          // Dev: afficher le token à l'écran (en prod il serait envoyé par email)
          this.jetonRecut.set(response.jeton);
          this.etapeRecuperation.set('reinitialisation');
          this.notification.afficherSucces('Token généré — entrez-le ci-dessous avec votre nouveau mot de passe');
        } else {
          this.notification.afficherSucces(response.message);
        }
      },
      error: () => {
        this.enChargementRecuveration.set(false);
        this.notification.afficherErreur('Erreur lors de la demande de réinitialisation');
      }
    });
  }

  reinitialiserMotDePasse(): void {
    const jeton = this.jetonSaisi().trim();
    const mdp = this.nouveauMotDePasse().trim();
    const confirm = this.confirmNouveauMotDePasse().trim();

    if (!jeton) {
      this.notification.afficherErreur('Veuillez entrer le token de réinitialisation');
      return;
    }
    if (mdp.length < 6) {
      this.notification.afficherErreur('Le mot de passe doit contenir au moins 6 caractères');
      return;
    }
    if (mdp !== confirm) {
      this.notification.afficherErreur('Les mots de passe ne correspondent pas');
      return;
    }

    this.enChargementRecuveration.set(true);
    this.authService.reinitialiserMotDePasse(jeton, mdp).subscribe({
      next: () => {
        this.enChargementRecuveration.set(false);
        this.notification.afficherSucces('Mot de passe réinitialisé avec succès! Vous pouvez vous connecter.');
        this.etapeRecuperation.set('demande');
        this.jetonRecut.set(null);
        this.jetonSaisi.set('');
        this.nouveauMotDePasse.set('');
        this.confirmNouveauMotDePasse.set('');
        this.ongletActif.set(0);  // Retour à l'onglet connexion
      },
      error: (err) => {
        this.enChargementRecuveration.set(false);
        const msg = err?.error?.message || 'Token invalide ou expiré';
        this.notification.afficherErreur(msg);
      }
    });
  }

  private redirigerApresAuthentification(role: RoleUtilisateur): void {
    const returnUrl = this.activatedRoute.snapshot.queryParamMap.get('returnUrl');
    if (returnUrl && returnUrl.startsWith('/')) {
      this.router.navigateByUrl(returnUrl);
      return;
    }

    if (role === 'restaurateur') {
      this.router.navigate(['/restaurateur/gestion']);
      return;
    }

    if (role === 'livreur') {
      this.router.navigate(['/livreur/commandes']);
      return;
    }

    this.router.navigate(['/restaurants']);
  }
}
