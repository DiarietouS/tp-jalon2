import { Component, model, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

/**
 * ============================================================================
 * COMPOSANT SÉLECTEUR DE QUANTITÉ
 * ============================================================================
 * 
 * COURS INF1013 - MODEL<T>() - TWO-WAY BINDING MODERNE
 * ----------------------------------------------------
 * "model<T>() permet le two-way binding entre parent et enfant.
 * C'est une alternative moderne à l'ancien pattern @Input/@Output
 * avec [(ngModel)]."
 * 
 * "Déclaration: quantity = model<number>(1);
 * Dans le template parent: <selecteur-quantite [(quantity)]='articleQte'/>"
 * 
 * "model() crée un signal MUTABLE qui peut être:
 * - Lu par le parent et l'enfant
 * - Modifié par le parent et l'enfant (propagation automatique)"
 * 
 * AVANTAGES PAR RAPPORT À @Input/@Output:
 * - Syntaxe simplifiée: [(prop)] au lieu de [prop] + (propChange)
 * - Propagation bidirectionnelle automatique
 * - Compatible avec les signals
 * 
 * @see Diapo: model<T>(), Two-way binding avec signals
 * ============================================================================
 */
@Component({
  selector: 'app-quantite-selecteur',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule],
  template: `
    <!-- 
      COURS INF1013 - MODEL<T>():
      "La valeur du model() se lit comme un signal: quantite()"
    -->
    <div class="quantity-selector">
      <button 
        mat-icon-button 
        (click)="decrementer()"
        [disabled]="quantite() <= min()">
        <mat-icon>remove</mat-icon>
      </button>
      
      <span class="quantity-value">{{ quantite() }}</span>
      
      <button 
        mat-icon-button 
        (click)="incrementer()"
        [disabled]="quantite() >= max()">
        <mat-icon>add</mat-icon>
      </button>
    </div>
  `,
  styles: [`
    .quantity-selector {
      display: flex;
      align-items: center;
      gap: 8px;
      background: #f5f5f5;
      border-radius: 24px;
      padding: 4px;
    }
    .quantity-value {
      min-width: 24px;
      text-align: center;
      font-weight: 600;
      font-size: 16px;
    }
    button {
      width: 32px !important;
      height: 32px !important;
    }
    button[disabled] {
      opacity: 0.5;
    }
  `]
})
export class QuantiteSelecteur {
  
  /**
   * Quantité avec two-way binding
   * 
   * COURS INF1013 - MODEL<T>():
   * "model<T>() permet le two-way binding entre parent et enfant.
   * La valeur peut être modifiée des deux côtés."
   * 
   * Usage parent: <app-quantite-selecteur [(quantite)]="article.quantite" />
   */
  quantite = model<number>(1);

  /**
   * Valeur minimum (signal d'entrée avec défaut)
   */
  min = model<number>(1);

  /**
   * Valeur maximum (signal d'entrée avec défaut)
   */
  max = model<number>(99);

  /**
   * Décrémente la quantité
   * 
   * COURS INF1013 - UPDATE():
   * "model.update() permet de modifier la valeur basée sur la valeur actuelle"
   */
  decrementer(): void {
    if (this.quantite() > this.min()) {
      this.quantite.update(q => q - 1);
    }
  }

  /**
   * Incrémente la quantité
   * 
   * COURS INF1013 - SET() vs UPDATE():
   * "set() remplace la valeur, update() la transforme"
   */
  incrementer(): void {
    if (this.quantite() < this.max()) {
      this.quantite.update(q => q + 1);
    }
  }
}
