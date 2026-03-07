import { Injectable, signal, computed, WritableSignal, Signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { Utilisateur } from '../models';
import { RestaurantService } from './restaurant.service';

/**
 * ============================================================================
 * SERVICE AUTHENTIFICATION - AuthService
 * ============================================================================
 * 
 * COURS INF1013 - SIGNALS
 * -----------------------
 * "Un signal est un wrapper (fonction) autour d'une valeur qui notifie
 * les observateurs concernés lorsque cette valeur change."
 * 
 * "Les signaux peuvent contenir n'importe quelle valeur, des types
 * primitifs aux structures de données complexes."
 * 
 * "La valeur d'un signal se lit en appelant sa fonction d'accès (getter),
 * ce qui permet à Angular de suivre son utilisation."
 * 
 * OBSERVABLES VS SIGNALS:
 * - Signals: état synchrone, réactif, idéal pour l'UI
 * - Observables: opérations asynchrones (HTTP), flux de données
 * 
 * @see Diapo: Signal Mutable, C'est quoi un signal
 * ============================================================================
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  
  /**
   * Signal mutable pour l'utilisateur connecté
   * 
   * COURS INF1013: "Les signaux modifiables offrent des fonctionnalités
   * permettant de mettre à jour directement leurs valeurs avec set(T)"
   */
  private readonly _utilisateurCourant: WritableSignal<Utilisateur | null> = signal<Utilisateur | null>(null);

  /**
   * Signal immutable exposé pour lecture
   * 
   * COURS INF1013: "On peut générer un signal immutable à partir d'un
   * signal mutable à l'aide de asReadonly()"
   */
  readonly utilisateurCourant: Signal<Utilisateur | null> = this._utilisateurCourant.asReadonly();

  /**
   * Signal calculé pour vérifier si l'utilisateur est connecté
   * 
   * COURS INF1013: "Un signal calculé est un cas particulier de signal Immutable.
   * Quand le signal source change, sa dérivée (computed) est recalculée."
   */
  readonly estConnecteSignal: Signal<boolean> = computed(() => this._utilisateurCourant() !== null);

  /**
   * Signal calculé pour le rôle de l'utilisateur
   */
  readonly roleUtilisateur: Signal<string | null> = computed(() => {
    const user = this._utilisateurCourant();
    return user ? user.role : null;
  });

  // Liste des utilisateurs mock
  private utilisateurs: Utilisateur[] = [];

  private readonly storageKeyUsers = 'INF1013_users';

  constructor(
    private http: HttpClient,
    private readonly restaurantService: RestaurantService
  ) {
    // Charger les utilisateurs mock
    this.chargerUtilisateurs();
    // Vérifier si un utilisateur est déjà connecté (localStorage)
    this.verifierUtilisateurStocke();
  }

  /**
   * Charge les utilisateurs depuis le fichier mock
   */
  private chargerUtilisateurs(): void {
    // 1) Priorité au localStorage (permet de conserver les inscriptions)
    const cache = this.lireUtilisateursDepuisStorage();
    if (cache.length) {
      this.utilisateurs = cache;
      return;
    }

    // 2) Sinon charger depuis le JSON et persister
    this.http.get<any[]>('assets/mock/users.json').subscribe(users => {
      this.utilisateurs = (users ?? []).map(u => ({
        id: u.id,
        courriel: u.email,
        motDePasse: u.password,
        prenom: u.firstName,
        nom: u.lastName,
        telephone: u.phone,
        adresse: u.address,
        role: u.role,
        favoris: u.favorites || []
      }));
      this.ecrireUtilisateursDansStorage(this.utilisateurs);
    });
  }

  /**
   * Vérifie si un utilisateur est stocké dans le localStorage
   * 
   * COURS INF1013: "this.count.set(3)" - utilise set() pour définir la valeur
   */
  private verifierUtilisateurStocke(): void {
    const stocke = localStorage.getItem('utilisateurCourant');
    if (stocke) {
      this._utilisateurCourant.set(JSON.parse(stocke));
    }
  }

  /**
   * Connexion d'un utilisateur
   * @param courriel - Email de l'utilisateur
   * @param motDePasse - Mot de passe
   * @returns Observable avec l'utilisateur ou erreur
   */
  connexion(courriel: string, motDePasse: string): Observable<Utilisateur | null> {
    // Si les utilisateurs ne sont pas encore chargés, les charger d'abord
    if (this.utilisateurs.length === 0) {
      // Essayer localStorage en premier
      const cache = this.lireUtilisateursDepuisStorage();
      if (cache.length) {
        this.utilisateurs = cache;
        return this.trouverEtConnecterUtilisateur(courriel, motDePasse);
      }

      return this.http.get<any[]>('assets/mock/users.json').pipe(
        switchMap(users => {
          this.utilisateurs = users.map(u => ({
            id: u.id,
            courriel: u.email,
            motDePasse: u.password,
            prenom: u.firstName,
            nom: u.lastName,
            telephone: u.phone,
            adresse: u.address,
            role: u.role,
            favoris: u.favorites || []
          }));
          this.ecrireUtilisateursDansStorage(this.utilisateurs);
          return this.trouverEtConnecterUtilisateur(courriel, motDePasse);
        })
      );
    }
    return this.trouverEtConnecterUtilisateur(courriel, motDePasse);
  }

  // Alias pour compatibilité
  login(email: string, password: string): Observable<Utilisateur | null> {
    return this.connexion(email, password);
  }

  /**
   * Cherche et connecte l'utilisateur
   * 
   * COURS INF1013: Utilise set() pour mettre à jour le signal
   */
  private trouverEtConnecterUtilisateur(courriel: string, motDePasse: string): Observable<Utilisateur | null> {
    // Rechercher l'utilisateur dans les données mock
    const utilisateur = this.utilisateurs.find(u => u.courriel === courriel && u.motDePasse === motDePasse);
    
    if (utilisateur) {
      // Stocker l'utilisateur connecté
      localStorage.setItem('utilisateurCourant', JSON.stringify(utilisateur));
      this._utilisateurCourant.set(utilisateur);
      return of(utilisateur);
    } else {
      return of(null);
    }
  }

  /**
   * Inscription d'un nouvel utilisateur
   * @param donnees - Données de l'utilisateur
   * @returns Observable avec le nouvel utilisateur
   */
  inscription(donnees: Partial<Utilisateur>): Observable<Utilisateur> {
    // Créer un nouvel utilisateur avec un ID unique
    const nouvelUtilisateur: Utilisateur = {
      id: this.genererIdUnique(),
      prenom: donnees.prenom || '',
      nom: donnees.nom || '',
      courriel: donnees.courriel || '',
      motDePasse: donnees.motDePasse || '',
      telephone: donnees.telephone || '',
      adresse: donnees.adresse || '',
      role: donnees.role || 'client',
      favoris: []
    };

    // Ajouter à la liste locale (en vrai, ça irait au serveur)
    this.utilisateurs.push(nouvelUtilisateur);
    this.ecrireUtilisateursDansStorage(this.utilisateurs);
    
    // Connecter automatiquement après inscription
    // COURS INF1013: Utilise set() pour définir la valeur du signal
    localStorage.setItem('utilisateurCourant', JSON.stringify(nouvelUtilisateur));
    this._utilisateurCourant.set(nouvelUtilisateur);

    // Si c'est un restaurateur: créer automatiquement un restaurant associé
    if (nouvelUtilisateur.role === 'restaurateur') {
      // Charger d'abord la liste (cache/storage), puis ajouter
      this.restaurantService.loadRestaurants().subscribe(() => {
        this.restaurantService.creerRestaurantPourProprietaire({
          idProprietaire: nouvelUtilisateur.id,
          courriel: nouvelUtilisateur.courriel,
          telephone: nouvelUtilisateur.telephone,
          adresse: nouvelUtilisateur.adresse,
          nom: `Restaurant de ${nouvelUtilisateur.prenom}`,
        }).subscribe();
      });
    }
    
    return of(nouvelUtilisateur);
  }

  private lireUtilisateursDepuisStorage(): Utilisateur[] {
    try {
      const raw = localStorage.getItem(this.storageKeyUsers);
      if (!raw) return [];
      const parsed = JSON.parse(raw);
      if (!Array.isArray(parsed)) return [];
      return parsed as Utilisateur[];
    } catch {
      return [];
    }
  }

  private ecrireUtilisateursDansStorage(liste: Utilisateur[]): void {
    try {
      localStorage.setItem(this.storageKeyUsers, JSON.stringify(liste));
    } catch {
      // ignore
    }
  }

  // Alias pour compatibilité
  register(userData: any): Observable<Utilisateur> {
    return this.inscription({
      prenom: userData.firstName,
      nom: userData.lastName,
      courriel: userData.email,
      motDePasse: userData.password,
      telephone: userData.phone,
      adresse: userData.address,
      role: userData.role
    });
  }

  /**
   * Déconnexion
   * 
   * COURS INF1013: "this.count.set(3)" - utilise set() pour la déconnexion
   */
  deconnexion(): void {
    localStorage.removeItem('utilisateurCourant');
    this._utilisateurCourant.set(null);
  }

  // Alias pour compatibilité
  logout(): void {
    this.deconnexion();
  }

  /**
   * Vérifie si l'utilisateur est connecté
   * 
   * COURS INF1013: "La valeur d'un signal se lit en appelant sa fonction d'accès"
   */
  estConnecte(): boolean {
    return this._utilisateurCourant() !== null;
  }

  // Alias pour compatibilité
  isLoggedIn(): boolean {
    return this.estConnecte();
  }

  /**
   * Retourne l'utilisateur connecté
   * 
   * COURS INF1013: Lecture du signal avec ()
   */
  getUtilisateurCourant(): Utilisateur | null {
    return this._utilisateurCourant();
  }

  // Alias pour compatibilité
  getCurrentUser(): Utilisateur | null {
    return this.getUtilisateurCourant();
  }

  /**
   * Vérifie si le courriel existe déjà
   */
  courrielExiste(courriel: string): boolean {
    return this.utilisateurs.some(u => u.courriel === courriel);
  }

  // Alias pour compatibilité
  emailExists(email: string): boolean {
    return this.courrielExiste(email);
  }

  /**
   * Génère un ID unique
   */
  private genererIdUnique(): number {
    return Math.max(...this.utilisateurs.map(u => u.id), 0) + 1;
  }

  /**
   * Ajoute un restaurant aux favoris
   * @param idRestaurant - ID du restaurant
   * 
   * COURS INF1013: "this.count.update(oldValue => oldValue + 4)"
   * Utilise update() pour transformer l'état existant
   */
  ajouterAuxFavoris(idRestaurant: number): void {
    this._utilisateurCourant.update(utilisateur => {
      if (utilisateur && !utilisateur.favoris.includes(idRestaurant)) {
        const maj = { ...utilisateur, favoris: [...utilisateur.favoris, idRestaurant] };
        localStorage.setItem('utilisateurCourant', JSON.stringify(maj));
        return maj;
      }
      return utilisateur;
    });
  }

  // Alias pour compatibilité
  addToFavorites(restaurantId: number): void {
    this.ajouterAuxFavoris(restaurantId);
  }

  /**
   * Retire un restaurant des favoris
   * @param idRestaurant - ID du restaurant
   * 
   * COURS INF1013: Utilise update() pour modifier l'état
   */
  retirerDesFavoris(idRestaurant: number): void {
    this._utilisateurCourant.update(utilisateur => {
      if (utilisateur) {
        const maj = { ...utilisateur, favoris: utilisateur.favoris.filter(id => id !== idRestaurant) };
        localStorage.setItem('utilisateurCourant', JSON.stringify(maj));
        return maj;
      }
      return utilisateur;
    });
  }

  // Alias pour compatibilité
  removeFromFavorites(restaurantId: number): void {
    this.retirerDesFavoris(restaurantId);
  }

  /**
   * Vérifie si un restaurant est dans les favoris
   * @param idRestaurant - ID du restaurant
   * 
   * COURS INF1013: Lecture du signal avec ()
   */
  estFavori(idRestaurant: number): boolean {
    const utilisateur = this._utilisateurCourant();
    return utilisateur ? utilisateur.favoris.includes(idRestaurant) : false;
  }

  // Alias pour compatibilité
  isFavorite(restaurantId: number): boolean {
    return this.estFavori(restaurantId);
  }

  /**
   * Met à jour les informations du profil de l'utilisateur connecté
   * @param donnees - Données à mettre à jour
   * 
   * COURS INF1013: Utilise update() pour modifier partiellement l'état
   */
  mettreAJourProfil(donnees: Partial<Utilisateur>): void {
    this._utilisateurCourant.update(utilisateur => {
      if (utilisateur) {
        const maj = {
          ...utilisateur,
          prenom: donnees.prenom !== undefined ? donnees.prenom : utilisateur.prenom,
          nom: donnees.nom !== undefined ? donnees.nom : utilisateur.nom,
          telephone: donnees.telephone !== undefined ? donnees.telephone : utilisateur.telephone,
          adresse: donnees.adresse !== undefined ? donnees.adresse : utilisateur.adresse
        };
        localStorage.setItem('utilisateurCourant', JSON.stringify(maj));
        return maj;
      }
      return utilisateur;
    });
  }

  // Alias pour compatibilité
  updateProfile(data: Partial<Utilisateur>): void {
    this.mettreAJourProfil(data);
  }
}
