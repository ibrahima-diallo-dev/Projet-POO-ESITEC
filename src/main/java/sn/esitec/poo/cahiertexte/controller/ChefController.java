package sn.esitec.poo.cahiertexte.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import sn.esitec.poo.cahiertexte.dao.ClasseDAO;
import sn.esitec.poo.cahiertexte.dao.CoursDAO;
import sn.esitec.poo.cahiertexte.dao.SeanceDAO;
import sn.esitec.poo.cahiertexte.model.*;
import sn.esitec.poo.cahiertexte.service.CompteService;
import sn.esitec.poo.cahiertexte.service.EmailService;
import sn.esitec.poo.cahiertexte.utils.PdfGenerator;

import java.io.File;
import java.util.List;

/**
 * Contrôleur du tableau de bord du Chef de Département ({@code chef-dashboard.fxml}).
 * <p>
 * Permet au chef de département de :
 * <ul>
 *   <li>Consulter les statistiques globales des séances</li>
 *   <li>Valider, suspendre ou supprimer des comptes utilisateurs</li>
 *   <li>Assigner des cours à des enseignants pour une classe</li>
 *   <li>Générer des fiches de suivi pédagogique PDF</li>
 *   <li>Créer manuellement de nouveaux comptes</li>
 * </ul>
 * </p>
 */
public class ChefController {

    @FXML private TableView<Utilisateur> comptesTable;
    @FXML private TableColumn<Utilisateur, String> colNom, colPrenom, colEmail, colRole, colStatutCompte;
    @FXML private TableColumn<Utilisateur, Void> colActionsCompte;
    @FXML private Label totalSeancesLabel, seancesValidesLabel, tauxValidationLabel,
                        enseignantsActifsLabel, comptesAttenteLabel;
    @FXML private ComboBox<String> enseignantCombo, classeCombo, pdfEnseignantCombo;
    @FXML private TextField nomCoursField;
    @FXML private Button validerCompteButton, assignerCoursButton, genererPdfButton, ajouterUtilisateurButton;
    @FXML private Label assignErrorLabel, pdfErrorLabel;

    @FXML private TableView<Utilisateur> tableTousLesUsers;
    @FXML private TableColumn<Utilisateur, String> colNomFull, colPrenomFull, colEmailFull, colRoleFull;

    private final CompteService compteService = new CompteService();
    private final CoursDAO coursDAO = new CoursDAO();
    private final ClasseDAO classeDAO = new ClasseDAO();
    private final SeanceDAO seanceDAO = new SeanceDAO();

    private List<Utilisateur> listeEnseignants;
    private List<Classe> listeClasses;

    @FXML
    /**
     * Initialise le tableau de bord du chef : configure les colonnes,
     * les listes déroulantes, les boutons et charge toutes les données.
     */
    public void initialize() {
        if (tableTousLesUsers != null) {
            setupUtilisateursTable();
            refreshUtilisateurs();
        } else if (totalSeancesLabel != null) {
            setupTableColumns();
            setupComboBoxes();
            refreshAll();
        } else if (pdfEnseignantCombo != null) {
            setupPdfCombo();
            if (pdfErrorLabel != null) {
                pdfErrorLabel.setVisible(false);
                pdfErrorLabel.setManaged(false);
            }
        }

        if (ajouterUtilisateurButton != null)
            ajouterUtilisateurButton.setOnAction(e -> handleAjouterUtilisateur());
        if (validerCompteButton != null)
            validerCompteButton.setOnAction(e -> {
                Utilisateur selected = comptesTable.getSelectionModel().getSelectedItem();
                if (selected != null) handleValiderCompte(selected.getId());
                else showNotification("Attention", "Sélectionnez un compte à valider.");
            });
        if (assignerCoursButton != null)
            assignerCoursButton.setOnAction(e -> handleAssignerCours());
        if (genererPdfButton != null)
            genererPdfButton.setOnAction(e -> handleGenererPdf());
    }

