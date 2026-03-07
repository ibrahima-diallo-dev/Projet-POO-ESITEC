package sn.esitec.poo.cahiertexte.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utilitaire de connexion à la base de données MySQL.
 * <p>
 * Implémente un singleton de connexion JDBC : une seule instance
 * {@link java.sql.Connection} est maintenue pour toute la durée de vie
 * de l'application. La connexion est réouverte automatiquement si elle
 * est fermée ou nulle.
 * </p>
 * <p>
 * Base de données cible : {@code jdbc:mysql://localhost:3306/cahier_texte}
 * </p>
 */
public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/cahier_texte";
    private static final String USER     = "root";
    private static final String PASSWORD = ""; 

    private static Connection instance = null;

    /**
     * Retourne la connexion active à la base de données.
     * <p>
     * Si la connexion n'existe pas encore ou a été fermée, une nouvelle
     * connexion JDBC est établie avant d'être retournée.
     * </p>
     *
     * @return Instance unique de {@link java.sql.Connection}
     * @throws SQLException si la connexion échoue (driver absent, serveur
     *                      inaccessible, mauvais identifiants, etc.)
     */
    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            System.out.println("🔌 Tentative de connexion à la base de données: " + URL);
            instance = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion établie avec succès");
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
