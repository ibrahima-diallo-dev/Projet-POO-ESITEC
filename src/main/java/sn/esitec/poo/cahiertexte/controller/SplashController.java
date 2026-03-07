package sn.esitec.poo.cahiertexte.controller;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import sn.esitec.poo.cahiertexte.utils.SceneUtils;

/**
 * Contrôleur du splash screen affiché au démarrage de l'application.
 * <p>
 * Affiche un écran de chargement animé pendant 3,5 secondes avant de
 * charger automatiquement l'écran de connexion.
 * </p>
 */
public class SplashController {

    @FXML private ProgressBar progressBar;
    @FXML private Label chargementLabel;
    @FXML private ImageView logoImage;

    /**
     * Initialise le splash screen.
     * <p>
     * Démarre la barre de progression en mode indéterminé et programme
     * le chargement de la vue login après 3,5 secondes via un
     * {@link javafx.animation.PauseTransition}.
     * </p>
     */
    @FXML
    public void initialize() {
        // Mode indéterminé : JavaFX anime la barre automatiquement (segment bleu qui bounce)
        progressBar.setProgress(-1);

        // Après 3.5 secondes, passer au login
        PauseTransition pause = new PauseTransition(Duration.seconds(3.5));
        pause.setOnFinished(e -> {
            Stage stage = (Stage) progressBar.getScene().getWindow();
            stage.setMaximized(true);
            SceneUtils.loadScene("/fxml/login.fxml", stage,
                "ESITEC - Cahier de Texte Numérique");
        });
        pause.play();
    }
}
