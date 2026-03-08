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


@Component({
  selector: 'app-gestion',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
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


  ongletActif = signal<number>(0);


  plats: Plat[] = [];
  categories = ['Entrées', 'Plats principaux', 'Desserts', 'Boissons', 'Accompagnements'];


  restaurant: RestaurantModel | null = null;
  modeEditionRestaurant = signal<boolean>(false);


  formulairePlat!: FormGroup;


  formulaireRestaurant!: FormGroup;

  // Signals
  enChargement = signal<boolean>(true);
  messageErreur = signal<string | null>(null);


  constructor(
    private readonly platsService: DishesService,
    private readonly restaurantService: RestaurantService,
    private readonly authService: AuthService,
    private readonly fb: FormBuilder,
    private readonly notification: NotificationService,
    private readonly cdr: ChangeDetectorRef
  ) {}


  ngOnInit(): void {

    this.formulairePlat = this.fb.group({
      nom: ['', Validators.required],
      description: [''],
      prix: [null, [Validators.required, Validators.min(0)]],
      categorie: ['', Validators.required],
      imageUrl: [''],
      disponible: [true]
    });


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
      idRestaurant: this.restaurant.id
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

  // Supprime un plat

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

  // Annule l'édition du restaurant

  annulerEditionRestaurant(): void {
    this.modeEditionRestaurant.set(false);
    this.formulaireRestaurant.reset();
  }

  // Sauvegarde les modifications du restaurant

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
