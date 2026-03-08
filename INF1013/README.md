Application de restaurant – INF1013

Ce projet a été réalisé dans le cadre du cours INF1013.
L’objectif est de développer une application web de livraison de repas en utilisant Angular pour le front-end.
Le back-end sera développé plus tard avec Spring Boot, mais pour le Jalon I, les échanges avec le serveur sont simplement simulés.

Description du projet

L’application permet de simuler une plateforme de commande de repas avec différents types d’utilisateurs :

Client : peut consulter les restaurants et leurs menus.

Restaurateur : peut gérer son restaurant et ajouter ou supprimer des plats.

Livreur : peut consulter les commandes à livrer.

Les données sont chargées à partir de fichiers JSON locaux ou de services simulés, ce qui permet de tester le fonctionnement de l’interface sans avoir de serveur réel.

Technologies utilisées

Angular

Angular Material

TypeScript

Reactive Forms

Signals

Firebase Hosting (pour le déploiement)

Installation

Ouvrir le dossier INF1013 dans l’environnement de développement.

Installer les dépendances :

npm install
Lancer l’application

Pour démarrer le serveur de développement :

npm start

ou

ng serve

Ensuite ouvrir dans le navigateur :

http://localhost:4200

L’application se met automatiquement à jour lorsque le code est modifié.

Compilation du projet

Pour générer la version de production :

ng build

Les fichiers compilés seront placés dans le dossier :

dist/
Déploiement

Le projet est déployé sur Firebase Hosting.

Commandes utilisées :

firebase login
firebase deploy

