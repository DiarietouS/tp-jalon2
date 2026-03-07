import { Component, OnInit, ChangeDetectorRef, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTabsModule } from '@angular/material/tabs';
import { MatDividerModule } from '@angular/material/divider';

import { Plat } from '../../../core/models/dish';
import { RestaurantModel } from '../../../core/models/restaurant';
import { DishesService } from '../../../core/services/dish.service';
import { RestaurantService } from '../../../core/services/restaurant.service';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';

/**
 * ============================================================================
 * COMPOSANT GESTION - Gestion du restaurant (plats + infos)
 * ============================================================================
 * 
 * COURS INF1013 - REACTIVE FORMS (Controller Driven Forms)
 * --------------------------------------------------------
 * "Angular utilise 2 approches pour gérer les formulaires:
 * Les Reactive Forms (RF) - Plus structurées, mise à l'échelle plus facile,
 * destinés à de gros formulaires."
 * 
 * "La construction des RF débute dans le contrôleur"
 * 
 * FORMBUILDER (Pattern Fabrique):
 * "Pour éviter les constructions lourdes des instances de chaque formControl,
 * on utilise un service de fabrique à l'aide du composant FormBuilder"
 * 
 * "profileForm = this.fb.group({
 *   firstName: [''],
 *   lastName: [''],
 *   address: this.fb.group({
 *     street: [''], city: ['']
 *   })
 * });"
 * 
 * CYCLE DE VIE OnInit:
 * "OnInit doit s'utiliser pour faire des initialisations complexes,
 * juste après la construction. Le constructeur ne doit faire
 * qu'initialiser les variables membres (DI)."
 * 
 * @see Diapo: Les Reactive Forms, Cycle de vie des composants: OnInit
 * ============================================================================
 */
@Component({
  selector: 'app-gestion',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,  // COURS INF1013: Module pour Reactive Forms
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSlideToggleModule,
    MatTabsModule,
    MatDividerModule
  ],
  templateUrl: './gestion.html',
  styleUrl: './gestion.css'
})
export class Gestion implements OnInit {
  
  /**
   * Index de l'onglet actif (signal mutable)
   * COURS INF1013: "Les signaux modifiables offrent des fonctionnalités
   * permettant de mettre à jour directement leurs valeurs"
   */
  ongletActif = signal<number>(0);

  // === Données pour les plats ===
  plats: Plat[] = [];
  categories = ['Entrées', 'Plats principaux', 'Desserts', 'Boissons', 'Accompagnements'];

  // === Données pour le restaurant ===
  restaurant: RestaurantModel | null = null;
  modeEditionRestaurant = signal<boolean>(false);

  /**
   * Formulaire pour les plats (Reactive Form)
   * 
   * COURS INF1013 - FORMGROUP:
   * "Les RF permettent un regroupement logique des formulaires.
   * L'objectif est d'obtenir une structure de données cohérente (json)"
   */
  formulairePlat!: FormGroup;

  /**
   * Formulaire pour les infos restaurant
   * 
   * COURS INF1013 - VALIDATION:
   * "fname: ['', [Validators.required, Validators.minLength(2)]]"
   */
  formulaireRestaurant!: FormGroup;

  // États (signals)
  enChargement = signal<boolean>(true);
  messageErreur = signal<string | null>(null);

  /**
   * Constructeur avec injection de dépendances
   * 
   * COURS INF1013: "La construction d'un composant doit rester simple.
   * Le constructeur ne doit faire qu'initialiser les variables membres (DI).
   * Privilégier OnInit pour récupérer les données."
   */
  constructor(
    private readonly platsService: DishesService,
    private readonly restaurantService: RestaurantService,
    private readonly authService: AuthService,
    private readonly fb: FormBuilder,
    private readonly notification: NotificationService,
    private readonly cdr: ChangeDetectorRef
  ) {}

  /**
   * Initialisation du composant
   * 
   * COURS INF1013 - CYCLE DE VIE OnInit:
   * "OnInit doit s'utiliser pour deux raisons principales:
   * - Faire des initialisations complexes, juste après la construction
   * - Configurer le composant après initialisation des propriétés"
   * 
   * "Privilégier OnInit pour récupérer les données, observer les variables..."
   */
  ngOnInit(): void {
    /**
     * COURS INF1013 - FORMBUILDER:
     * "profileForm = this.fb.group({
     *   firstName: [''],
     *   lastName: [''],
     * });"
     */
    this.formulairePlat = this.fb.group({
      nom: ['', Validators.required],
      description: [''],
      prix: [null, [Validators.required, Validators.min(0)]],
      categorie: ['', Validators.required],
      imageUrl: [''],
      disponible: [true]
    });

    /**
     * COURS INF1013 - VALIDATION des Formulaires RF:
     * "Le module RF propose une famille de validateurs prédéfinis:
     * Validators.required, Validators.email, Validators.min()"
     */
    this.formulaireRestaurant = this.fb.group({
      nom: ['', Validators.required],
      adresse: ['', Validators.required],
      telephone: ['', Validators.required],
      courriel: ['', [Validators.required, Validators.email]],
      typeCuisine: [''],
      imageUrl: [''],
      tempsLivraison: [30, [Validators.required, Validators.min(1)]],
      fraisLivraison: [0, [Validators.required, Validators.min(0)]]
    });

    this.chargerDonnees();
  }

