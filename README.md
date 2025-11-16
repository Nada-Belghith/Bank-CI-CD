# Bank (Spring Boot)

Projet d'exemple : application bancaire minimale construite avec Spring Boot et Thymeleaf.

**Description courte**
- Application web pour gérer des clients et comptes (CRUD minimal).
- Inclut : contrôleurs MVC, repository DAO, templates Thymeleaf, tests, Dockerfile, Jenkinsfile et plan JMeter pour tests de charge.

**Fonctionnalités**
- Listage et création de clients
- Listage et recherche de comptes
- Interface web (Thymeleaf) et endpoints JSON
- Plan JMeter pour tests de performance
- Dockerfile pour containeriser l'application

## Prérequis
- Java 17+ (ou la version indiquée dans `pom.xml`)
- Maven
- Docker (optionnel, pour image)
- JMeter (optionnel, pour exécuter `jmeter/*.jmx`)

## Exécution locale (développement)
1. Compiler et lancer avec Maven :

```powershell
mvn clean package
mvn spring-boot:run
```

2. Accéder à l'application : `http://localhost:8083/` 

## Construire l'artefact

```powershell
mvn clean package -DskipTests
```

Le jar se trouvera dans `target/`.

## Docker
1. Construire l'image :

```powershell
docker build -t bank-app:latest -f Dockerfile .
```

2. Lancer le container (ajuster la configuration de la base de données) :

```powershell
docker run -p 8083:8083 --env-file .env bank-app:latest
```

## Tests
- Tests unitaires : `mvn test`
- Plan JMeter : `jmeter/performance_test_docker.jmx` (exécuter avec JMeter GUI ou non-GUI). Exemple non-GUI :

```powershell
jmeter -n -t jmeter/performance_test_docker.jmx -l results.jtl -Jhost=localhost -Jport=8083
```

## CI / Jenkins
- Le projet contient un `Jenkinsfile` pour l'intégration continue et les tests de performance.
- Conseil : ajouter des étapes pour vérifier l'état de l'application avant d'exécuter JMeter (smoke test `curl`), et collecter `docker logs` en cas d'échec.

## Ajouter ce projet sur GitHub (suggestion de description)
- Nom du repo : `bank` ou `bank-springboot`
- Description (suggestion) : "Simple Spring Boot banking demo with Thymeleaf, Docker, Jenkins and JMeter performance tests"
- Topics (suggestions) : `spring-boot`, `thymeleaf`, `docker`, `jenkins`, `jmeter`, `demo`

## Commandes Git (PowerShell)
1. Initialiser le repo localement (si pas déjà fait) :

```powershell
git init
git add .
git commit -m "Initial commit: Bank Spring Boot project"
```

2. Créer le repo sur GitHub via l'interface web ou utiliser `gh` (GitHub CLI) :

```powershell
gh repo create <OWNER>/bank --public --description "Simple Spring Boot banking demo with Thymeleaf, Docker, Jenkins and JMeter performance tests" --source=. --remote=origin
```

3. Pousser les commits (vous avez demandé de pousser vous-même) :

```powershell
git push -u origin main
```

Remarque : adaptez `<OWNER>` et la branche (`main`/`master`) selon votre configuration.

## Contribution
- Ouvrez des issues pour bugs ou améliorations.
- Propositions : ajouter authentification, persistance robuste (MySQL/Postgres), tests d'intégration, CI plus complet.

## Licence
Ce dépôt est fourni avec la licence MIT (voir `LICENSE`).

---
Si vous voulez, je peux aussi générer un `README` en anglais ou ajouter des badges CI/Build/coverage.
