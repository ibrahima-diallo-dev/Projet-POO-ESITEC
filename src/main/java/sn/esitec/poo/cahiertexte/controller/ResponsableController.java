package sn.esitec.poo.cahiertexte.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sn.esitec.poo.cahiertexte.model.Classe;
import sn.esitec.poo.cahiertexte.model.Seance;
import sn.esitec.poo.cahiertexte.model.StatutSeance;
import sn.esitec.poo.cahiertexte.dao.ClasseDAO;
import sn.esitec.poo.cahiertexte.service.GestionSeanceService;
import sn.esitec.poo.cahiertexte.utils.SessionManager;

import java.io.IOException;
import java.util.List;

/**
 * Contrôleur du tableau de bord du Responsable de Classe ({@code responsable.fxml}).
 * <p>
 * Permet au responsable de :
 * <ul>
 *   <li>Consulter le cahier de texte de sa classe avec filtrage par statut</li>
 *   <li>Valider ou rejeter des séances en attente (avec email automatique)</li>
 *   <li>Consulter l'historique complet et les statistiques d'avancement</li>
 * </ul>
 * </p>
 */
public class ResponsableController {

    @FXML private TabPane mainTabPane;
    @FXML private TableView<Seance> cahierTable;
    @FXML private TableColumn<Seance, String> colEnseignant, colMatiere,
            colDate, colContenu, colStatut, colMotifRejet, colActions;
        @FXML private TableView<Seance> historiqueTable;
        @FXML private TableColumn<Seance, String> histColDate, histColEnseignant, histColMatiere, histColStatut, histColMotif;
    @FXML private ComboBox<String> filtreStatut;
    @FXML private Label selectionLabel;
    @FXML private Button validerButton, rejeterButton;
    @FXML private Label seancesPrevuesLabel;
    @FXML private Label seancesRealiseeLabel;
    @FXML private Label progressionLabel;
    @FXML private ProgressBar progressionBar;
        @FXML private Label statTotalLabel;
        @FXML private Label statValideesLabel;
        @FXML private Label statAttenteLabel;
        @FXML private Label statRejeteesLabel;
        @FXML private Label statTauxLabel;

    private final GestionSeanceService seanceService = new GestionSeanceService();
    private final ClasseDAO classeDAO = new ClasseDAO();
    private final sn.esitec.poo.cahiertexte.dao.UtilisateurDAO utilisateurDAO = new sn.esitec.poo.cahiertexte.dao.UtilisateurDAO();
    private final sn.esitec.poo.cahiertexte.dao.CoursDAO coursDAO = new sn.esitec.poo.cahiertexte.dao.CoursDAO();
    private ObservableList<Seance> masterData = FXCollections.observableArrayList();
    private FilteredList<Seance> filteredData;
    private int idClasse = 1;

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
     * Initialise le tableau de bord responsable : configure le tableau
     * des séances avec actions de validation/rejet en ligne, le filtre par
     * statut, l'historique et les statistiques.
     */
    public void initialize() {
        // Récupérer la classe du responsable connecté
        var user = SessionManager.getInstance().getUtilisateurConnecte();
        if (user != null) {
            System.out.println("🔍 [Responsable] User: " + user.getEmail() + ", ID: " + user.getId());
            Classe classe = classeDAO.getClasseParResponsable(user.getId());
            if (classe != null) {
                idClasse = classe.getId();
                System.out.println("✅ [Responsable] Classe trouvée: " + classe.getNom() + " (ID: " + idClasse + ")");
            } else {
                System.out.println("❌ [Responsable] Aucune classe assignée pour l'utilisateur!");
                showAlert("⚠️ Erreur", "Aucune classe ne vous est assignée. Veuillez contacter le chef.");
                return;
            }
        }

        setupTable();
        setupHistoriqueTable();
        setupFiltering();
        refreshTable();

        validerButton.setOnAction(e -> handleBulkValidation());
        rejeterButton.setOnAction(e -> {
            System.out.println("🔘 [Responsable] Bouton Rejeter cliqué");
            ObservableList<Seance> selected = cahierTable.getSelectionModel().getSelectedItems();
            System.out.println("📊 [Responsable] Séances sélectionnées: " + selected.size());
            if (!selected.isEmpty()) {
                int idSeance = selected.get(0).getId();
                System.out.println("✓ [Responsable] Ouverture rejet popup pour séance ID: " + idSeance);
                openRejetPopup(idSeance);
            } else {
                System.out.println("❌ [Responsable] Aucune séance sélectionnée");
                showAlert("⚠️ Attention", "Veuillez sélectionner une séance à rejeter.");
            }
        });
    }

