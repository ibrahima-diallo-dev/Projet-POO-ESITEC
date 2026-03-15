package sn.esitec.poo.cahiertexte;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sn.esitec.poo.cahiertexte.utils.DatabaseInitializer;
import sn.esitec.poo.cahiertexte.utils.SceneUtils;

import java.util.Objects;

/**
 * Classe principale de l'application "Cahier de Texte Numérique".
 * Initialise la fenêtre principale et charge la vue de connexion.
 */
public class App extends Application {

    private static final String APP_TITLE = "ESITEC - Cahier de Texte Numérique";
    private static final String LOGIN_FXML = "/fxml/splash.fxml";
    private static final String ICON_PATH = "/images/logo-supdeco.png";

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Initialisation de la base SQLite (crée le fichier + données si absent)
            DatabaseInitializer.initialize();

            // 2. Configuration de l'icône de l'application
            setupAppIcon(primaryStage);

            // 3. Chargement de la scène initiale (géré en plein écran par SceneUtils)
            SceneUtils.loadScene(LOGIN_FXML, primaryStage, APP_TITLE);
            primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.setMaximized(true);
            
         

        } catch (Exception e) {
            System.err.println("Erreur critique lors du démarrage de l'application.");
            e.printStackTrace();
        }
    }

    /**
     * Tente de charger l'icône de l'application depuis les ressources.
     */
    private void setupAppIcon(Stage stage) {
        try {
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ICON_PATH)));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("Note : Icône de l'application non trouvée. Utilisation de l'icône système par défaut.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}