package sn.esitec.poo.cahiertexte.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utilitaire de connexion à la base de données SQLite.
 * <p>
 * Utilise un fichier SQLite local stocké dans le répertoire personnel
 * de l'utilisateur ({@code ~/.esitec/cahier_texte.db}), ce qui rend
 * le JAR 100 % portable : aucun serveur MySQL requis.
 * </p>
 */
public class DatabaseConnection {

    private static final String DB_DIR  = System.getProperty("user.home") + File.separator + ".esitec";
    private static final String DB_PATH = DB_DIR + File.separator + "cahier_texte.db";
    private static final String URL     = "jdbc:sqlite:" + DB_PATH;

    private static Connection instance = null;

    /**
     * Retourne le chemin absolu du fichier de base de données SQLite.
     * Utilisé par {@link DatabaseInitializer} pour vérifier l'existence du fichier.
     */
    public static String getDbPath() {
        return DB_PATH;
    }

    /**
     * Retourne la connexion active à la base de données SQLite.
     * Crée le répertoire parent et rouvre la connexion si nécessaire.
     *
     * @return Instance unique de {@link java.sql.Connection}
     * @throws SQLException si la connexion échoue
     */
    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            // Crée ~/.esitec/ si absent
            new File(DB_DIR).mkdirs();
            System.out.println("🔌 Connexion SQLite : " + DB_PATH);
            instance = DriverManager.getConnection(URL);
            System.out.println("✅ Connexion SQLite établie");
        }
        return instance;
    }

    /**
     * Ferme la connexion active si elle est ouverte.
     * <p>
     * Cette méthode peut être appelée à l'arrêt de l'application pour
     * libérer proprement les ressources JDBC.
     * </p>
     */
    public static void closeConnection() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
                System.out.println("Connexion fermée.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
