package sn.esitec.poo.cahiertexte.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sn.esitec.poo.cahiertexte.model.Cours;
import sn.esitec.poo.cahiertexte.model.Seance;
import sn.esitec.poo.cahiertexte.model.StatutSeance;
import sn.esitec.poo.cahiertexte.model.Utilisateur;
import sn.esitec.poo.cahiertexte.dao.ClasseDAO;
import sn.esitec.poo.cahiertexte.service.GestionSeanceService;
import sn.esitec.poo.cahiertexte.utils.PdfGenerator;
import sn.esitec.poo.cahiertexte.utils.SessionManager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contrôleur du tableau de bord enseignant ({@code enseignant.fxml}).
 * <p>
 * Permet à l'enseignant de :
 * <ul>
 *   <li>Consulter ses séances avec filtre par statut</li>
 *   <li>Ajouter, modifier et supprimer des séances</li>
 *   <li>Visualiser ses cours assignés et les statistiques d'avancement</li>
 *   <li>Générer sa fiche de suivi pédagogique au format PDF</li>
 * </ul>
 * </p>
 */
public class EnseignantController {

    @FXML private TabPane mainTabPane;
    @FXML private TableView<Seance> seancesTable;
    @FXML private TableColumn<Seance, LocalDate> colDate;
    @FXML private TableColumn<Seance, String> colHeure;
    @FXML private TableColumn<Seance, Integer> colDuree;
    @FXML private TableColumn<Seance, String> colContenu;
    @FXML private TableColumn<Seance, StatutSeance> colStatut;
    @FXML private Label totalHoursLabel;
    @FXML private Button addSeanceButton;
    @FXML private Button modifierSeanceButton;
    @FXML private Button supprimerSeanceButton;
    @FXML private TextField filterSeancesField;
    @FXML private Button generatePdfButton;

    @FXML private TableView<Cours> coursTable;
    @FXML private TableColumn<Cours, String> colCoursNom;
    @FXML private TableColumn<Cours, String> colCoursClasse;
    @FXML private TableColumn<Cours, Integer> colCoursNbSeances;

    @FXML private Label totalHeuresLabel;
    @FXML private Label seancesValideesLabel;
    @FXML private Label seancesAttenteLabel;

    private final GestionSeanceService seanceService = new GestionSeanceService();
    private final ClasseDAO classeDAO = new ClasseDAO();
    private ObservableList<Seance> seanceList = FXCollections.observableArrayList();
    private FilteredList<Seance> filteredSeances;
    private final Map<Integer, Long> seancesParCours = new HashMap<>();
    private final Map<Integer, String> classesParId = new HashMap<>();
    private Utilisateur currentUser;

    /**
     * Sélectionne programmatiquement un onglet du {@code TabPane} principal.
     *
     * @param tabIndex Index de l'onglet à activer (0-basé)
     */
    public void selectTab(int tabIndex) {
        if (mainTabPane != null && tabIndex >= 0 && tabIndex < mainTabPane.getTabs().size()) {
            mainTabPane.getSelectionModel().select(tabIndex);
        }
    }

