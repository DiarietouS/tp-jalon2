import { AfterViewInit, Component, OnDestroy, ViewChild, ElementRef, computed, output, signal, Signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

// Angular Material
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

import { fromEvent, Subject, debounceTime, distinctUntilChanged, map, takeUntil } from 'rxjs';

/**
 * ============================================================================
 * COMPOSANT HEADER
 * ============================================================================
 * 
 * COURS INF1013 - SIGNALS CALCULÉS (computed)
 * -------------------------------------------
 * "Un signal calculé est un cas particulier de signal Immutable.
 * Quand le signal source change, sa dérivée (computed) est recalculée."
 * 
 * "const howMany: Signal<string> = computed(() => {
 *   return count() > 10 ? `C'est grand` : `C'est petit`
 * });"
 * 
 * COURS INF1013 - OUTPUT()
 * ------------------------
 * "Dans le composant enfant, on crée un OutputEmitterRef nommé: 
 * addItemEvent = output<string>();"
 * 
 * "output<T>() retourne un émetteur OutputEmitterRef<T>"
 * "On peut lui ajouter un alias: displayInfo = output({alias: 'info'})"
 * 
 * @see Diapo: Signal: computed, output<T>() avec emit()
 * ============================================================================
 */
@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header implements AfterViewInit, OnDestroy {
  
  /**
   * Adresse de livraison calculée depuis le profil utilisateur
   * 
   * COURS INF1013 - COMPUTED:
   * "Un signal calculé est un cas particulier de signal Immutable.
   * Quand le signal source change, sa dérivée (computed) est recalculée."
   * 
   * L'adresse est maintenant dynamique: elle provient du profil de
   * l'utilisateur connecté ou affiche une valeur par défaut.
   */
  adresseLivraison: Signal<string> = computed(() => {
    const utilisateur = this.serviceAuth.utilisateurCourant();
    return utilisateur?.adresse?.trim() || 'Trois-Rivières, QC';
  });

  /**
   * Signal local pour le texte de recherche
   * 
   * COURS INF1013: "Les signaux modifiables offrent des fonctionnalités
   * permettant de mettre à jour directement leurs valeurs avec set(T)"
   */
  texteRecherche = signal<string>('');

  /**
   * Output pour émettre la recherche vers le parent
   * 
   * COURS INF1013 - OUTPUT:
   * "Dans le composant enfant, on crée un OutputEmitterRef nommé:
   * addItemEvent = output<string>();"
   * 
   * "Pour émettre un évènement depuis l'enfant:
   * this.addItemEvent.emit(newItem)"
   */
  rechercheChange = output<string>();

  /** Référence DOM de l'input de recherche (cours: TVar / ViewChild) */
  @ViewChild('searchInput', { static: true })
  searchInput!: ElementRef<HTMLInputElement>;

  private readonly destroy$ = new Subject<void>();

  constructor(
    public serviceAuth: AuthService,
    private readonly router: Router
  ) {}

  /**
   * COURS INF1013 - Observables (fromEvent + pipe)
   * Typeahead: debounceTime + distinctUntilChanged
   */
  ngAfterViewInit(): void {
    fromEvent(this.searchInput.nativeElement, 'input')
      .pipe(
        map(() => this.searchInput.nativeElement.value),
        debounceTime(200),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe((valeur) => {
        this.texteRecherche.set(valeur);
        this.rechercheChange.emit(valeur);
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Déconnexion de l'utilisateur
   */
  deconnecter(): void {
    this.serviceAuth.deconnexion();
    this.texteRecherche.set('');
    this.rechercheChange.emit('');
    this.router.navigate(['/auth']);
  }
}
