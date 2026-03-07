package sn.esitec.poo.cahiertexte.controller;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import sn.esitec.poo.cahiertexte.model.Role;
import sn.esitec.poo.cahiertexte.model.Utilisateur;
import sn.esitec.poo.cahiertexte.service.AuthService;
import sn.esitec.poo.cahiertexte.service.CompteService;
import sn.esitec.poo.cahiertexte.utils.SceneUtils;
import sn.esitec.poo.cahiertexte.utils.SessionManager;


/**
 * Contrôleur de la vue de connexion ({@code login.fxml}).
 * <p>
 * Gère l'authentification des utilisateurs :
 * saisie des identifiants, vérification de la cohérence du rôle sélectionné,
 * stockage en session et redirection vers le tableau de bord.
 * Permet également la création d'un nouveau compte via un formulaire pop-up.
 * </p>
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    @FXML private VBox mainContainer;

    private final AuthService authService = new AuthService();

    /**
     * Initialise la vue login.
     * Déclenche une animation de fondu sur le formulaire et branche
     * le bouton de connexion sur {@link #handleLogin()}.
     */
    @FXML
    public void initialize() {
        errorLabel.setVisible(false);

        FadeTransition ft = new FadeTransition(Duration.millis(600), mainContainer);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        loginButton.setOnAction(e -> handleLogin());
    }

    /**
     * Traite la tentative de connexion.
     * Vérifie que tous les champs sont remplis, appelle
     * {@link sn.esitec.poo.cahiertexte.service.AuthService#connecter} et contrôle
     * la cohérence du rôle sélectionné avant de rediriger vers le dashboard.
     */
    private void handleLogin() {
        String email = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String roleSelectionne = roleCombo.getValue();

        if (email.isEmpty() || password.isEmpty() || roleSelectionne == null) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        Utilisateur user = authService.connecter(email, password);

        if (user == null) {
            showError("Identifiants incorrects. Veuillez réessayer.");
            return;
        }

        // Vérification cohérence rôle sélectionné vs rôle BDD
        Role roleAttendu = getRoleFromSelection(roleSelectionne);
        if (!user.getRole().equals(roleAttendu)) {
            showError("Le rôle sélectionné ne correspond pas à votre compte.");
            return;
        }

        SessionManager.getInstance().setUtilisateurConnecte(user);

        Stage stage = (Stage) loginButton.getScene().getWindow();
        SceneUtils.loadScene("/fxml/dashboard-layout.fxml", stage, "ESITEC");
    }

    /**
     * Convertit la chaîne sélectionnée dans le {@code ComboBox} en {@link Role}.
     *
     * @param selection Label affiché dans la liste déroulante (ex. {@code "Enseignant"})
     * @return Rôle correspondant, ou {@code null} si la valeur est inconnue
     */
    private Role getRoleFromSelection(String selection) {
        switch (selection) {
            case "Chef de département": return Role.CHEF_DEPARTEMENT;
            case "Enseignant":          return Role.ENSEIGNANT;
            case "Responsable de classe": return Role.RESPONSABLE_CLASSE;
            default: return null;
        }
    }
    /**
     * Ouvre la boîte de dialogue d'inscription et crée un nouveau compte
     * (statut initial {@code EN_ATTENTE}, en attente de validation par le chef).
     */
    @FXML
    private void handleVersInscription() {
    // 1. Création du formulaire d'inscription (Pop-up)
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Inscription - ESITEC");
    dialog.setHeaderText("Créez votre compte (Sera validé par le chef)");

    // Champs
    TextField nom = new TextField(); nom.setPromptText("Nom");
    TextField prenom = new TextField(); prenom.setPromptText("Prénom");
    TextField email = new TextField(); email.setPromptText("Email (Ex: p.diop@esitec.sn)");
    PasswordField mdp = new PasswordField(); mdp.setPromptText("Mot de passe (6 car. min)");
    ComboBox<String> roles = new ComboBox<>(FXCollections.observableArrayList("Enseignant", "Responsable de classe"));
    roles.setValue("Enseignant");

    VBox content = new VBox(10, 
        new Label("Nom:"), nom, new Label("Prénom:"), prenom,
        new Label("Email:"), email, new Label("Mot de passe:"), mdp,
        new Label("Rôle:"), roles
    );
    content.setPrefWidth(300);
    dialog.getDialogPane().setContent(content);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    // 2. Traitement de l'inscription
    dialog.showAndWait().ifPresent(response -> {
        if (response == ButtonType.OK) {
            CompteService service = new CompteService();
            boolean success;
            
            if (roles.getValue().equals("Enseignant")) {
                success = service.creerEnseignant(nom.getText(), prenom.getText(), email.getText(), mdp.getText());
            } else {
                success = service.creerResponsable(nom.getText(), prenom.getText(), email.getText(), mdp.getText());
            }

            if (success) {
                // Succès : l'utilisateur est en BDD avec le statut EN_ATTENTE
                errorLabel.setStyle("-fx-text-fill: #059669;");
                errorLabel.setText("Inscription réussie ! Patientez la validation du Chef.");
            } else {
                errorLabel.setText("Erreur : Données invalides ou email déjà utilisé.");
            }
        }
    });
}

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}