    private void setupTable() {
        cahierTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        cahierTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            int count = cahierTable.getSelectionModel().getSelectedItems().size();
            selectionLabel.setText(count + " séance(s) sélectionnée(s)");
        });

        colDate.setCellValueFactory(cellData ->
    new javafx.beans.property.SimpleStringProperty(
        cellData.getValue().getDateSeance().toString()
    )
);
        colContenu.setCellValueFactory(cellData ->
    new javafx.beans.property.SimpleStringProperty(
        cellData.getValue().getContenu()
    )
);
        colStatut.setCellValueFactory(cellData -> 
    new javafx.beans.property.SimpleStringProperty(
        cellData.getValue().getStatut().toString()
    )
);

        // Colonne Enseignant — affiche le nom réel
        colEnseignant.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setText(null); return; }
                Seance s = getTableView().getItems().get(getIndex());
                try {
                    var enseignant = utilisateurDAO.getById(s.getEnseignantId());
                    if (enseignant != null) {
                        setText(enseignant.getPrenom() + " " + enseignant.getNom());
                    } else {
                        setText("Enseignant #" + s.getEnseignantId());
                    }
                } catch (Exception e) {
                    setText("Enseignant #" + s.getEnseignantId());
                }
            }
        });

        // Colonne Matière — affiche le nom réel du cours
        colMatiere.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setText(null); return; }
                Seance s = getTableView().getItems().get(getIndex());
                try {
                    var cours = coursDAO.getById(s.getCoursId());
                    if (cours != null) {
                        setText(cours.getIntitule());
                    } else {
                        setText("Cours #" + s.getCoursId());
                    }
                } catch (Exception e) {
                    setText("Cours #" + s.getCoursId());
                }
            }
        });

        // Coloration statut
        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("statut-valide", "statut-attente", "statut-rejete");
                if (empty || item == null) { setText(null); return; }
                setText(item);
                switch (item) {
                    case "VALIDEE"    -> getStyleClass().add("statut-valide");
                    case "EN_ATTENTE" -> getStyleClass().add("statut-attente");
                    case "REJETEE"    -> getStyleClass().add("statut-rejete");
                }
            }
        });

        // Motif du rejet
        colMotifRejet.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setText(null); return; }
                Seance s = getTableView().getItems().get(getIndex());
                String motif = s.getCommentaireRejet();
                if (motif != null && !motif.isEmpty()) {
                    setText(motif);
                } else {
                    setText("-");
                }
            }
        });

        // Boutons inline
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnV = new Button("✓");
            private final Button btnR = new Button("✗");
            private final HBox box = new HBox(8, btnV, btnR);
            {
                btnV.getStyleClass().add("primary-button");
                btnR.getStyleClass().add("danger-button");
                btnV.setOnAction(e -> handleSingleValidation(
                        getTableView().getItems().get(getIndex()).getId()));
                btnR.setOnAction(e -> openRejetPopup(
                        getTableView().getItems().get(getIndex()).getId()));
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void setupFiltering() {
        filteredData = new FilteredList<>(masterData, p -> true);
        filtreStatut.getSelectionModel().selectedItemProperty().addListener((obs, o, n) ->
            filteredData.setPredicate(s -> {
                if (n == null || n.equals("Tous")) return true;
                return s.getStatut().toString().equals(
                    n.toUpperCase().replace(" ", "_")
                     .replace("VALIDÉS", "VALIDEE")
                     .replace("REJETÉS", "REJETEE")
                );
            })
        );
        cahierTable.setItems(filteredData);
    }

    /**
     * Recharge les données du cahier de texte depuis la base de données
     * et met à jour l'affichage (tableau, historique, statistiques).
     */
    public void refreshTable() {
        try {
            System.out.println("🔄 [Responsable] Rafraîchissement pour la classe ID: " + idClasse);
            List<Seance> list = seanceService.getSeancesParClasse(idClasse);
            System.out.println("📊 [Responsable] " + list.size() + " séance(s) trouvée(s)");
            masterData.setAll(list);
            updateProgressCards(list);
            updateHistorique(list);
            updateStatsTab(list);
        } catch (Exception e) { 
            System.err.println("❌ [Responsable] Erreur lors du rafraîchissement: " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    private void setupHistoriqueTable() {
        if (historiqueTable == null) return;

        histColDate.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDateSeance().toString()));

        histColEnseignant.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setText(null); return; }
                Seance s = getTableView().getItems().get(getIndex());
                try {
                    var enseignant = utilisateurDAO.getById(s.getEnseignantId());
                    setText(enseignant != null
                            ? enseignant.getPrenom() + " " + enseignant.getNom()
                            : "Enseignant #" + s.getEnseignantId());
                } catch (Exception e) {
                    setText("Enseignant #" + s.getEnseignantId());
                }
            }
        });

        histColMatiere.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setText(null); return; }
                Seance s = getTableView().getItems().get(getIndex());
                try {
                    var cours = coursDAO.getById(s.getCoursId());
                    setText(cours != null ? cours.getIntitule() : "Cours #" + s.getCoursId());
                } catch (Exception e) {
                    setText("Cours #" + s.getCoursId());
                }
            }
        });

        histColStatut.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatut().toString()));

        histColMotif.setCellValueFactory(cellData -> {
            String motif = cellData.getValue().getCommentaireRejet();
            return new javafx.beans.property.SimpleStringProperty((motif == null || motif.isBlank()) ? "-" : motif);
        });
    }

    private void updateHistorique(List<Seance> list) {
        if (historiqueTable != null) {
            historiqueTable.setItems(FXCollections.observableArrayList(list));
        }
    }

    private void updateStatsTab(List<Seance> list) {
        long total = list.size();
        long validees = list.stream().filter(s -> s.getStatut() == StatutSeance.VALIDEE).count();
        long attente = list.stream().filter(s -> s.getStatut() == StatutSeance.EN_ATTENTE).count();
        long rejetees = list.stream().filter(s -> s.getStatut() == StatutSeance.REJETEE).count();
        double taux = total > 0 ? (validees * 100.0 / total) : 0.0;

        if (statTotalLabel != null) statTotalLabel.setText(String.valueOf(total));
        if (statValideesLabel != null) statValideesLabel.setText(String.valueOf(validees));
        if (statAttenteLabel != null) statAttenteLabel.setText(String.valueOf(attente));
        if (statRejeteesLabel != null) statRejeteesLabel.setText(String.valueOf(rejetees));
        if (statTauxLabel != null) statTauxLabel.setText(String.format("%.1f%%", taux));
    }

    private void updateProgressCards(List<Seance> list) {
        int total = list.size();
        long validees = list.stream()
                .filter(s -> s.getStatut() == StatutSeance.VALIDEE).count();
        double progression = total > 0 ? (double) validees / total : 0;

        if (seancesPrevuesLabel  != null) seancesPrevuesLabel.setText(String.valueOf(total));
        if (seancesRealiseeLabel != null) seancesRealiseeLabel.setText(String.valueOf(validees));
        if (progressionLabel     != null) progressionLabel.setText(Math.round(progression * 100) + "%");
        if (progressionBar       != null) progressionBar.setProgress(progression);
    }

    private void handleSingleValidation(int id) {
        try {
            // Récupérer la séance pour notifier l'enseignant
            Seance seance = masterData.stream()
                    .filter(s -> s.getId() == id).findFirst().orElse(null);

            seanceService.validerSeance(id);

            // Notification email
            if (seance != null) {
                // TODO : récupérer email enseignant et décommenter
                // Utilisateur ens = utilisateurDAO.getById(seance.getEnseignantId());
                // EmailService.notifierValidation(ens.getEmail(), ens.getNom(), seance.getContenu());
            }

            refreshTable();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleBulkValidation() {
        cahierTable.getSelectionModel().getSelectedItems()
                .forEach(s -> handleSingleValidation(s.getId()));
    }

    private void openRejetPopup(int idSeance) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/rejet-form.fxml"));
            Parent root = loader.load();
            RejetFormController ctrl = loader.getController();

            Seance seance = masterData.stream()
                    .filter(s -> s.getId() == idSeance)
                    .findFirst().orElse(null);

            if (seance != null) {
                ctrl.setSeance(seance);
            } else {
                ctrl.setIdSeance(idSeance);
            }

            ctrl.setParentController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Motif du rejet de la séance");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.setTitle(title);
        alert.showAndWait();
    }
}