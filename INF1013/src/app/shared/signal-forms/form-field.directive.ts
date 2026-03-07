import {
  Directive,
  ElementRef,
  HostListener,
  Input,
  OnInit,
} from '@angular/core';

import type { FieldState } from './signal-forms';

/**
 * Directive [formField]
 *
 * Permet de binder un <input> / <textarea> à un FieldState (Signal Forms pattern).
 * - Synchronise la valeur UI -> model signal
 * - Met à jour l'input quand le modèle change
 * - Marque touched au blur
 */
@Directive({
  selector: '[formField]',
  standalone: true,
})
export class FormFieldDirective implements OnInit {
  @Input('formField') field!: () => FieldState<any>;

  constructor(private readonly el: ElementRef<HTMLInputElement | HTMLTextAreaElement>) {}

  ngOnInit(): void {
    // Init input value depuis le model
    const current = this.field().value();
    this.el.nativeElement.value = current ?? '';
  }

  @HostListener('input', ['$event'])
  onInput(_evt: Event): void {
    const value = this.el.nativeElement.value;
    this.field().setValue(value);
  }

  @HostListener('blur')
  onBlur(): void {
    this.field().markTouched();
  }
}
