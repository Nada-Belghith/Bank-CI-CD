# 🏦 Bank-App — Pipeline CI/CD

Projet démo d'une application bancaire (Spring Boot + Thymeleaf) conçue pour être **buildée, déployée et testée automatiquement** via un pipeline Jenkins.

L'objectif principal n'est pas l'application elle-même, mais de démontrer un cycle CI/CD complet intégrant Maven, Docker et JMeter.

**Ce que fait l'application :** C'est une application web simple qui permet de gérer des clients et leurs comptes (opérations CRUD - Créer, Lire, Mettre à jour, Supprimer). Elle utilise Spring Boot pour le backend et Thymeleaf pour afficher les pages web.

## 🚀 L'objectif : Le Pipeline CI/CD

Ce projet est conçu pour être lancé via le `Jenkinsfile` inclus. C'est lui qui orchestre l'ensemble du cycle de vie de l'application.

Lorsque vous lancez le pipeline Jenkins, voici ce qu'il fait **automatiquement** :

1.  **Checkout :** Récupère le code source depuis Git.
2.  **Build (Maven) :** Compile le code et crée le fichier `.jar` exécutable (`mvn clean package`).
3.  **Build (Docker) :** Construit l'image Docker de l'application (`docker build`).
4.  **Deploy (Docker) :**
    * Crée un réseau Docker dédié.
    * Démarre un conteneur MySQL sur ce réseau.
    * Démarre le conteneur de l'application Spring Boot, connecté à la base de données.
5.  **Test (JMeter) :**
    * Démarre un troisième conteneur (JMeter).
    * Exécute un test de performance (plan `.jmx`) contre l'application déployée.
    * Génère un rapport de performance.
6.  **Cleanup :** Arrête et supprime tous les conteneurs et le réseau pour laisser l'environnement propre.

---

## ▶️ Lancement

## Lancement Automatisé :

C'est la méthode prévue pour ce projet.

1.  Assurez-vous que votre instance Jenkins a accès à **JDK**, **Maven** et **Docker**.
2.  Créez un nouveau "Pipeline Job" dans Jenkins.
3.  Configurez le pipeline pour utiliser le `Jenkinsfile` présent dans ce dépôt Git.
4.  Lancez le build.

Le pipeline s'occupe de tout, du build au test de performance.