    @FXML
    /**
     * Initialise le tableau de bord enseignant : configure les colonnes,
     * les filtres et charge les données de séances et de cours.
     */
    public void initialize() {
        currentUser = SessionManager.getInstance().getUtilisateurConnecte();
        setupSeancesTable();
        setupCoursTable();

        filteredSeances = new FilteredList<>(seanceList, s -> true);
        seancesTable.setItems(filteredSeances);

        // Filtrage dynamique
        filterSeancesField.textProperty().addListener((obs, o, n) -> {
            String filter = (n == null) ? "" : n.toLowerCase();
            filteredSeances.setPredicate(s -> {
                if (filter.isEmpty()) return true;
                return (s.getContenu() != null && s.getContenu().toLowerCase().contains(filter))
                        || (s.getDateSeance() != null && s.getDateSeance().toString().contains(filter));
            });
        });

        loadSeancesData();
        loadCoursData();

        // Gestion de l'activation des boutons selon le statut
        seancesTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            boolean mutable = n != null && n.getStatut() == StatutSeance.EN_ATTENTE;
            modifierSeanceButton.setDisable(!mutable);
            if (supprimerSeanceButton != null) supprimerSeanceButton.setDisable(!mutable);
        });
        
        // Double-clic pour modifier
        seancesTable.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) openModifierSeancePopup();
        });
    }

    private void setupSeancesTable() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateSeance"));
        colHeure.setCellValueFactory(new PropertyValueFactory<>("heureDebut"));
        colDuree.setCellValueFactory(new PropertyValueFactory<>("duree"));
        colContenu.setCellValueFactory(new PropertyValueFactory<>("contenu"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        colStatut.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(StatutSeance item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); } 
                else {
                    setText(item.toString());
                    switch (item) {
                        case VALIDEE -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                        case EN_ATTENTE -> setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                        case REJETEE -> setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void setupCoursTable() {
        colCoursNom.setCellValueFactory(new PropertyValueFactory<>("intitule"));
        colCoursClasse.setCellValueFactory(cell -> {
            Cours cours = cell.getValue();
            String nomClasse = classesParId.get(cours.getClasseId());
            if (nomClasse == null || nomClasse.isBlank()) {
                nomClasse = "Classe " + cours.getClasseId();
            }
            return new ReadOnlyObjectWrapper<>(nomClasse);
        });
        colCoursNbSeances.setCellValueFactory(cell -> {
            Cours cours = cell.getValue();
            int nbSeances = seancesParCours.getOrDefault(cours.getId(), 0L).intValue();
            return new ReadOnlyObjectWrapper<>(nbSeances);
        });
    }

    /** Charge (ou recharge) la liste des séances de l'enseignant depuis la base de données. */
    private void loadSeancesData() {
        if (currentUser == null) return;
        List<Seance> data = seanceService.getSeancesEnseignant(currentUser.getId());
        seanceList.setAll(data);
        seancesParCours.clear();
        seancesParCours.putAll(data.stream()
                .collect(Collectors.groupingBy(Seance::getCoursId, Collectors.counting())));
        if (coursTable != null) {
            coursTable.refresh();
        }
        updateSuiviStats(data);
    }

    private void loadCoursData() {
        if (currentUser == null) return;
        classesParId.clear();
        classeDAO.getToutesLesClasses().forEach(classe -> classesParId.put(classe.getId(), classe.getNom()));
        List<Cours> data = seanceService.getCoursDeLEnseignant(currentUser.getId());
        coursTable.setItems(FXCollections.observableArrayList(data));
    }

    private void updateSuiviStats(List<Seance> list) {
        int totalMins = list.stream().filter(s -> s.getStatut() == StatutSeance.VALIDEE).mapToInt(Seance::getDuree).sum();
        long validees = list.stream().filter(s -> s.getStatut() == StatutSeance.VALIDEE).count();
        long attente = list.stream().filter(s -> s.getStatut() == StatutSeance.EN_ATTENTE).count();

        String hoursStr = (totalMins / 60) + "h " + (totalMins % 60) + "min";
        if (totalHoursLabel != null) totalHoursLabel.setText(hoursStr);
        if (totalHeuresLabel != null) totalHeuresLabel.setText(hoursStr);
        if (seancesValideesLabel != null) seancesValideesLabel.setText(String.valueOf(validees));
        if (seancesAttenteLabel != null) seancesAttenteLabel.setText(String.valueOf(attente));
    }

    @FXML
    /** Ouvre la fenêtre pop-up de saisie d'une nouvelle séance. */
    private void openAddSeancePopup() {
        showPopup(null);
    }

    @FXML
    /**
     * Ouvre la fenêtre pop-up de modification pour la séance sélectionnée.
     * Affiche un avertissement si aucune séance n'est sélectionnée ou si
     * la séance n'est plus modifiable ({@code VALIDEE}).
     */
    private void openModifierSeancePopup() {
        Seance selected = seancesTable.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getStatut() == StatutSeance.EN_ATTENTE) {
            showPopup(selected);
        }
    }

    private void showPopup(Seance seance) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/seance-form.fxml"));
            Parent root = loader.load();
            SeanceFormController ctrl = loader.getController();
            ctrl.setEnseignant(currentUser);
            if (seance != null) ctrl.setSeanceToEdit(seance);

            Stage popup = new Stage(StageStyle.UNDECORATED);
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setScene(new Scene(root));
            popup.showAndWait();
            loadSeancesData();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    /**
     * Génère la fiche de suivi PDF de l'enseignant connecté et l'ouvre
     * avec l'application par défaut du système.
     */
    private void handleGeneratePdf() {
        try {
            PdfGenerator.generateFicheSuivi(currentUser, seanceList);
            new Alert(Alert.AlertType.INFORMATION, "PDF généré !").showAndWait();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    /**
     * Supprime la séance sélectionnée après confirmation de l'utilisateur.
     * Seules les séances en statut {@code EN_ATTENTE} ou {@code REJETEE} peuvent
     * être supprimées.
     */
    private void handleSupprimerSeance() {
        Seance selected = seancesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Sélectionnez une séance").showAndWait();
            return;
        }
        if (selected.getStatut() != StatutSeance.EN_ATTENTE) {
            new Alert(Alert.AlertType.WARNING, "Seule les séances en attente peuvent être supprimées").showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Confirmer la suppression ?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            seanceService.supprimerSeance(selected.getId());
            loadSeancesData();
            new Alert(Alert.AlertType.INFORMATION, "Séance supprimée").showAndWait();
        }
    }
}