package sn.esitec.poo.cahiertexte.utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Utilitaire de navigation entre les vues FXML de l'application.
 * <p>
 * Fournit une méthode centralisée pour charger et afficher une nouvelle
 * scène JavaFX depuis un fichier FXML, en gérant automatiquement la
 * maximisation de la fenêtre et la gestion des erreurs de chargement.
 * </p>
 */
public class SceneUtils {
    /**
     * Charge et affiche une nouvelle scène FXML dans le {@link Stage} donné.
     * <p>
     * Le titre de la fenêtre sera préfixé par {@code "Cahier de Texte Numérique - "}.
     * La fenêtre est maximisée automatiquement après le rendu via
     * {@link Platform#runLater}.
     * </p>
     *
     * @param fxmlPath Chemin du fichier FXML (depuis le classpath, ex. {@code /fxml/login.fxml})
     * @param stage    Stage JavaFX dans lequel afficher la scène
     * @param title    Titre de la fenêtre (sans le préfixe commun)
     */
    public static void loadScene(String fxmlPath, Stage stage, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Cahier de Texte Numérique - " + title);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();
            // Forcer la maximisation après le rendu complet de la fenêtre
            Platform.runLater(() -> stage.setMaximized(true));
        } catch (IOException e) {
            System.err.println("Erreur de chargement FXML : " + fxmlPath);
            e.printStackTrace();
        }
    }
}