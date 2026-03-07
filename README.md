# Projet POO Java — Cahier de Texte Numérique

Application JavaFX de gestion du cahier de texte avec rôles (Chef de département, Responsable de classe, Enseignant), validation des séances, notifications email et génération PDF.

## 1) Fonctionnalités

### Authentification
- Connexion par rôle
- Contrôle des accès selon le profil utilisateur

### Chef de département
- Valider les comptes en attente
- Ajouter enseignant / responsable
- Assigner un cours à un enseignant
- Générer une fiche de suivi PDF (iText)
- Consulter les statistiques globales

### Enseignant
- Voir la liste de ses cours
- Ajouter une séance (date, heure, durée, contenu, observations)
- Modifier/supprimer une séance avant validation
- Consulter son suivi pédagogique
- Générer une fiche de suivi PDF

### Responsable de classe
- Consulter le cahier de texte de sa classe
- Valider / rejeter une séance
- Ajouter un motif de rejet
- Consulter l'historique
- Consulter les statistiques d'avancement

### Notifications email
- Enseignant notifié lors de l'assignation d'un cours
- Responsable notifié lorsqu'une séance est soumise
- Enseignant notifié lors d'un rejet

## 2) Stack technique
- Java 17
- JavaFX 21
- Maven
- MySQL / SQLite (selon configuration)
- iText 8 (kernel + layout)
- JUnit 5
- Jakarta Mail

## 3) Prérequis
- JDK 17+
- Maven 3.8+
- Base de données disponible

## 4) Lancer le projet

```bash
mvn clean javafx:run
```

## 5) Compiler

```bash
mvn clean compile
```

## 6) Lancer les tests

```bash
mvn test
```

## 7) Configuration email (important)
Renseigner ces variables d'environnement **ou** `application.properties`:
- `MAIL_FROM`
- `MAIL_PASSWORD`
- `MAIL_SMTP_HOST` (optionnel, défaut: smtp.gmail.com)
- `MAIL_SMTP_PORT` (optionnel, défaut: 587)

Sans configuration SMTP, l'application continue de fonctionner mais n'envoie pas d'email.

## 8) Arborescence utile
- `src/main/java/.../controller` : contrôleurs JavaFX
- `src/main/java/.../service` : logique métier
- `src/main/java/.../dao` : accès base de données
- `src/main/resources/fxml` : vues
- `src/test/java` : tests unitaires

## 9) Livrables
- Application fonctionnelle exécutable
- README
- Code source versionné GitHub
- Tests unitaires JUnit
