# INF1013 - Application de Restaurants en Ligne - Jalon 2

Ce projet implémente une architecture microservices pour l'application de restaurants en ligne développée dans le cadre du cours **INF1013**.

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    APPLICATION ANGULAR                          │
│                    (localhost:4200)                             │
│   - Signals pour l'état                                        │
│   - RxJS + Observables pour HTTP                               │
│   - JWT Interceptor                                             │
└──────────────┬────────────────────┬────────────────────────────┘
               │                    │
               ▼                    ▼
┌──────────────────────┐  ┌─────────────────────────┐
│   auth-service       │  │   business-service      │
│   (Port 8081)        │  │   (Port 8082)           │
│   - Connexion        │  │   - Restaurants         │
│   - Inscription      │  │   - Plats               │
│   - JWT HMAC256      │  │   - Commandes           │
│   - BCrypt           │  │   - Livraisons          │
└──────────┬───────────┘  └──────────┬──────────────┘
           │                         │
           └───────────┬─────────────┘
                       ▼
          ┌────────────────────────┐
          │   PostgreSQL           │
          │   (Port 5432)          │
          │   - restaurants DB     │
          └────────────────────────┘
```

## Prérequis

- **Java 21** (JDK)
- **Maven 3.9+**
- **Node.js 20+** et **npm**
- **Docker** et **Docker Compose**

---

## Démarrage rapide

### 1. Démarrer PostgreSQL avec Docker

```bash
# Démarrer uniquement la base de données
docker compose up postgres -d
```

### 2. Démarrer les microservices Spring Boot

**Option A : Avec Maven (développement)**

```bash
# Terminal 1 - auth-service
cd auth-service
mvn spring-boot:run

# Terminal 2 - business-service
cd business-service
mvn spring-boot:run
```

**Option B : Avec Docker Compose**

```bash
# Construire et démarrer tous les services
docker compose --profile backend up --build
```

### 3. Démarrer l'application Angular

```bash
cd INF1013
npm install
npm start
```

Accéder à : **http://localhost:4200**

---

## Configuration de la Base de Données

| Paramètre | Valeur |
|-----------|--------|
| Hôte | localhost |
| Port | 5432 |
| Base de données | restaurants |
| Utilisateur | postgres |
| Mot de passe | choupinou |

---

## Utilisateurs par Défaut

| Courriel | Rôle | Mot de passe |
|----------|------|--------------|
| admin@restaurants.com | ADMIN | password123 |
| resto1@restaurants.com | RESTAURATEUR | password123 |
| client1@restaurants.com | CLIENT | password123 |
| livreur1@restaurants.com | LIVREUR | password123 |

---

## API REST

### Auth Service (Port 8081)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/auth/connexion` | Connexion utilisateur |
| POST | `/api/auth/inscription` | Inscription nouvel utilisateur |
| POST | `/api/auth/valider` | Valider un jeton JWT |
| GET | `/api/auth/sante` | Vérifier la santé du service |

**Exemple de connexion :**
```json
POST http://localhost:8081/api/auth/connexion
{
  "courriel": "client1@restaurants.com",
  "motDePasse": "password123"
}
```

**Réponse :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "typeToken": "Bearer",
  "utilisateur": {
    "id": 3,
    "prenom": "Marie",
    "nom": "Dupont",
    "courriel": "client1@restaurants.com",
    "role": "CLIENT"
  },
  "message": "Connexion réussie"
}
```

### Business Service (Port 8082)

| Méthode | Endpoint | Description | Auth |
|---------|----------|-------------|------|
| GET | `/api/restaurants` | Liste des restaurants | Non |
| GET | `/api/restaurants/{id}` | Détails d'un restaurant | Non |
| POST | `/api/restaurants` | Créer un restaurant | RESTAURATEUR |
| PUT | `/api/restaurants/{id}` | Modifier un restaurant | RESTAURATEUR |
| DELETE | `/api/restaurants/{id}` | Supprimer un restaurant | ADMIN |
| GET | `/api/plats/restaurant/{id}` | Plats d'un restaurant | Non |
| POST | `/api/plats` | Créer un plat | RESTAURATEUR |
| PUT | `/api/plats/{id}` | Modifier un plat | RESTAURATEUR |
| DELETE | `/api/plats/{id}` | Supprimer un plat | RESTAURATEUR |
| GET | `/api/commandes/client/{id}` | Commandes d'un client | CLIENT |
| GET | `/api/commandes/restaurant/{id}` | Commandes d'un restaurant | RESTAURATEUR |
| POST | `/api/commandes` | Créer une commande | CLIENT |
| PUT | `/api/commandes/{id}/statut` | Mettre à jour le statut | RESTAURATEUR/LIVREUR |
| GET | `/api/livraisons/en-attente` | Livraisons en attente | LIVREUR |
| POST | `/api/livraisons` | Créer une livraison | LIVREUR |
| PUT | `/api/livraisons/{id}/statut` | Mettre à jour livraison | LIVREUR |

---

## Sécurité JWT

```
COURS INF1013 - JWT HMAC256
━━━━━━━━━━━━━━━━━━━━━━━━━━

