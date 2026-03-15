-- ============================================================
-- Schéma SQLite du Cahier de Texte Numérique (ESITEC)
-- Compatible SQLite 3.x - Généré depuis cahier_texte.sql (MySQL)
-- Utilise INSERT OR IGNORE pour être idempotent au redémarrage
-- ============================================================

PRAGMA foreign_keys = OFF;

-- Table utilisateurs (créée en premier car référencée par classes)
CREATE TABLE IF NOT EXISTS utilisateurs (
    id_user       INTEGER PRIMARY KEY AUTOINCREMENT,
    nom_user      TEXT NOT NULL,
    prenom_user   TEXT NOT NULL,
    email         TEXT NOT NULL UNIQUE,
    mot_de_passe  TEXT NOT NULL,
    role          TEXT NOT NULL,
    statut        TEXT DEFAULT 'EN_ATTENTE',
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    date_inscription DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Table classes
CREATE TABLE IF NOT EXISTS classes (
    id_classe      INTEGER PRIMARY KEY AUTOINCREMENT,
    nom_classe     TEXT NOT NULL,
    filiere        TEXT,
    id_responsable INTEGER
);

-- Table cours
CREATE TABLE IF NOT EXISTS cours (
    id_cours       INTEGER PRIMARY KEY AUTOINCREMENT,
    intitule       TEXT NOT NULL,
    volume_horaire INTEGER DEFAULT 0,
    id_enseignant  INTEGER,
    id_classe      INTEGER
);

-- Table seances
CREATE TABLE IF NOT EXISTS seances (
    id_seance          INTEGER PRIMARY KEY AUTOINCREMENT,
    date_seance        TEXT NOT NULL,
    heure_debut        TEXT NOT NULL,
    duree              INTEGER NOT NULL,
    contenu            TEXT NOT NULL,
    observations       TEXT,
    statut             TEXT DEFAULT 'EN_ATTENTE',
    commentaire_rejet  TEXT,
    id_cours           INTEGER,
    id_enseignant      INTEGER
);

-- ============================================================
-- Données initiales - utilisateurs
-- ============================================================
INSERT OR IGNORE INTO utilisateurs (id_user, nom_user, prenom_user, email, mot_de_passe, role, statut, date_creation, date_inscription) VALUES
(3,  'DIALLO',  'Mamadou',  'mamadou.diallo@esitec.sn',  'admin123', 'CHEF_DEPARTEMENT',   'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(4,  'NDIAYE',  'Fatou',    'fatou.ndiaye@esitec.sn',    'admin123', 'CHEF_DEPARTEMENT',   'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(5,  'SALL',    'Ibrahima', 'ibrahima.sall@esitec.sn',   'admin123', 'CHEF_DEPARTEMENT',   'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(6,  'FALL',    'Cheikh',   'torresdiallo21@gmail.com',     'prof123',  'ENSEIGNANT',         'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(7,  'SECK',    'Aminata',  'aminata.seck@esitec.sn',    'prof123',  'ENSEIGNANT',         'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(8,  'BA',      'Ousmane',  'ousmane.ba@esitec.sn',      'prof123',  'ENSEIGNANT',         'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(9,  'FAYE',    'Mariama',  'mariama.faye@esitec.sn',    'prof123',  'ENSEIGNANT',         'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(10, 'GUEYE',   'Cheikh',   'cheikh.gueye@esitec.sn',    'prof123',  'ENSEIGNANT',         'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(11, 'SARR',    'Rokhaya',  'rokhaya.sarr@esitec.sn',    'prof123',  'ENSEIGNANT',         'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(12, 'MBAYE',   'Serigne',  'serigne.mbaye@esitec.sn',   'prof123',  'ENSEIGNANT',         'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(13, 'THIAM',   'Ndeye',    'ndeye.thiam@esitec.sn',     'prof123',  'ENSEIGNANT',         'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(14, 'DIOUF',   'Pape',     'pape.diouf@esitec.sn',      'prof123',  'ENSEIGNANT',         'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(15, 'CISSE',   'Khadija',  'khadija.cisse@esitec.sn',   'prof123',  'ENSEIGNANT',         'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(16, 'DIOP',    'Abdoulaye','abdoulaye.diop@esitec.sn',   'prof123',  'ENSEIGNANT',         'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(17, 'TOURE',   'Aissatou', 'aissatou.toure@esitec.sn',  'prof123',  'ENSEIGNANT',         'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(18, 'WADE',    'Moussa',   'moussa.wade@esitec.sn',     'prof123',  'ENSEIGNANT',         'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(19, 'NGOM',    'Binta',    'binta.ngom@esitec.sn',      'resp123',  'RESPONSABLE_CLASSE', 'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(20, 'KANE',    'Lamine',   'lamine.kane@esitec.sn',     'resp123',  'RESPONSABLE_CLASSE', 'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(21, 'LO',      'Sokhna',   'sokhna.lo@esitec.sn',       'resp123',  'RESPONSABLE_CLASSE', 'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(22, 'NIANG',   'Babacar',  'ibrahimasaidoudiallo43@gmail.com',   'resp123',  'RESPONSABLE_CLASSE', 'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(23, 'BADJI',   'Fatima',   'fatima.badji@esitec.sn',    'resp123',  'RESPONSABLE_CLASSE', 'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(24, 'CAMARA',  'Ibou',     'ibou.camara@esitec.sn',     'resp123',  'RESPONSABLE_CLASSE', 'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(25, 'NDOYE',   'Seynabou', 'seynabou.ndoye@esitec.sn',  'resp123',  'RESPONSABLE_CLASSE', 'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00'),
(26, 'DEME',    'Aliou',    'aliou.deme@esitec.sn',      'resp123',  'RESPONSABLE_CLASSE', 'VALIDE', '2025-09-01 08:00:00', '2025-09-15 10:00:00');

-- ============================================================
-- Données initiales - classes
-- ============================================================
INSERT OR IGNORE INTO classes (id_classe, nom_classe, filiere, id_responsable) VALUES
(1, 'L1 Informatique A', 'Informatique', 19),
(2, 'L2 Informatique B', 'Informatique', 22),
(3, 'L3 Informatique',   'Informatique', 21),
(4, 'L1 Gestion A',      'Gestion',      20),
(5, 'L2 Gestion B',      'Gestion',      23),
(6, 'L3 Gestion',        'Gestion',      24);

-- ============================================================
-- Données initiales - cours
-- ============================================================
INSERT OR IGNORE INTO cours (id_cours, intitule, volume_horaire, id_enseignant, id_classe) VALUES
(1,  'Algorithmique',             40, 4,  1),
(2,  'Programmation Java',        60, 4,  1),
(3,  'Base de donnees',           45, 5,  2),
(4,  'Reseaux informatiques',     40, 6,  2),
(5,  'Mathematiques discretes',   50, 7,  1),
(6,  'Systemes exploitation',     45, 8,  3),
(7,  'Developpement Web',         50, 9,  3),
(8,  'Comptabilite generale',     45, 10, 4),
(9,  'Marketing digital',         30, 11, 4),
(10, 'Gestion de projet',         40, 12, 5),
(11, 'Statistiques',              45, 13, 5),
(12, 'Economie generale',         40, 14, 6);

-- ============================================================
-- Données initiales - seances
-- ============================================================
INSERT OR IGNORE INTO seances (id_seance, date_seance, heure_debut, duree, contenu, observations, statut, commentaire_rejet, id_cours, id_enseignant) VALUES
(1,  '2026-01-05', '08:00:00', 120, 'Introduction aux algorithmes et notion de complexite',    'Bonne participation generale',                   'VALIDEE',    '',                                          1, 4),
(2,  '2026-01-12', '08:00:00', 120, 'Structures de donnees : tableaux et listes chainees',     'Quelques absences notees',                       'VALIDEE',    '',                                          1, 4),
(3,  '2026-01-19', '08:00:00', 120, 'Algorithmes de tri : tri a bulles et tri rapide',          'Exercices pratiques effectues',                  'VALIDEE',    '',                                          1, 4),
(4,  '2026-01-26', '08:00:00', 120, 'Recursivite et algorithmes recursifs',                    'Difficultes sur la recursivite',                  'EN_ATTENTE', '',                                          1, 4),
(5,  '2026-01-06', '10:00:00', 180, 'Introduction a Java et installation environnement',       'TP en salle informatique',                        'VALIDEE',    '',                                          2, 4),
(6,  '2026-01-13', '10:00:00', 180, 'Variables types primitifs et operateurs',                 'Exercices corriges en classe',                    'VALIDEE',    '',                                          2, 4),
(7,  '2026-01-20', '10:00:00', 180, 'Structures conditionnelles et boucles',                   'Bon niveau general',                             'VALIDEE',    '',                                          2, 4),
(8,  '2026-01-27', '10:00:00', 180, 'POO : classes et objets',                                 'TP pratique realise',                            'EN_ATTENTE', '',                                          2, 4),
(9,  '2026-01-07', '14:00:00', 120, 'Introduction aux bases de donnees relationnelles',        'Cours magistral',                                'VALIDEE',    '',                                          3, 5),
(10, '2026-01-14', '14:00:00', 120, 'Le modele entite-association',                            'TD sur cas pratiques',                           'VALIDEE',    '',                                          3, 5),
(11, '2026-01-21', '14:00:00', 120, 'SQL : CREATE TABLE et INSERT',                            'TP sur MySQL',                                   'VALIDEE',    '',                                          3, 5),
(12, '2026-01-28', '14:00:00', 120, 'SQL : SELECT et jointures',                               'Exercices pratiques',                            'REJETEE',    'Contenu insuffisant revoir les jointures',   3, 5),
(13, '2026-01-08', '08:00:00', 120, 'Introduction aux reseaux informatiques',                  'Cours theorique',                                'VALIDEE',    '',                                          4, 6),
(14, '2026-01-15', '08:00:00', 120, 'Le modele OSI et TCP/IP',                                 'Schemas explicatifs utilises',                   'VALIDEE',    '',                                          4, 6),
(15, '2026-01-22', '08:00:00', 120, 'Adressage IP et sous-reseaux',                            'Exercices de calcul effectues',                  'EN_ATTENTE', '',                                          4, 6),
(16, '2026-01-29', '08:00:00', 120, 'Protocoles reseau : HTTP FTP DNS',                        'Bonne participation',                            'EN_ATTENTE', '',                                          4, 6),
(17, '2026-01-09', '10:00:00',  90, 'Introduction aux mathematiques discretes',                'Cours magistral',                                'VALIDEE',    '',                                          5, 7),
(18, '2026-01-16', '10:00:00',  90, 'Logique propositionnelle et predicats',                   'Exercices corriges',                             'VALIDEE',    '',                                          5, 7),
(19, '2026-01-23', '10:00:00',  90, 'Theorie des ensembles et relations',                      'Bon deroulement',                                'EN_ATTENTE', '',                                          5, 7),
(20, '2026-01-10', '14:00:00', 120, 'Introduction aux systemes exploitation',                  'Cours theorique',                                'VALIDEE',    '',                                          6, 8),
(21, '2026-01-17', '14:00:00', 120, 'Gestion des processus et ordonnancement',                 'TP sous Linux',                                  'VALIDEE',    '',                                          6, 8),
(22, '2026-01-24', '14:00:00', 120, 'Gestion de la memoire virtuelle',                         'Cours dense',                                    'REJETEE',    'Veuillez detailler la partie pagination',    6, 8),
(23, '2026-01-31', '14:00:00', 120, 'Systeme de fichiers et permissions',                      'TP pratique',                                    'EN_ATTENTE', '',                                          6, 8),
(24, '2026-01-11', '10:00:00', 120, 'Developpement web : HTML et CSS',                         'TP en salle',                                    'VALIDEE',    '',                                          7, 9),
(25, '2026-01-18', '10:00:00', 120, 'JavaScript et manipulation du DOM',                       'Exercices pratiques',                            'VALIDEE',    '',                                          7, 9),
(26, '2026-01-25', '10:00:00', 120, 'Introduction a React et composants',                      'Bon niveau general',                             'EN_ATTENTE', '',                                          7, 9),
(27, '2026-01-12', '14:00:00',  90, 'Introduction a la comptabilite generale',                 'Bonne ambiance en classe',                       'VALIDEE',    '',                                          8, 10),
(28, '2026-01-19', '14:00:00',  90, 'Bilan et compte de resultat',                             'Cas pratiques traites',                          'VALIDEE',    '',                                          8, 10),
(29, '2026-01-26', '14:00:00',  90, 'Operations d achat et de vente',                          'Exercices supplementaires donnes',               'EN_ATTENTE', '',                                          8, 10),
(30, '2026-01-13', '10:00:00',  90, 'Introduction au marketing digital',                       'Cours interactif',                               'VALIDEE',    '',                                          9, 11),
(31, '2026-01-20', '10:00:00',  90, 'Reseaux sociaux et strategie digitale',                   'Etudes de cas reels',                            'VALIDEE',    '',                                          9, 11),
(32, '2026-01-27', '10:00:00',  90, 'SEO et publicite en ligne',                               'Bonne participation',                            'EN_ATTENTE', '',                                          9, 11),
(33, '2026-01-14', '08:00:00', 120, 'Introduction a la gestion de projet',                     'Methode agile presentee',                        'VALIDEE',    '',                                          10, 12),
(34, '2026-01-21', '08:00:00', 120, 'Planification et diagramme de Gantt',                     'Exercices pratiques',                            'VALIDEE',    '',                                          10, 12),
(35, '2026-01-28', '08:00:00', 120, 'Gestion des risques et des ressources',                   'Cas pratique traite',                            'EN_ATTENTE', '',                                          10, 12),
(36, '2026-01-15', '14:00:00',  90, 'Introduction aux statistiques descriptives',              'Cours magistral',                                'VALIDEE',    '',                                          11, 13),
(37, '2026-01-22', '14:00:00',  90, 'Mesures de tendance centrale et dispersion',              'Exercices corriges',                             'VALIDEE',    '',                                          11, 13),
(38, '2026-01-29', '14:00:00',  90, 'Probabilites et distributions',                           'Bon niveau general',                             'EN_ATTENTE', '',                                          11, 13),
(39, '2026-01-16', '10:00:00',  90, 'Introduction a l economie generale',                      'Cours interactif',                               'VALIDEE',    '',                                          12, 14),
(40, '2026-01-23', '10:00:00',  90, 'Offre demande et equilibre du marche',                    'Etudes de cas',                                  'VALIDEE',    '',                                          12, 14),
(41, '2026-01-30', '10:00:00',  90, 'Politique monetaire et fiscale',                          'Bonne participation',                            'EN_ATTENTE', '',                                          12, 14);
