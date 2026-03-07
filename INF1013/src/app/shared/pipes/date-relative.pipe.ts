import { Pipe, PipeTransform } from '@angular/core';

/**
 * ============================================================================
 * PIPE PERSONNALISÉ - DateRelativePipe
 * ============================================================================
 * 
 * COURS INF1013 - PIPES PERSONNALISÉS
 * -----------------------------------
 * "Les pipes permettent de transformer des valeurs dans les templates.
 * On peut créer des pipes personnalisés pour des besoins spécifiques."
 * 
 * "Les pipes peuvent prendre des arguments:
 * {{ date | dateRelative:'court' }}"
 * 
 * UTILISATION DANS LE TEMPLATE:
 * {{ commande.date | dateRelative }}          → "Il y a 5 minutes"
 * {{ commande.date | dateRelative:'court' }}  → "5 min"
 * {{ commande.date | dateRelative:'complet' }} → "Mercredi 26 février 2026 à 14h30"
 * 
 * @see Diapo: Pipes personnalisés, PipeTransform
 * ============================================================================
 */
@Pipe({
  name: 'dateRelative',
  standalone: true
})
export class DateRelativePipe implements PipeTransform {
  
  /**
   * Transforme une date en texte relatif ou formaté
   * 
   * COURS INF1013 - TRANSFORM():
   * "La méthode transform() est la seule méthode requise par PipeTransform.
   * Elle reçoit la valeur et retourne la valeur transformée."
   * 
   * @param valeur - La date à formater (Date, string ou timestamp)
   * @param format - 'relatif' (défaut), 'court', ou 'complet'
   * @returns La date formatée selon le format demandé
   */
  transform(valeur: Date | string | number | null | undefined, format: 'relatif' | 'court' | 'complet' = 'relatif'): string {
    // Gérer les valeurs nulles
    if (!valeur) {
      return '';
    }

    // Convertir en objet Date
    const date = new Date(valeur);
    if (isNaN(date.getTime())) {
      return '';
    }

    const maintenant = new Date();
    const diffMs = maintenant.getTime() - date.getTime();
    const diffMinutes = Math.floor(diffMs / 60000);
    const diffHeures = Math.floor(diffMinutes / 60);
    const diffJours = Math.floor(diffHeures / 24);

    switch (format) {
      case 'court':
        return this.formatCourt(diffMinutes, diffHeures, diffJours);
      
      case 'complet':
        return this.formatComplet(date);
      
      case 'relatif':
      default:
        return this.formatRelatif(diffMinutes, diffHeures, diffJours);
    }
  }

  /**
   * Format court: "5 min", "2 h", "3 j"
   */
  private formatCourt(minutes: number, heures: number, jours: number): string {
    if (jours > 0) return `${jours} j`;
    if (heures > 0) return `${heures} h`;
    if (minutes > 0) return `${minutes} min`;
    return 'maintenant';
  }

  /**
   * Format relatif: "Il y a 5 minutes", "Il y a 2 heures"
   */
  private formatRelatif(minutes: number, heures: number, jours: number): string {
    if (jours > 7) return 'Il y a plus d\'une semaine';
    if (jours > 1) return `Il y a ${jours} jours`;
    if (jours === 1) return 'Hier';
    if (heures > 1) return `Il y a ${heures} heures`;
    if (heures === 1) return 'Il y a 1 heure';
    if (minutes > 1) return `Il y a ${minutes} minutes`;
    if (minutes === 1) return 'Il y a 1 minute';
    return 'À l\'instant';
  }

  /**
   * Format complet: "Mercredi 26 février 2026 à 14h30"
   */
  private formatComplet(date: Date): string {
    const jours = ['Dimanche', 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'];
    const mois = ['janvier', 'février', 'mars', 'avril', 'mai', 'juin', 
                  'juillet', 'août', 'septembre', 'octobre', 'novembre', 'décembre'];
    
    const jour = jours[date.getDay()];
    const numero = date.getDate();
    const nomMois = mois[date.getMonth()];
    const annee = date.getFullYear();
    const heures = date.getHours();
    const minutes = date.getMinutes().toString().padStart(2, '0');

    return `${jour} ${numero} ${nomMois} ${annee} à ${heures}h${minutes}`;
  }
}