Header.Payload.Signature

Header  : {"alg": "HS256", "typ": "JWT"}
Payload : {"sub": "email", "role": "CLIENT", "iat": ..., "exp": ...}
Signature: HMAC256(base64(header) + "." + base64(payload), secret)
```

- Algorithme : **HMAC256 (HS256)**
- Expiration : **24 heures**
- Encodage des mots de passe : **BCrypt**
- L'intercepteur Angular ajoute automatiquement : `Authorization: Bearer <token>`

---

## Structure du Projet

```
tp-jalon2/
├── auth-service/          # Microservice d'authentification (Port 8081)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/restaurants/auth/
│       │   ├── config/    # SecurityConfig, JwtConfig, CorsConfig, BeansConfig
│       │   ├── controllers/  # AuthController
│       │   ├── services/  # AuthService, JwtService, UtilisateurDetailsService
│       │   ├── security/  # JwtAuthenticationFilter, JwtAuthenticationEntryPoint
│       │   ├── models/    # Utilisateur
│       │   ├── repositories/  # UtilisateurRepository
│       │   ├── dtos/      # LoginRequestDTO, RegisterRequestDTO, AuthResponseDTO, UtilisateurDTO
│       │   └── exceptions/   # AuthException
│       └── resources/
│           ├── application.yaml
│           └── db/changelog/  # Migrations Liquibase
│
├── business-service/      # Microservice métier (Port 8082)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/restaurants/business/
│       │   ├── config/    # SecurityConfig, JwtConfig, CorsConfig
│       │   ├── controllers/  # RestaurantController, PlatController, CommandeController, LivraisonController
│       │   ├── services/  # RestaurantService, PlatService, CommandeService, LivraisonService
│       │   ├── security/  # JwtAuthenticationFilter
│       │   ├── models/    # Restaurant, Plat, Commande, LigneCommande, Livraison
│       │   ├── repositories/  # Repositories JPA
│       │   ├── dtos/      # DTOs de transfert
│       │   └── exceptions/   # BusinessException
│       └── resources/
│           ├── application.yaml
│           └── db/changelog/  # Migrations Liquibase
│
├── INF1013/              # Application Angular (Port 4200)
│   └── src/app/
│       └── core/
│           ├── interceptors/  # jwt.interceptor.ts (NOUVEAU)
│           ├── services/  # auth.service.ts, restaurant.service.ts...
│           └── guards/    # auth.guard.ts, restaurateur.guard.ts, livreur.guard.ts
│
└── docker-compose.yml    # PostgreSQL + microservices
```

---

## Migrations Liquibase

Les migrations sont exécutées automatiquement au démarrage des services :

### auth-service
1. `01-create-utilisateur-table.yaml` - Création de la table `utilisateur`
2. `02-insert-default-users.yaml` - Insertion des 4 utilisateurs par défaut

### business-service
1. `01-create-restaurant-table.yaml` - Table `restaurant`
2. `02-create-plat-table.yaml` - Table `plat`
3. `03-create-commande-table.yaml` - Table `commande`
4. `04-create-ligne-commande-table.yaml` - Table `ligne_commande`
5. `05-create-livraison-table.yaml` - Table `livraison`
6. `06-insert-default-data.yaml` - Restaurants et plats par défaut

---

## Déploiement Firebase (Angular)

```bash
cd INF1013
ng build --configuration production
firebase login
firebase deploy
```

---

## Conventions du Cours INF1013

- ✅ Tous les commentaires **en français**
- ✅ Messages d'erreur **en français**
- ✅ Noms de variables **en français** (courriel, motDePasse, prenom, nom...)
- ✅ Architecture **Controller → Service → Repository**
- ✅ **DTOs** séparés des **Models JPA**
- ✅ **JWT HMAC256** avec expiration 24h
- ✅ **Liquibase YAML** pour les migrations
- ✅ **CORS** configuré pour localhost:4200
- ✅ **Spring Security** avec rôles
- ✅ **Signals Angular** pour l'état
- ✅ **RxJS + Observables** pour les appels HTTP
- ✅ **Intercepteurs HTTP** pour JWT