    private void setupPdfCombo() {
        try {
            listeEnseignants = compteService.getEnseignants();
            ObservableList<String> noms = FXCollections.observableArrayList();
            listeEnseignants.forEach(e -> noms.add(e.getPrenom() + " " + e.getNom()));
            pdfEnseignantCombo.setItems(noms);
        } catch (Exception e) {
            System.err.println("Erreur chargement enseignants PDF : " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colStatutCompte.setCellValueFactory(new PropertyValueFactory<>("statut"));

        colStatutCompte.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "VALIDE"     -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    case "EN_ATTENTE" -> setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    default           -> setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }
            }
        });
    }

    private void setupUtilisateursTable() {
        colNomFull.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenomFull.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmailFull.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRoleFull.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    private void setupComboBoxes() {
        try {
            listeEnseignants = compteService.getEnseignants();
            ObservableList<String> nomsEns = FXCollections.observableArrayList();
            listeEnseignants.forEach(e -> nomsEns.add(e.getPrenom() + " " + e.getNom()));

            if (enseignantCombo != null) enseignantCombo.setItems(nomsEns);
            if (pdfEnseignantCombo != null) pdfEnseignantCombo.setItems(nomsEns);

            if (classeCombo != null) {
                listeClasses = classeDAO.getToutesLesClasses();
                ObservableList<String> nomsCls = FXCollections.observableArrayList();
                listeClasses.forEach(c -> nomsCls.add(c.getNom()));
                classeCombo.setItems(nomsCls);
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement combos : " + e.getMessage());
        }
    }

    /**
     * Rafraîchit toutes les données affichées (statistiques, liste des utilisateurs,
     * listes déroulantes enseignants/classes).
     */
    public void refreshAll() {
        try {
            List<Utilisateur> enAttente = compteService.getComptesEnAttente();
            if (comptesTable != null)
                comptesTable.setItems(FXCollections.observableArrayList(enAttente));
            if (comptesAttenteLabel != null)
                comptesAttenteLabel.setText(enAttente.size() + " compte(s) en attente");

            List<Seance> seances = seanceDAO.getToutesLesSeances();
            int total = seances.size();
            long valides = seances.stream()
                    .filter(s -> s.getStatut() == StatutSeance.VALIDEE).count();

            if (totalSeancesLabel   != null) totalSeancesLabel.setText(String.valueOf(total));
            if (seancesValidesLabel != null) seancesValidesLabel.setText(String.valueOf(valides));
            if (tauxValidationLabel != null)
                tauxValidationLabel.setText(total > 0 ? (valides * 100 / total) + "%" : "0%");

            listeEnseignants = compteService.getEnseignants();
            if (enseignantsActifsLabel != null)
                enseignantsActifsLabel.setText(String.valueOf(listeEnseignants.size()));

            final ObservableList<String> nomsEns = FXCollections.observableArrayList();
            listeEnseignants.forEach(e -> nomsEns.add(e.getPrenom() + " " + e.getNom()));
            if (enseignantCombo    != null) enseignantCombo.setItems(nomsEns);
            if (pdfEnseignantCombo != null) pdfEnseignantCombo.setItems(nomsEns);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshUtilisateurs() {
        try {
            List<Utilisateur> actifs = compteService.getUtilisateursActifs();
            tableTousLesUsers.setItems(FXCollections.observableArrayList(actifs));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Active (valide) le compte d'un utilisateur identifié par son id.
     *
     * @param id Identifiant de l'utilisateur à valider
     */
    private void handleValiderCompte(int id) {
        try {
            compteService.validerCompte(id);
            refreshAll();
            showNotification("Succès", "Utilisateur activé !");
        } catch (Exception e) {
            showNotification("Erreur", "Échec de la validation.");
            e.printStackTrace();
        }
    }

    /**
     * Assigne un cours à l'enseignant sélectionné pour la classe choisie,
     * puis envoie une notification email à l'enseignant.
     */
    private void handleAssignerCours() {
        int eIdx = enseignantCombo.getSelectionModel().getSelectedIndex();
        int cIdx = classeCombo.getSelectionModel().getSelectedIndex();
        String matiere = nomCoursField.getText().trim();

        if (eIdx < 0 || cIdx < 0 || matiere.isEmpty()) {
            assignErrorLabel.setText("Veuillez remplir tous les champs.");
            assignErrorLabel.setVisible(true);
            assignErrorLabel.setManaged(true);
            return;
        }

        Cours c = new Cours(0, matiere, 0,
                listeEnseignants.get(eIdx).getId(),
                listeClasses.get(cIdx).getId());

        if (coursDAO.ajouter(c)) {
            // Notification email à l'enseignant — thread séparé pour ne pas bloquer l'UI
            final Utilisateur ens = listeEnseignants.get(eIdx);
            final String matiereFinale = matiere;
            new Thread(() -> EmailService.notifierAssignationCours(
                    ens.getEmail(), ens.getNom(), ens.getPrenom(), matiereFinale
            )).start();

            showNotification("Succès", "Cours \"" + matiere + "\" assigné !\n"
                    + "Un email a été envoyé à " + ens.getPrenom() + " " + ens.getNom());
            nomCoursField.clear();
            assignErrorLabel.setVisible(false);
            assignErrorLabel.setManaged(false);
            refreshAll();
        } else {
            assignErrorLabel.setText("Erreur lors de l'assignation.");
            assignErrorLabel.setVisible(true);
            assignErrorLabel.setManaged(true);
        }
    }

    /**
     * Génère et ouvre la fiche de suivi pédagogique PDF
     * pour l'enseignant sélectionné dans la liste déroulante PDF.
     */
    private void handleGenererPdf() {
        if (listeEnseignants == null || listeEnseignants.isEmpty()) {
            showNotification("Erreur", "Aucun enseignant disponible.");
            return;
        }

        int idx = pdfEnseignantCombo.getSelectionModel().getSelectedIndex();
        if (idx < 0) {
            if (pdfErrorLabel != null) {
                pdfErrorLabel.setText("Veuillez sélectionner un enseignant.");
                pdfErrorLabel.setVisible(true);
                pdfErrorLabel.setManaged(true);
            }
            return;
        }

        Utilisateur enseignant = listeEnseignants.get(idx);
        List<Seance> seancesEns = seanceDAO.getSeancesParEnseignant(enseignant.getId());
        long total    = seancesEns.size();
        long validees = seancesEns.stream().filter(s -> s.getStatut() == StatutSeance.VALIDEE).count();
        long attente  = seancesEns.stream().filter(s -> s.getStatut() == StatutSeance.EN_ATTENTE).count();
        long rejetees = seancesEns.stream().filter(s -> s.getStatut() == StatutSeance.REJETEE).count();
        double taux   = total > 0 ? (validees * 100.0 / total) : 0.0;

        String fileName = System.getProperty("user.dir") + "\\rapport_"
                + enseignant.getPrenom().toLowerCase() + "_"
                + enseignant.getNom().toLowerCase() + ".pdf";

        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Enregistrer le rapport PDF");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
            chooser.setInitialFileName(fileName.substring(fileName.lastIndexOf('\\') + 1));
            File destination = chooser.showSaveDialog(genererPdfButton.getScene().getWindow());
            if (destination == null) {
                return;
            }

            PdfGenerator.generateFicheSuivi(enseignant, seancesEns, destination.getAbsolutePath());

            if (pdfErrorLabel != null) {
                pdfErrorLabel.setVisible(false);
                pdfErrorLabel.setManaged(false);
            }
            showNotification("PDF généré", "Fichier créé : " + destination.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            showNotification("Erreur", "Impossible de générer le PDF : " + ex.getMessage());
        }
    }

    /**
     * Ouvre le formulaire de création manuelle d'un nouvel utilisateur
     * (enseignant ou responsable de classe) et l'enregistre directement
     * avec le statut {@code ACTIF}.
     */
    private void handleAjouterUtilisateur() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un nouvel utilisateur");
        dialog.setHeaderText("Saisissez les informations de l'utilisateur");

        TextField nomField     = new TextField(); nomField.setPromptText("Nom");
        TextField prenomField  = new TextField(); prenomField.setPromptText("Prénom");
        TextField emailField   = new TextField(); emailField.setPromptText("Email");
        PasswordField mdpField = new PasswordField(); mdpField.setPromptText("Mot de passe");
        ComboBox<String> roleCombo = new ComboBox<>(
                FXCollections.observableArrayList("ENSEIGNANT", "RESPONSABLE"));
        roleCombo.setValue("ENSEIGNANT");

        VBox content = new VBox(10,
            new Label("Nom:"), nomField,
            new Label("Prénom:"), prenomField,
            new Label("Email:"), emailField,
            new Label("Mot de passe:"), mdpField,
            new Label("Rôle:"), roleCombo
        );
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String nom    = nomField.getText().trim();
                String prenom = prenomField.getText().trim();
                String email  = emailField.getText().trim();
                String mdp    = mdpField.getText().trim();
                String role   = roleCombo.getValue();

                boolean success = role.equals("ENSEIGNANT")
                        ? compteService.creerEtValiderEnseignant(nom, prenom, email, mdp)
                        : compteService.creerEtValiderResponsable(nom, prenom, email, mdp);

                if (success) {
                    showNotification("Succès", "Utilisateur " + prenom + " " + nom + " ajouté !");
                    if (tableTousLesUsers != null) refreshUtilisateurs();
                    else refreshAll();
                } else {
                    showNotification("Erreur", "Échec de l'ajout. Email déjà utilisé ?");
                }
            }
        });
    }

    private void showNotification(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }
}