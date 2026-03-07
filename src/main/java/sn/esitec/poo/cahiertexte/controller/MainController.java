package sn.esitec.poo.cahiertexte.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import sn.esitec.poo.cahiertexte.model.Role;
import sn.esitec.poo.cahiertexte.model.Utilisateur;
import sn.esitec.poo.cahiertexte.utils.SceneUtils;
import sn.esitec.poo.cahiertexte.utils.SessionManager;

/**
 * Contrôleur du layout principal ({@code dashboard-layout.fxml}).
 * <p>
 * Affiche la barre de navigation et le panneau central de l'application.
 * Charge dynamiquement la vue métier (enseignant, responsable ou chef) dans
 * la zone de contenu selon le rôle de l'utilisateur connecté. Gère aussi
 * le basculement thème clair/sombre et la déconnexion.
 * </p>
 */
public class MainController {

    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Button logoutButton;
    @FXML private Button navBtn1;
    @FXML private Button navBtn2;
    @FXML private Button navBtn3;
    @FXML private Button themeToggleButton;
    @FXML private VBox contentArea;
    @FXML private BorderPane mainContainer;
    @FXML private Label versionLabel;

    private Utilisateur currentUser;
    private String[] viewPaths;
    private boolean isDarkMode = false;

    private static final String CSS_LIGHT = "/css/style.css";
    private static final String CSS_DARK  = "/css/style-dark.css";

    /**
     * Initialise le layout principal : remplit les informations utilisateur,
     * configure la navigation adaptée au rôle et charge la vue par défaut.
     */
    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getUtilisateurConnecte();

        if (currentUser != null) {
            userNameLabel.setText(currentUser.getPrenom() + " " + currentUser.getNom());
            userRoleLabel.setText(formatRole(currentUser.getRole()));
            configureNavigation();
            loadDefaultView();
        }

        logoutButton.setOnAction(e -> handleLogout());

        // Toggle dark/light mode
        if (themeToggleButton != null) {
            themeToggleButton.setText("🌙");
            themeToggleButton.setOnAction(e -> toggleTheme());
        }

        if (currentUser != null && currentUser.getRole() == Role.ENSEIGNANT) {
            navBtn1.setOnAction(e -> { loadEnseignantView(0); setActive(navBtn1); });
            navBtn2.setOnAction(e -> { loadEnseignantView(1); setActive(navBtn2); });
            navBtn3.setOnAction(e -> { loadEnseignantView(2); setActive(navBtn3); });
        } else if (currentUser != null && currentUser.getRole() == Role.RESPONSABLE_CLASSE) {
            navBtn1.setOnAction(e -> { loadResponsableView(0); setActive(navBtn1); });
            navBtn2.setOnAction(e -> { loadResponsableView(1); setActive(navBtn2); });
            navBtn3.setOnAction(e -> { loadResponsableView(2); setActive(navBtn3); });
        } else {
            navBtn1.setOnAction(e -> { loadView(viewPaths[0]); setActive(navBtn1); });
            navBtn2.setOnAction(e -> { loadView(viewPaths[1]); setActive(navBtn2); });
            navBtn3.setOnAction(e -> { loadView(viewPaths[2]); setActive(navBtn3); });
        }
    }

    /** Bascule entre le thème clair et le thème sombre et met à jour l'icône du bouton. */
    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        applyTheme(isDarkMode ? CSS_DARK : CSS_LIGHT);
        themeToggleButton.setText(isDarkMode ? "☀️" : "🌙");
    }

    /**
     * Applique la feuille de style CSS au conteneur principal.
     *
     * @param cssPath Chemin classpath du fichier CSS à appliquer
     */
    private void applyTheme(String cssPath) {
        if (mainContainer != null) {
            mainContainer.getStylesheets().clear();
            String fullPath = getClass().getResource(cssPath).toExternalForm();
            mainContainer.getStylesheets().add(fullPath);
        }
    }

    /**
     * Configure les libellés et chemins de vue des trois boutons de navigation
     * en fonction du rôle de l'utilisateur connecté.
     */
    private void configureNavigation() {
        Role role = currentUser.getRole();
        switch (role) {
            case ENSEIGNANT:
                navBtn1.setText("Mes Séances");
                navBtn2.setText("Mes Cours");
                navBtn3.setText("Suivi Pédagogique");
                viewPaths = new String[]{
                    "/fxml/enseignant.fxml",
                    "/fxml/enseignant.fxml",
                    "/fxml/enseignant.fxml"
                };
                break;
            case RESPONSABLE_CLASSE:
                navBtn1.setText("Cahier de Texte");
                navBtn2.setText("Historique");
                navBtn3.setText("Statistiques");
                viewPaths = new String[]{
                    "/fxml/responsable.fxml",
                    "/fxml/responsable.fxml",
                    "/fxml/responsable.fxml"
                };
                break;
            case CHEF_DEPARTEMENT:
                navBtn1.setText("Vue Globale");
                navBtn2.setText("Utilisateurs");
                navBtn3.setText("Rapports");
                viewPaths = new String[]{
                    "/fxml/chef-dashboard.fxml",
                    "/fxml/chef-utilisateurs.fxml",
                    "/fxml/chef-rapports.fxml"
                };
                break;
        }
    }

    private void loadDefaultView() {
        loadViewBasedOnRole();
        setActive(navBtn1);
    }

    private void loadViewBasedOnRole() {
        switch (currentUser.getRole()) {
            case ENSEIGNANT         -> loadEnseignantView(0);
            case RESPONSABLE_CLASSE -> loadResponsableView(0);
            case CHEF_DEPARTEMENT   -> loadView("/fxml/chef-dashboard.fxml");
        }
    }

    private void loadEnseignantView(int tabIndex) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/enseignant.fxml"));
            Node newNode = loader.load();
            EnseignantController controller = loader.getController();
            controller.selectTab(tabIndex);
            animateAndLoad(newNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadResponsableView(int tabIndex) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/responsable.fxml"));
            Node newNode = loader.load();
            ResponsableController controller = loader.getController();
            controller.selectTab(tabIndex);
            animateAndLoad(newNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Charge et affiche un fichier FXML dans la zone de contenu centrale,
     * avec une animation de fondu.
     *
     * @param fxmlPath Chemin classpath du fichier FXML à charger
     */
    private void loadView(String fxmlPath) {
        System.out.println("Loading view: " + fxmlPath);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node newNode = loader.load();
            animateAndLoad(newNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animateAndLoad(Node newNode) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), newNode);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        contentArea.getChildren().setAll(newNode);
        fadeIn.play();
    }

    private void setActive(Button btn) {
        navBtn1.getStyleClass().remove("nav-active");
        navBtn2.getStyleClass().remove("nav-active");
        navBtn3.getStyleClass().remove("nav-active");
        btn.getStyleClass().add("nav-active");
    }

    private String formatRole(Role role) {
        return switch (role) {
            case CHEF_DEPARTEMENT   -> "CHEF DE DÉPARTEMENT";
            case ENSEIGNANT         -> "ENSEIGNANT";
            case RESPONSABLE_CLASSE -> "RESPONSABLE DE CLASSE";
        };
    }

    /**
     * Déconnecte l'utilisateur : nettoie la session et redirige vers l'écran de connexion.
     */
    private void handleLogout() {
        SessionManager.getInstance().cleanUserSession();
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        SceneUtils.loadScene("/fxml/login.fxml", stage, "Connexion");
    }
}