import { Pipe, PipeTransform } from '@angular/core';

/**
 * ============================================================================
 * PIPE PERSONNALISÉ - PrixPipe
 * ============================================================================
 * 
 * COURS INF1013 - PIPES PERSONNALISÉS
 * -----------------------------------
 * "Les pipes permettent de transformer des valeurs dans les templates.
 * Angular propose des pipes intégrés mais on peut créer les nôtres."
 * 
 * "Pour créer un pipe personnalisé:
 * @Pipe({ name: 'monPipe' })
 * export class MonPipe implements PipeTransform {
 *   transform(value: any, ...args: any[]): any { ... }
 * }"
 * 
 * UTILISATION DANS LE TEMPLATE:
 * {{ plat.prix | prix }}           → "12,99 $"
 * {{ plat.prix | prix:'EUR' }}     → "12,99 €"
 * {{ plat.prix | prix:'USD':true }} → "$12.99"
 * 
 * @see Diapo: Pipes personnalisés, PipeTransform
 * ============================================================================
 */
@Pipe({
  name: 'prix',
  standalone: true
})
export class PrixPipe implements PipeTransform {
  
  /**
   * Transforme un nombre en prix formaté
   * 
   * COURS INF1013 - TRANSFORM():
   * "La méthode transform() reçoit la valeur à transformer
   * et des arguments optionnels pour personnaliser le comportement."
   * 
   * @param valeur - Le montant à formater
   * @param devise - 'CAD' (défaut), 'EUR', ou 'USD'
   * @param symboleAvant - Si true, le symbole est placé avant (style US)
   * @returns Le prix formaté selon la locale québécoise
   */
  transform(valeur: number | null | undefined, devise: 'CAD' | 'EUR' | 'USD' = 'CAD', symboleAvant: boolean = false): string {
    // Gérer les valeurs nulles ou undefined
    if (valeur === null || valeur === undefined) {
      return '0,00 $';
    }

    // Formater le nombre avec 2 décimales et virgule comme séparateur
    const nombreFormate = valeur.toFixed(2).replace('.', ',');

    // Déterminer le symbole de devise
    const symboles: Record<string, string> = {
      'CAD': '$',
      'EUR': '€',
      'USD': '$'
    };
    const symbole = symboles[devise] || '$';

    // Retourner selon le format demandé
    if (symboleAvant) {
      return `${symbole}${nombreFormate}`;
    }
    return `${nombreFormate} ${symbole}`;
  }
}
