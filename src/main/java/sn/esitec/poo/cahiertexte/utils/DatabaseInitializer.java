package sn.esitec.poo.cahiertexte.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;
import java.util.Arrays;

/**
 * Initialise la base de données SQLite au premier lancement.
 * <p>
 * Lit {@code /init.sql} depuis les ressources embarquées dans le JAR,
 * crée les tables et insère les données de démonstration si elles
 * ne sont pas encore présentes. Grâce aux clauses {@code CREATE TABLE IF NOT EXISTS}
 * et {@code INSERT OR IGNORE}, cette méthode est idempotente : elle peut
 * être appelée à chaque démarrage sans effet de bord.
 * </p>
 */
public class DatabaseInitializer {

    private static final String INIT_SQL_RESOURCE = "/init.sql";

    /**
     * Point d'entrée principal : initialise la base si nécessaire.
     * Appelé au démarrage de l'application depuis {@code App.start()}.
     */
    public static void initialize() {
        System.out.println("🗄️  Initialisation de la base SQLite...");
        try {
            // On récupère la connexion SANS la fermer (try-with-resources fermerait le singleton)
            Connection conn = DatabaseConnection.getConnection();
            if (!tablesExistent(conn)) {
                System.out.println("📋 Création des tables et insertion des données...");
            }
            executerScript(conn, INIT_SQL_RESOURCE);
            System.out.println("✅ Base de données prête.");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation de la base : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Vérifie si la table {@code utilisateurs} existe déjà dans la base.
     */
    private static boolean tablesExistent(Connection conn) {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, "utilisateurs", null)) {
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Lit le fichier SQL depuis le classpath et exécute chaque instruction
     * séparée par un point-virgule.
     *
     * @param conn     Connexion JDBC active
     * @param resource Chemin de la ressource (ex: {@code /init.sql})
     */
    private static void executerScript(Connection conn, String resource) throws Exception {
        InputStream is = DatabaseInitializer.class.getResourceAsStream(resource);
        if (is == null) {
            throw new IllegalStateException("Ressource SQL introuvable : " + resource);
        }

        String contenu;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            contenu = reader.lines().collect(Collectors.joining("\n"));
        }

        // Découpe par ";" en ignorant les lignes de commentaires et les blancs
        String[] instructions = contenu.split(";");
        try (Statement st = conn.createStatement()) {
            for (String instruction : instructions) {
                // Retirer toutes les lignes qui commencent par "--" avant de tester
                String sql = Arrays.stream(instruction.split("\n"))
                        .filter(line -> !line.trim().startsWith("--"))
                        .collect(Collectors.joining("\n"))
                        .trim();
                if (!sql.isEmpty()) {
                    try {
                        st.execute(sql);
                    } catch (SQLException e) {
                        // On logue sans stopper : INSERT OR IGNORE peut signaler
                        // un conflit de contrainte inoffensif
                        System.err.println("⚠️  SQL ignoré : " + e.getMessage());
                    }
                }
            }
        }
    }
}