  /**
   * Charge les plats et les infos du restaurant
   * 
   * COURS INF1013 - FILTRAGE PAR PROPRIÉTAIRE:
   * On utilise l'ID de l'utilisateur connecté (restaurateur) pour
   * filtrer les restaurants et n'afficher que celui qu'il possède.
   */
  private chargerDonnees(): void {
    this.enChargement.set(true);
    this.messageErreur.set(null);

    // Charger les plats
    this.platsService.chargerPlats().subscribe({
      next: (data) => {
        this.plats = data;
        this.cdr.detectChanges();
      },
      error: () => {
        this.messageErreur.set("Erreur lors du chargement des plats.");
        this.cdr.detectChanges();
      }
    });

    // Charger le restaurant du restaurateur connecté
    this.restaurantService.loadRestaurants().subscribe({
      next: (liste) => {
        // Récupérer l'utilisateur connecté (restaurateur)
        const utilisateur = this.authService.utilisateurCourant();
        
        if (utilisateur && utilisateur.role === 'restaurateur') {
          // Filtrer pour trouver le restaurant dont l'idProprietaire correspond à l'ID du restaurateur
          this.restaurant = liste.find(r => r.idProprietaire === utilisateur.id) || null;
        } else {
          this.restaurant = null;
        }

        if (!this.restaurant) {
          this.messageErreur.set('Aucun restaurant associé à votre compte.');
        }
        this.enChargement.set(false);
        this.cdr.detectChanges();
      },
      error: () => {
        this.messageErreur.set('Erreur chargement restaurant.');
        this.enChargement.set(false);
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Ajoute un nouveau plat
   * 
   * COURS INF1013 - VALIDATION:
   * "markAllAsTouched() force l'affichage des erreurs de validation"
   */
  ajouterPlat(): void {
    if (this.formulairePlat.invalid) {
      this.formulairePlat.markAllAsTouched();
      return;
    }

    // Vérifier qu'un restaurant est associé au compte
    if (!this.restaurant) {
      this.messageErreur.set("Impossible d'ajouter un plat: aucun restaurant associé.");
      return;
    }

    const valeurs = this.formulairePlat.getRawValue();

    const idNouveau = this.plats.length > 0
      ? Math.max(...this.plats.map(p => p.id)) + 1
      : 1;

    const nouveauPlat: Plat = {
      id: idNouveau,
      nom: valeurs.nom,
      description: valeurs.description || '',
      prix: valeurs.prix,
      categorie: valeurs.categorie,
      imageUrl: valeurs.imageUrl || '',
      disponible: valeurs.disponible,
      idRestaurant: this.restaurant.id  // Utilise l'ID du restaurant du restaurateur
    };

    this.platsService.ajouterPlat(nouveauPlat).subscribe({
      next: (liste) => {
        this.plats = liste;
        this.formulairePlat.reset({ disponible: true });
        this.notification.afficherSucces('Plat ajouté avec succès');
        this.cdr.detectChanges();
      },
      error: () => {
        this.messageErreur.set("Erreur lors de l'ajout.");
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Supprime un plat
   */
  supprimerPlat(id: number): void {
    this.platsService.supprimerPlat(id).subscribe({
      next: (liste) => {
        this.plats = liste;
        this.notification.afficher('Plat supprimé');
        this.cdr.detectChanges();
      },
      error: () => {
        this.messageErreur.set("Erreur lors de la suppression.");
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Active le mode édition du restaurant
   * 
   * COURS INF1013 - patchValue():
   * "patchValue() pour les formGroups permet de faire
   * une projection des valeurs dans le formulaire"
   */
  activerEditionRestaurant(): void {
    if (this.restaurant) {
      this.formulaireRestaurant.patchValue({
        nom: this.restaurant.nom,
        adresse: this.restaurant.adresse,
        telephone: this.restaurant.telephone,
        courriel: this.restaurant.courriel,
        typeCuisine: this.restaurant.typeCuisine,
        imageUrl: this.restaurant.imageUrl,
        tempsLivraison: this.restaurant.tempsLivraison,
        fraisLivraison: this.restaurant.fraisLivraison
      });
      this.modeEditionRestaurant.set(true);
    }
  }

  /**
   * Annule l'édition du restaurant
   */
  annulerEditionRestaurant(): void {
    this.modeEditionRestaurant.set(false);
    this.formulaireRestaurant.reset();
  }

  /**
   * Sauvegarde les modifications du restaurant
   */
  sauvegarderRestaurant(): void {
    if (this.formulaireRestaurant.invalid || !this.restaurant) {
      this.formulaireRestaurant.markAllAsTouched();
      return;
    }

    const valeurs = this.formulaireRestaurant.getRawValue();
    this.restaurantService.mettreAJourRestaurant(this.restaurant.id, {
      nom: valeurs.nom,
      adresse: valeurs.adresse,
      telephone: valeurs.telephone,
      courriel: valeurs.courriel,
      typeCuisine: valeurs.typeCuisine,
      imageUrl: valeurs.imageUrl,
      tempsLivraison: valeurs.tempsLivraison,
      fraisLivraison: valeurs.fraisLivraison
    }).subscribe({
      next: (updatedRestaurant) => {
        if (updatedRestaurant) {
          this.restaurant = updatedRestaurant;
          this.modeEditionRestaurant.set(false);
          this.notification.afficherSucces('Restaurant mis à jour avec succès');
          this.cdr.detectChanges();
        }
      },
      error: () => {
        this.messageErreur.set("Erreur lors de la mise à jour.");
        this.cdr.detectChanges();
      }
    });
  }
}
