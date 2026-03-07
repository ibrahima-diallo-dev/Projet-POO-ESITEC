<div align="center">

<br/>

<img src="src/main/resources/images/logo-supdeco.png" alt="SUPDECO" width="100"/>

<br/>

# ��� Cahier de Texte Numérique

<h3>Application de gestion pédagogique — SUPDECO / ESITEC</h3>

<br/>

[![Java](https://img.shields.io/badge/Java-17_LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-0099cc?style=for-the-badge&logo=java&logoColor=white)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![iText](https://img.shields.io/badge/iText-8.0.2-FF6C37?style=for-the-badge&logo=adobe&logoColor=white)](https://itextpdf.com/)
[![JUnit](https://img.shields.io/badge/JUnit-5-25A162?style=for-the-badge&logo=junit5&logoColor=white)](https://junit.org/junit5/)
[![Build](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge&logo=checkmarx&logoColor=white)]()

<br/>

> *Application de bureau JavaFX permettant la gestion complète du cahier de texte numérique d'un établissement scolaire : saisie des séances, workflow de validation multi-rôles, notifications email automatiques et génération de fiches PDF.*

<br/>

</div>

---

<div align="center">

## ���‍��� Réalisé par

</div>

<br/>

<table align="center" width="80%">
  <tr>
    <td align="center" width="50%" style="padding: 20px;">
      <h2>��� Ibrahima Saidou Diallo</h2>
      <img src="https://img.shields.io/badge/Rôle-Développeur_Full--Stack-1E3A5F?style=flat-square"/>
      <br/><br/>
      <table>
        <tr><td>⚙️ Architecture MVC + DAO/Service</td></tr>
        <tr><td>���️ Couche d'accès données (DAO)</td></tr>
        <tr><td>��� Authentification & gestion des sessions</td></tr>
        <tr><td>��� Intégration email (Jakarta Mail)</td></tr>
        <tr><td>��� Génération PDF (iText 8)</td></tr>
        <tr><td>��� Configuration Maven & fat-JAR</td></tr>
      </table>
    </td>
    <td align="center" width="50%" style="padding: 20px;">
      <h2>��� Ibrahima Saidou Diallo</h2>
      <img src="https://img.shields.io/badge/Rôle-Développeur_Full--Stack-1E3A5F?style=flat-square"/>
      <br/><br/>
      <table>
        <tr><td>��� Interfaces FXML & thèmes CSS</td></tr>
        <tr><td>��� Modèles métier & énumérations</td></tr>
        <tr><td>���️ Contrôleurs JavaFX</td></tr>
        <tr><td>��� Conception base de données</td></tr>
        <tr><td>��� Tableaux, filtres & statistiques</td></tr>
        <tr><td>��� Tests unitaires JUnit 5</td></tr>
      </table>
    </td>
  </tr>
</table>

<br/>

---

## ��� Description du projet

Le **Cahier de Texte Numérique** est une solution complète de gestion pédagogique pour les établissements d'enseignement supérieur. Il numérise le processus traditionnel de suivi des cours et offre :

- Un **système de rôles** avec trois profils distincts et des droits spécifiques
- Un **workflow de validation** : saisie → soumission → validation/rejet
- Des **notifications email automatiques** à chaque étape clé
- Des **statistiques en temps réel** sur l'avancement pédagogique
- Une **génération de fiches PDF** professionnelles par enseignant
- Deux **thèmes visuels** (clair et sombre) commutables dynamiquement

---

## ���️ Workflow de l'application

```
┌─────────────┐     saisit      ┌──────────────────┐    soumet     ┌──────────────────────┐
│  ENSEIGNANT │ ──────────────▶ │ Séance EN_ATTENTE │ ───────────▶ │  Responsable notifié │
└─────────────┘                 └──────────────────┘    (email)    └──────────┬───────────┘
       ▲                                                                       │
       │  notifié (email)                              ┌─────────────────────┐│
       │  ✅ VALIDÉE                                   │  RESPONSABLE CLASSE ││
       │  ❌ REJETÉE + motif                           └─────────────────────┘│
       └───────────────────────────────────────────────────────────────────────┘

 CHEF DE DÉPARTEMENT : valide les comptes ──▶ assigne les cours ──▶ génère les rapports PDF
```

---

## ✨ Fonctionnalités détaillées

<details>
<summary><b>��� Authentification &amp; Sécurité</b></summary>
<br/>

- Connexion par **email + mot de passe + rôle sélectionné**
- Vérification de la **cohérence rôle/compte** (impossible de se connecter avec le mauvais rôle)
- Compte créé avec statut **EN_ATTENTE** jusqu'à validation par le chef
- Session persistante via `SessionManager` (singleton)
- Animation de fondu à l'ouverture de la vue login

</details>

<details>
<summary><b>���‍��� Chef de Département</b></summary>
<br/>

| Fonctionnalité | Détail |
|---|---|
| ��� **Statistiques globales** | Total séances, séances validées, taux de validation, heures effectuées |
| ✅ **Gestion des comptes** | Valider / suspendre les comptes en attente d'activation |
| ➕ **Créer un compte** | Ajouter un enseignant ou responsable directement avec statut ACTIF |
| ��� **Assigner un cours** | Lier un enseignant à un cours pour une classe donnée + email automatique |
| ��� **Fiche PDF** | Générer la fiche de suivi pédagogique d'un enseignant (iText 8) |
| ��� **Liste complète** | Voir tous les utilisateurs de la plateforme |

</details>

<details>
<summary><b>���‍��� Enseignant</b></summary>
<br/>

| Fonctionnalité | Détail |
|---|---|
| ��� **Mes séances** | Liste filtrée dynamiquement par statut (Tous / En attente / Validée / Rejetée) |
| ➕ **Ajouter une séance** | Formulaire pop-up : date, heure, durée, cours, contenu, observations |
| ✏️ **Modifier une séance** | Disponible si statut `EN_ATTENTE` ou `REJETÉE` uniquement |
| ���️ **Supprimer une séance** | Confirmation requise, impossible si séance `VALIDÉE` |
| ��� **Suivi pédagogique** | Volume horaire réalisé vs volume prévu par cours (%) |
| ��� **Mes cours** | Liste des cours assignés avec statistiques d'avancement |
| ��� **Export PDF** | Fiche de suivi personnelle générée dans le répertoire courant |

</details>

<details>
<summary><b>���‍��� Responsable de Classe</b></summary>
<br/>

| Fonctionnalité | Détail |
|---|---|
| ��� **Cahier de texte** | Toutes les séances de sa classe avec filtre par statut |
| ✅ **Valider une séance** | Un clic → BDD mise à jour + email automatique à l'enseignant |
| ❌ **Rejeter une séance** | Formulaire de motif (min. 10 caractères) + email avec le motif |
| ��� **Historique complet** | Toutes les séances validées et rejetées avec date et motif |
| ��� **Statistiques** | Séances prévues, réalisées, taux d'avancement par cours |

</details>

<details>
<summary><b>��� Notifications Email automatiques</b></summary>
<br/>

| Déclencheur | Destinataire | Contenu |
|---|---|---|
| Assignation d'un cours | Enseignant | Cours assigné + invitation à saisir les séances |
| Soumission d'une séance | Responsable de classe | Détails de la séance en attente de validation |
| Validation d'une séance | Enseignant | Confirmation de validation + contenu de la séance |
| Rejet d'une séance | Enseignant | Motif du rejet + invitation à corriger |

> Tous les envois sont **asynchrones** (JavaFX `Task`) : l'interface ne se bloque jamais.

</details>

---

## ���️ Stack technique

<div align="center">

| Catégorie | Technologie | Version | Rôle |
|---|---|---|---|
| ☕ **Langage** | Java | 17 LTS | Langage principal |
| ���️ **UI Framework** | JavaFX | 21 | Interface graphique |
| ��� **Build** | Maven | 3.8+ | Gestion des dépendances |
| ��� **Base de données** | MySQL | 8.0 | Persistance des données |
| ��� **Accès BDD** | JDBC natif | — | Requêtes SQL directes |
| ��� **PDF** | iText | 8.0.2 | Génération des fiches |
| ��� **Email** | Jakarta Mail | 2.0.1 | Notifications SMTP |
| ��� **Tests** | JUnit | 5.10.2 | Tests unitaires |
| ���️ **Architecture** | MVC + DAO/Service | — | Séparation des responsabilités |
| ��� **Fat-JAR** | maven-shade-plugin | 3.5.1 | Livraison exécutable |

</div>

---

## ���️ Structure du projet

```
Projet-POO-ESITEC/
│
├── ��� pom.xml                          ← Dépendances et configuration Maven
├── ��� README.md                        ← Ce fichier
│
├── ��� docs/
│   ├── schema_BD.sql                   ← Script de création de la base de données
│   └── user_guide.md                   ← Guide d'utilisation
│
└── ��� src/
    ├── ��� main/
    │   ├── ��� java/sn/esitec/poo/cahiertexte/
    │   │   ├── ��� App.java             ← Point d'entrée JavaFX
    │   │   ├── ��� Launcher.java        ← Wrapper pour le fat-JAR
    │   │   │
    │   │   ├── ��� controller/          ← Contrôleurs FXML (8 fichiers)
    │   │   ├── ��� dao/                 ← Accès base de données (4 fichiers)
    │   │   ├── ��� model/               ← Entités métier (9 fichiers)
    │   │   ├── ��� service/             ← Logique métier (5 fichiers)
    │   │   └── ��� utils/               ← Utilitaires (4 fichiers)
    │   │
    │   └── ��� resources/
    │       ├── application.properties  ← Config SMTP & BDD
    │       ├── ��� css/                 ← style.css + style-dark.css
    │       ├── ��� fxml/                ← 11 vues JavaFX
    │       └── ��� images/              ← Logos
    │
    └── ��� test/
        └── ��� java/                    ← Tests JUnit 5
```

---

## ⚙️ Installation & Démarrage

### Prérequis

| Outil | Version minimale | Vérification |
|---|---|---|
| ☕ JDK | 17 | `java -version` |
| ��� Maven | 3.8 | `mvn -version` |
| ��� MySQL | 8.0 | Via XAMPP ou standalone |

### Étape 1 — Cloner le projet

```bash
git clone <url-du-depot>
cd Projet-POO-ESITEC
```

### Étape 2 — Créer la base de données

```bash
mysql -u root -p < docs/schema_BD.sql
```

### Étape 3 — Démarrer MySQL (XAMPP)

Ouvrir **XAMPP Control Panel** → cliquer **Start** sur **MySQL**

### Étape 4 — (Optionnel) Configurer les emails

Éditer `src/main/resources/application.properties` :

```properties
mail.smtp.host=smtp.gmail.com
mail.smtp.port=587
mail.from=votre-adresse@gmail.com
mail.password=votre-mot-de-passe-application
```

### Étape 5 — Lancer l'application

```bash
mvn clean javafx:run
```

### Étape 6 — Générer le JAR exécutable

```bash
mvn clean package -DskipTests
java -jar target/cahier-texte-app.jar
```

---

## ��� Tests unitaires

```bash
mvn test
```

---

## ��� Thèmes visuels

| Thème | Bouton | Description |
|---|---|---|
| ☀️ **Clair** (défaut) | ��� | Interface bleu marine et blanc, style professionnel |
| ��� **Sombre** | ☀️ | Fond sombre `#1e293b`, réduction de la fatigue visuelle |

> Le thème est commutable en temps réel depuis la barre de navigation, sans redémarrage.

---

## ⚠️ Problèmes fréquents

| Problème | Cause | Solution |
|---|---|---|
| `Connection refused` au login | MySQL non démarré | Ouvrir XAMPP → Start MySQL |
| `ClassNotFoundException` | Build obsolète | `mvn clean compile` |
| Email non envoyé | SMTP non configuré | Renseigner `application.properties` |
| Fenêtre trop petite | — | Se maximise automatiquement au démarrage |

---

## ��� Livrables

- [x] ���️ Application JavaFX fonctionnelle (3 rôles, workflow complet)
- [x] ��� Code source intégralement commenté (Javadoc sur toutes les classes)
- [x] ���️ Script SQL de création BDD (`docs/schema_BD.sql`)
- [x] ��� JAR exécutable autonome (`target/cahier-texte-app.jar`)
- [x] ��� Tests unitaires JUnit 5
- [x] ��� README complet et structuré
- [x] ���️ Diagrammes UML (classes, cas d'utilisation, activités)
- [x] ��� Système de notifications email
- [x] ��� Génération PDF des fiches de suivi
- [x] ��� Double thème clair / sombre

---

<div align="center">

---

<img src="src/main/resources/images/logo-supdeco.png" alt="SUPDECO" width="60"/>

**SUPDECO / ESITEC — Dakar, Sénégal**

**Année académique 2025–2026**

*Projet réalisé dans le cadre du cours de Programmation Orientée Objet en Java*

<br/>

*Ibrahima Saidou Diallo &nbsp;•&nbsp; Abdoulaye Diallo*

</div>
