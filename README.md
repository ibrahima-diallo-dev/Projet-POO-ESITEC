<div align="center">

<img src="src/main/resources/images/logo_esitec.png" alt="ESITEC" width="110"/>

# Cahier de Texte Numerique

### Application desktop JavaFX pour la gestion pedagogique d'ESITEC

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-0A84FF?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjfx.io/)
[![SQLite](https://img.shields.io/badge/SQLite-3-0F80CC?style=for-the-badge&logo=sqlite&logoColor=white)](https://www.sqlite.org/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![iText](https://img.shields.io/badge/PDF-iText%208-F97316?style=for-the-badge&logo=adobeacrobatreader&logoColor=white)](https://itextpdf.com/)
[![JUnit](https://img.shields.io/badge/Tests-JUnit%205-16A34A?style=for-the-badge&logo=junit5&logoColor=white)](https://junit.org/junit5/)

<br/>

> Une application de bureau moderne qui numerise le cahier de texte d'un etablissement: gestion des comptes, saisie des seances, validation multi-roles, notifications email et generation de rapports PDF.

</div>

---

## Apercu

Le projet permet de suivre l'activite pedagogique d'un etablissement a travers une interface JavaFX organisee autour de trois profils:

- Enseignant: saisit, modifie et suit ses seances.
- Responsable de classe: valide ou rejette les seances soumises.
- Chef de departement: gere les comptes, assigne les cours et genere des fiches PDF.

Le stockage est maintenant base sur SQLite. La base est creee automatiquement au demarrage a partir du script [src/main/resources/init.sql](src/main/resources/init.sql), ce qui evite toute dependance a MySQL ou XAMPP.

---

## Points forts

| Domaine                | Ce que fait l'application                              |
| ---------------------- | ------------------------------------------------------ |
| Authentification       | Connexion par email, mot de passe et role              |
| Workflow               | Soumission, validation et rejet des seances            |
| Persistance            | Base SQLite locale initialisee automatiquement         |
| Productivite           | Filtres, tableaux, statistiques et suivi par cours     |
| Communication          | Notifications email automatiques aux acteurs concernes |
| Reporting              | Export PDF des fiches pedagogiques                     |
| Experience utilisateur | Interface JavaFX, themes clair et sombre               |

---

## Workflow metier

```text
ENSEIGNANT
    |
    | saisit une seance
    v
SEANCE EN_ATTENTE
    |
    | notification email
    v
RESPONSABLE DE CLASSE
    |                \
    | valide          \ rejette avec motif
    v                 v
SEANCE VALIDEE     SEANCE REJETEE
    |                 |
    | email           | email
    v                 v
ENSEIGNANT       ENSEIGNANT

CHEF DE DEPARTEMENT
    -> valide les comptes
    -> assigne les cours
    -> consulte les utilisateurs
    -> genere les fiches PDF
```

---

## Fonctionnalites

### Chef de departement

- Validation et suspension des comptes utilisateurs.
- Creation de comptes enseignant et responsable.
- Assignation des cours aux enseignants.
- Consultation des statistiques globales.
- Generation de fiches de suivi PDF.

### Enseignant

- Consultation des cours qui lui sont assignes.
- Ajout d'une seance via formulaire.
- Modification d'une seance non validee.
- Suppression d'une seance selon son statut.
- Filtrage des seances par etat.
- Export PDF de son suivi pedagogique.

### Responsable de classe

- Consultation du cahier de texte de sa classe.
- Validation des seances soumises.
- Rejet motive des seances.
- Suivi de l'historique et de l'avancement pedagogique.

### Notifications email

- Email lors de l'assignation d'un cours.
- Email au responsable lorsqu'une seance est soumise.
- Email a l'enseignant apres validation.
- Email a l'enseignant apres rejet avec motif.

---

## Stack technique

| Couche          | Technologie                 |
| --------------- | --------------------------- |
| Langage         | Java 17                     |
| Interface       | JavaFX 21 + FXML + CSS      |
| Build           | Maven                       |
| Base de donnees | SQLite via sqlite-jdbc      |
| PDF             | iText 8                     |
| Email           | Jakarta Mail                |
| Tests           | JUnit 5                     |
| Organisation    | MVC + DAO + Service + Utils |

---

## Structure du projet

```text
Projet-POO-ESITEC/
├── pom.xml
├── README.md
├── docs/
│   └── cahier_texte.sql
├── src/
│   ├── main/
│   │   ├── java/sn/esitec/poo/cahiertexte/
│   │   │   ├── App.java
│   │   │   ├── Launcher.java
│   │   │   ├── controller/
│   │   │   ├── dao/
│   │   │   ├── model/
│   │   │   ├── service/
│   │   │   └── utils/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── init.sql
│   │       ├── css/
│   │       ├── fxml/
│   │       └── images/
│   └── test/
│       ├── java/
│       └── resources/
└── target/
```

---

## Lancement rapide

### Prerequis

- JDK 17
- Maven 3.8 ou plus
- Windows recommande pour le fat-JAR fourni avec les binaires JavaFX Windows

### 1. Cloner le projet

```bash
git clone <url-du-depot>
cd Projet-POO-ESITEC
```

### 2. Lancer en mode developpement

```bash
mvn clean javafx:run
```

### 3. Construire le JAR executable

```bash
mvn clean package -DskipTests
java -jar target/cahier-texte-app.jar
```

---

## Base de donnees

Le projet n'a pas besoin d'un serveur externe.

- La base SQLite est stockee dans le dossier utilisateur, sous un repertoire .esitec.
- Le schema et les donnees de demonstration sont charges depuis [src/main/resources/init.sql](src/main/resources/init.sql).
- L'initialisation est declenchee au demarrage de l'application par la classe [src/main/java/sn/esitec/poo/cahiertexte/utils/DatabaseInitializer.java](src/main/java/sn/esitec/poo/cahiertexte/utils/DatabaseInitializer.java).

Cela simplifie fortement le demarrage du projet pour les demonstrations et les tests locaux.

---

## Configuration email

Les parametres SMTP se trouvent dans [src/main/resources/application.properties](src/main/resources/application.properties).

Exemple de configuration:

```properties
mail.smtp.host=smtp.gmail.com
mail.smtp.port=587
mail.from=adresse@gmail.com
mail.password=mot-de-passe-application
```

Le projet supporte aussi la redirection des emails en mode demonstration pour envoyer toutes les notifications vers une ou plusieurs adresses definies.

---

## Tests

```bash
mvn test
```

---

## Interface et themes

| Theme  | Description                                      |
| ------ | ------------------------------------------------ |
| Clair  | Palette lumineuse orientee bleu et blanc         |
| Sombre | Interface plus contrastee pour un usage prolonge |

Les styles sont definis dans le dossier [src/main/resources/css](src/main/resources/css).

---

## Livrables couverts

- Application JavaFX complete.
- Gestion des trois roles metier.
- Workflow pedagogique de bout en bout.
- Base SQLite autonome.
- Notifications email.
- Exports PDF.
- Themes visuels clair et sombre.
- Tests unitaires JUnit 5.

---

## Equipe

| Nom                    | Contribution principale                                       |
| ---------------------- | ------------------------------------------------------------- |
| Abdoulaye Diallo       | Architecture, DAO, sessions, email, PDF, build Maven          |
| Ibrahima Saidou Diallo | Interfaces FXML, modeles, controleurs, base de donnees, tests |

---

<div align="center">

<img src="src/main/resources/images/logo-supdeco.png" alt="SUPDECO" width="70"/>

**SUPDECO / ESITEC - Dakar, Senegal**

Projet de Programmation Orientee Objet en Java

Annee academique 2025-2026

</div>
