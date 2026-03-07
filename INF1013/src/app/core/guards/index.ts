/**
 * ============================================================================
 * EXPORTS DES GUARDS
 * ============================================================================
 * 
 * COURS INF1013 - BARREL EXPORTS:
 * "Les fichiers index.ts permettent de regrouper les exports
 * pour simplifier les imports dans le reste de l'application."
 * 
 * import { authGuard, restaurateurGuard } from './core/guards';
 * ============================================================================
 */
export { authGuard } from './auth.guard';
export { restaurateurGuard } from './restaurateur.guard';
export { livreurGuard } from './livreur.guard';
