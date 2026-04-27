import { Injectable, signal, computed, WritableSignal, Signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { Utilisateur } from '../models';
import { RestaurantService } from './restaurant.service';

/** Réponse renvoyée par auth-service lors de /signin ou /signup */
interface AuthBackendResponse {
  token: string;
  id: number;
  courriel: string;
  prenom: string;
  nom: string;
  role: string;
}

/** Réponse de /mot-de-passe-oublie */
export interface MotDePasseOubliResponse {
  message: string;
  jeton: string | null;   // null si courriel inconnu (sécurité)
  expireLe: string | null;
}

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

  // URL de base du backend auth-service (COURS INF1013: HttpClient)
  private readonly AUTH_BASE_URL = 'http://localhost:8081/api/auth';

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
    this.http.get<any[]>('assets/mock/users.json').pipe(
      catchError(() => of([]))
    ).subscribe(users => {
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
      const utilisateur = JSON.parse(stocke) as Utilisateur;
      this._utilisateurCourant.set(utilisateur);
      this.synchroniserUtilisateurDansStorage(utilisateur);
    }
  }

  /**
   * Connexion d'un utilisateur.
   * COURS INF1013 (HttpClient): essaie d'abord le backend auth-service,
   * avec repli automatique sur les données mock via catchError.
   */
  connexion(courriel: string, motDePasse: string): Observable<Utilisateur | null> {
    return this.http.post<AuthBackendResponse>(`${this.AUTH_BASE_URL}/signin`, { courriel, motDePasse }).pipe(
      tap(response => localStorage.setItem('jwtToken', response.token)),
      map(response => {
        const base = this.utilisateurDepuisReponseAuth(response);
        const local = this.lireUtilisateursDepuisStorage().find(u => u.courriel === base.courriel);
        return { ...base, telephone: local?.telephone || '', adresse: local?.adresse || '', favoris: local?.favoris || [] };
      }),
      tap(utilisateur => this.persistUtilisateurCourant(utilisateur)),
      catchError(() => this.connexionMock(courriel, motDePasse))
    );
  }

  /** Repli mock si le backend est indisponible */
  private connexionMock(courriel: string, motDePasse: string): Observable<Utilisateur | null> {
    if (this.utilisateurs.length === 0) {
      const cache = this.lireUtilisateursDepuisStorage();
      if (cache.length) {
        this.utilisateurs = cache;
        return this.trouverEtConnecterUtilisateur(courriel, motDePasse);
      }
      return this.http.get<any[]>('assets/mock/users.json').pipe(
        catchError(() => of([])),
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
      this.persistUtilisateurCourant(utilisateur);
      return of(utilisateur);
    } else {
      return of(null);
    }
  }

  /**
   * Inscription d'un nouvel utilisateur.
   * COURS INF1013 (HttpClient): essaie d'abord POST /api/auth/signup,
   * avec repli automatique vers l'inscription mock.
   */
  inscription(donnees: Partial<Utilisateur>): Observable<Utilisateur> {
    return this.http.post<AuthBackendResponse>(`${this.AUTH_BASE_URL}/signup`, {
      prenom: donnees.prenom || '',
      nom: donnees.nom || '',
      courriel: donnees.courriel || '',
      motDePasse: donnees.motDePasse || '',
      telephone: donnees.telephone || '',
      adresse: donnees.adresse || '',
      role: donnees.role || 'client'
    }).pipe(
      tap(response => localStorage.setItem('jwtToken', response.token)),
      map(response => {
        const base = this.utilisateurDepuisReponseAuth(response);
        // Préserver TOUS les champs du formulaire (le backend ne renvoie pas motDePasse/telephone/adresse)
        return {
          ...base,
          motDePasse: donnees.motDePasse || '',
          telephone: donnees.telephone || '',
          adresse: donnees.adresse || '',
          role: (donnees.role as Utilisateur['role']) || base.role
        };
      }),
      tap(utilisateur => this.persistUtilisateurCourant(utilisateur)),
      switchMap(utilisateur => {
        if (utilisateur.role === 'restaurateur') {
          return this.restaurantService.creerRestaurantPourProprietaire({
            idProprietaire: utilisateur.id,
            courriel: utilisateur.courriel,
            telephone: utilisateur.telephone,
            adresse: utilisateur.adresse,
            nom: `Restaurant de ${utilisateur.prenom}`,
          }).pipe(map(() => utilisateur));
        }
        return of(utilisateur);
      }),
      catchError(() => this.inscriptionMock(donnees))
    );
  }

  /** Repli mock si le backend est indisponible */
  private inscriptionMock(donnees: Partial<Utilisateur>): Observable<Utilisateur> {
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
    this.utilisateurs.push(nouvelUtilisateur);
    this.ecrireUtilisateursDansStorage(this.utilisateurs);
    this.persistUtilisateurCourant(nouvelUtilisateur);
    if (nouvelUtilisateur.role === 'restaurateur') {
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

  /** Construit un Utilisateur depuis la réponse du backend auth-service */
  private utilisateurDepuisReponseAuth(response: AuthBackendResponse): Utilisateur {
    return {
      id: response.id,
      courriel: response.courriel,
      motDePasse: '',
      prenom: response.prenom,
      nom: response.nom,
      telephone: '',
      adresse: '',
      role: response.role as Utilisateur['role'],
      favoris: []
    };
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
   * Demande un token de réinitialisation de mot de passe.
   * COURS INF1013 (HttpClient): POST vers auth-service,
   * retourne le token (dev) ou un message générique (courriel inconnu).
   */
  motDePasseOubli(courriel: string): Observable<MotDePasseOubliResponse> {
    return this.http.post<MotDePasseOubliResponse>(
      `${this.AUTH_BASE_URL}/mot-de-passe-oublie`,
      { courriel }
    );
  }

  /**
   * Réinitialise le mot de passe avec le token reçu.
   * COURS INF1013 (HttpClient): POST avec {jeton, nouveauMotDePasse}.
   */
  reinitialiserMotDePasse(jeton: string, nouveauMotDePasse: string): Observable<void> {
    return this.http.post<void>(
      `${this.AUTH_BASE_URL}/reinitialiser-mot-de-passe`,
      { jeton, nouveauMotDePasse }
    );
  }

  /**
   * Déconnexion
   * 
   * COURS INF1013: "this.count.set(3)" - utilise set() pour la déconnexion
   */
  deconnexion(): void {
    localStorage.removeItem('utilisateurCourant');
    localStorage.removeItem('jwtToken');
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
    const emailRecherche = courriel.trim().toLowerCase();
    const source = this.utilisateurs.length ? this.utilisateurs : this.lireUtilisateursDepuisStorage();
    return source.some(u => u.courriel.trim().toLowerCase() === emailRecherche);
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
        this.synchroniserUtilisateurDansStorage(maj);
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
        this.synchroniserUtilisateurDansStorage(maj);
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
        this.synchroniserUtilisateurDansStorage(maj);
        return maj;
      }
      return utilisateur;
    });
  }

  // Alias pour compatibilité
  updateProfile(data: Partial<Utilisateur>): void {
    this.mettreAJourProfil(data);
  }

  private persistUtilisateurCourant(utilisateur: Utilisateur): void {
    this.synchroniserUtilisateurDansStorage(utilisateur);
    this._utilisateurCourant.set(utilisateur);
  }

  private synchroniserUtilisateurDansStorage(utilisateur: Utilisateur): void {
    localStorage.setItem('utilisateurCourant', JSON.stringify(utilisateur));

    const liste = this.lireUtilisateursDepuisStorage();
    const index = liste.findIndex(u => u.id === utilisateur.id || u.courriel === utilisateur.courriel);

    if (index === -1) {
      liste.push(utilisateur);
    } else {
      liste[index] = { ...liste[index], ...utilisateur };
    }

    this.ecrireUtilisateursDansStorage(liste);
    this.utilisateurs = liste;
  }
}
