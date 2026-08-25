# Gestion de Stock de Pharmacie — Projet CI/CD avec Jenkins

Application Java/Spring Boot (CRUD + alertes stock faible / péremption) servant de
support à un pipeline d'intégration continue complet avec Jenkins, en réutilisant
et en approfondissant tout ce qui a été vu dans les TP0 et TP1 (installation,
plugins, sécurité par rôles, pipeline déclaratif, SonarQube, Nexus).

## 1. Structure du projet

```
GestionStockPharmacie/
├── pom.xml                          # build Maven + Jacoco + Sonar + Nexus
├── Jenkinsfile                      # pipeline déclaratif
├── src/main/java/com/pharmacie/stock/
│   ├── model/Medicament.java
│   ├── repository/MedicamentRepository.java
│   ├── service/MedicamentService(Impl).java   # logique métier (stock, alertes)
│   ├── controller/MedicamentController.java   # API REST
│   └── exception/                             # exceptions métier
├── src/main/resources/application.properties
└── src/test/java/.../MedicamentServiceImplTest.java   # tests JUnit + Mockito
```

## 2. Installer Jenkins et les outils (≈ Parties 1 à 4 du TP0)

1. Téléchargez et lancez Jenkins (`java -jar jenkins.war`), comme en Partie 1-2 du TP0.
2. Accédez à `http://localhost:8080`, débloquez l'instance.
3. **Administrer Jenkins > Gestion des plugins > Disponibles**, installez au minimum :
   - `Git plugin`, `Maven Integration`, `Pipeline`
   - `SonarQube Scanner for Jenkins`
   - `Role-based Authorization Strategy` (comme au TP3)
   - `Email Extension Plugin` (comme en fin de TP1)
4. **Administrer Jenkins > Configuration globale des outils** (équivalent de
   « Configurer le système » au TP0 Partie 4) :
   - Ajoutez un **JDK** nommé `JDK17` (décochez *Install automatically* et
     pointez vers votre JDK installé, ou laissez l'installation automatique).
   - Ajoutez un **Maven** nommé `Maven3`.
5. **Administrer Jenkins > System > SonarQube servers** : ajoutez un serveur
   nommé `SonarServer` avec l'URL de votre instance SonarQube (ex.
   `http://localhost:9000`) et un token d'authentification.
6. Dans SonarQube, activez le **Webhook** vers
   `http://localhost:8080/sonarqube-webhook/` pour que le Quality Gate
   remonte correctement dans Jenkins.
7. Vérifiez que **Nexus** tourne (`http://localhost:8081`) et que les dépôts
   `Releases` / `Snapshots` existent (comme illustré au TP4).
   Ajoutez les identifiants Nexus dans `~/.m2/settings.xml` :

```xml
<settings>
  <servers>
    <server>
      <id>nexus-releases</id>
      <username>admin</username>
      <password>VOTRE_MOT_DE_PASSE</password>
    </server>
    <server>
      <id>nexus-snapshots</id>
      <username>admin</username>
      <password>VOTRE_MOT_DE_PASSE</password>
    </server>
  </servers>
</settings>
```

## 3. Créer le dépôt Git

Poussez ce dossier sur un dépôt Git (GitHub/GitLab), puis mettez à jour
l'URL dans le `Jenkinsfile` (stage `Checkout`) et éventuellement dans le
champ *GitHub project* du job (comme au TP4).

```bash
cd GestionStockPharmacie
git init
git add .
git commit -m "Initial commit - Gestion Stock Pharmacie"
git remote add origin <URL_DE_VOTRE_DEPOT>
git push -u origin main
```

## 4. Créer le job Pipeline (≈ TP4 « Jenkins Pipeline »)

1. **Nouveau Item > Pipeline**, nommez-le `GestionStockPharmacie`.
2. Onglet **Pipeline** : `Definition = Pipeline script from SCM`, SCM = `Git`,
   renseignez l'URL de votre dépôt et `Script Path = Jenkinsfile`.
   (Sur Windows, remplacez les commandes `sh '...'` par `bat '...'` dans le
   Jenkinsfile, comme dans les captures du TP1.)
3. Cochez **Ce build a des paramètres** si vous voulez retrouver le
   comportement du TP4 (paramètre `ENVIRONNEMENT`, déjà défini dans le
   Jenkinsfile via `parameters { choice(...) }` — rien à ajouter côté UI).
4. Sauvegardez puis **Lancer un build**.

Le pipeline exécute, dans l'ordre :
`Checkout → Build → Tests unitaires (+ rapport JUnit) → Analyse SonarQube
→ Quality Gate → Package (+ archivage du .jar) → Déploiement Nexus`,
puis envoie un e-mail selon le statut (`success` / `unstable` / `failure`).

## 5. Sécuriser Jenkins avec des rôles (≈ TP3)

1. **Administrer Jenkins > Configurer la sécurité globale** : cochez
   `Stratégie basée sur les rôles` (nécessite le plugin installé à l'étape 2).
2. **Administrer Jenkins > Gérer et assigner les rôles > Gérer les rôles** :
   créez par exemple :
   - `admin` : tous les droits
   - `developpeur` : lecture, build, configuration sur les jobs préfixés
     `GestionStock*`
   - `testeur` : lecture + lancement de build uniquement
3. **Assigner les rôles** : créez les utilisateurs (`Administrer Jenkins >
   Gérer les utilisateurs > Créer un utilisateur`) puis associez-les aux
   rôles définis, exactement comme dans les captures du TP3.

## 6. (Optionnel) Déclenchement à distance (≈ TP2)

Dans la configuration du job, cochez **Déclencher les builds à distance**,
définissez un jeton, puis déclenchez le build via :

```
http://localhost:8080/job/GestionStockPharmacie/build?token=VOTRE_TOKEN
```

## 7. Lancer le projet en local (sans Jenkins, pour vérifier)

```bash
mvn spring-boot:run
```

API disponible sur `http://localhost:8080/api/medicaments`
(H2 console sur `http://localhost:8080/h2-console`).

## 8. Endpoints principaux

| Méthode | URL | Description |
|---|---|---|
| POST | `/api/medicaments` | Ajouter un médicament |
| GET | `/api/medicaments` | Lister / rechercher (`?nom=`) |
| GET | `/api/medicaments/{id}` | Détail |
| PUT | `/api/medicaments/{id}` | Modifier |
| DELETE | `/api/medicaments/{id}` | Supprimer |
| POST | `/api/medicaments/{id}/entree?quantite=` | Entrée de stock |
| POST | `/api/medicaments/{id}/sortie?quantite=` | Sortie de stock |
| GET | `/api/medicaments/alertes/stock-faible` | Médicaments sous le seuil |
| GET | `/api/medicaments/alertes/expiration?jours=30` | Péremption proche |
