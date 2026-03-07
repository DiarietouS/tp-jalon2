import { Component, input, computed, Signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';

// Angular Material
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

/**
 * Item de navigation dans le menu du bas
 */
interface ElementNav {
  icone: string;       // Nom de l'icône Material
  libelle: string;     // Texte affiché
  route: string;       // Route Angular
}

/**
 * ============================================================================
 * COMPOSANT BOTTOM-NAV - Navigation du bas
 * ============================================================================
 * 
 * COURS INF1013 - INPUT.REQUIRED<T>()
 * -----------------------------------
 * "input.required<T>() force le parent à fournir une valeur.
 * Si le parent ne fournit pas la valeur, Angular génère une erreur."
 * 
 * "Déclaration du côté enfant:
 * detailedStudent = input.required<StudentModel>();"
 * 
 * COMPUTED SIGNAL:
 * "Un signal calculé est un cas particulier de signal Immutable.
 * const doubleCount: Signal<number> = computed(() => this.count() * 2)"
 * 
 * @see Diapo: Signal: input.required<T>(), Copie Immutable d'un Signal Mutable
 * ============================================================================
 */
@Component({
  selector: 'app-bottom-nav',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './bottom-nav.html',
  styleUrl: './bottom-nav.css',
})
export class BottomNav {
  
  /**
   * Rôle de l'utilisateur (signal d'entrée OBLIGATOIRE)
   * Change les items affichés selon le rôle
   * 
   * COURS INF1013 - INPUT.REQUIRED:
   * "input.required<T>() force le parent à fournir une valeur.
   * Si le parent ne fournit pas la valeur, Angular génère une erreur."
   * 
   * Le parent DOIT fournir [roleUtilisateur]="'client'" ou "'restaurateur'"
   */
  roleUtilisateur = input.required<'client' | 'restaurateur' | 'livreur'>();

  // Items de navigation pour les clients
  private readonly elementsClient: ElementNav[] = [
    { icone: 'home', libelle: 'Restaurants', route: '/restaurants' },
    { icone: 'receipt_long', libelle: 'Commandes', route: '/commandes' },
    { icone: 'shopping_cart', libelle: 'Panier', route: '/panier' },
    { icone: 'person', libelle: 'Profil', route: '/profil' }
  ];

  // Items de navigation pour les restaurateurs
  private readonly elementsRestaurateur: ElementNav[] = [
    { icone: 'delivery_dining', libelle: 'Livraisons', route: '/commandes' },
    { icone: 'restaurant_menu', libelle: 'Gestion', route: '/restaurateur/gestion' },
    { icone: 'person', libelle: 'Profil', route: '/profil' }
  ];

  // Items de navigation pour les livreurs
  private readonly elementsLivreur: ElementNav[] = [
    { icone: 'delivery_dining', libelle: 'À livrer', route: '/livreur/commandes' },
    { icone: 'person', libelle: 'Profil', route: '/profil' }
  ];

  /**
   * Signal calculé pour les éléments de navigation
   * 
   * COURS INF1013: "Un signal calculé est un cas particulier de signal Immutable.
   * Quand le signal source change, sa dérivée (computed) est recalculée."
   */
  readonly elementsNav: Signal<ElementNav[]> = computed(() => {
    const role = this.roleUtilisateur();
    if (role === 'restaurateur') return this.elementsRestaurateur;
    if (role === 'livreur') return this.elementsLivreur;
    return this.elementsClient;
  });
}
