package sn.esitec.poo.cahiertexte.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sn.esitec.poo.cahiertexte.dao.ClasseDAO;
import sn.esitec.poo.cahiertexte.dao.CoursDAO;
import sn.esitec.poo.cahiertexte.dao.UtilisateurDAO;
import sn.esitec.poo.cahiertexte.model.Classe;
import sn.esitec.poo.cahiertexte.model.Cours;
import sn.esitec.poo.cahiertexte.model.Seance;
import sn.esitec.poo.cahiertexte.model.StatutSeance;
import sn.esitec.poo.cahiertexte.model.Utilisateur;
import sn.esitec.poo.cahiertexte.service.EmailService;
import sn.esitec.poo.cahiertexte.service.GestionSeanceService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Contrôleur du formulaire pop-up d'ajout/modification de séance ({@code seance-form.fxml}).
 * <p>
 * Utilisé par {@link EnseignantController} pour ouvrir une fenêtre modale.
 * En mode édition ({@link #setSeanceToEdit}), les champs sont pré-remplis.
 * À la confirmation, la séance est persistée et une notification email est
 * envoyée au responsable de classe.
 * </p>
 */
public class SeanceFormController {

    @FXML private DatePicker dateField;
    @FXML private TextField heureField, dureeField;
    @FXML private TextArea contenuArea, observationsArea;
    @FXML private ComboBox<String> coursCombo;
    @FXML private Label errorLabel, formTitleLabel;
    @FXML private Button confirmSeanceButton, cancelSeanceButton;

    private final GestionSeanceService seanceService = new GestionSeanceService();
    private final CoursDAO coursDAO = new CoursDAO();
    private final ClasseDAO classeDAO = new ClasseDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private Utilisateur enseignant;
    private Seance seanceToEdit = null;
    private List<Cours> coursList;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // Limitation de la date (Pas de futur, max 30 jours en arrière)
        dateField.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()) || date.isBefore(LocalDate.now().minusDays(30)));
            }
        });

        confirmSeanceButton.setOnAction(e -> handleConfirm());
        cancelSeanceButton.setOnAction(e -> closePopup());
    }

    /**
     * Définit l'enseignant pour lequel la séance est saisie et charge
     * automatiquement la liste des cours qui lui sont assignés.
     *
     * @param enseignant Utilisateur (enseignant) propriétaire de la séance
     */
    public void setEnseignant(Utilisateur enseignant) {
        this.enseignant = enseignant;
        loadCours();
    }

    /**
     * Active le mode édition en pré-remplissant tous les champs du formulaire
     * avec les données de la séance à modifier.
     *
     * @param seance Séance à modifier ({@code null} pour une nouvelle séance)
     */
    public void setSeanceToEdit(Seance seance) {
        this.seanceToEdit = seance;
        if (formTitleLabel != null) formTitleLabel.setText("Modifier la séance");

        dateField.setValue(seance.getDateSeance());
        heureField.setText(seance.getHeureDebut().toString());
        dureeField.setText(String.valueOf(seance.getDuree()));
        contenuArea.setText(seance.getContenu());
        observationsArea.setText(seance.getObservations());

        if (coursList != null) {
            for (int i = 0; i < coursList.size(); i++) {
                if (coursList.get(i).getId() == seance.getCoursId()) {
                    coursCombo.getSelectionModel().select(i);
                    break;
                }
            }
        }
    }

    private void loadCours() {
        coursList = coursDAO.getCoursDeLEnseignant(enseignant.getId());
        coursCombo.setItems(FXCollections.observableArrayList(coursList.stream().map(Cours::getIntitule).toList()));
        if (!coursList.isEmpty() && seanceToEdit == null) coursCombo.getSelectionModel().selectFirst();
    }

    /**
     * Valide le formulaire et persiste la séance (ajout ou modification).
     * En cas de succès, ferme le pop-up et notifie le responsable par email.
     */
    private void handleConfirm() {
        if (!validateForm()) return;

        try {
            int idx = coursCombo.getSelectionModel().getSelectedIndex();
            int idCours = coursList.get(idx).getId();

            if (seanceToEdit != null) {
                // MODIFICATION COMPLETE
                seanceToEdit.setDateSeance(dateField.getValue());
                seanceToEdit.setHeureDebut(LocalTime.parse(heureField.getText()));
                seanceToEdit.setDuree(Integer.parseInt(dureeField.getText()));
                seanceToEdit.setContenu(contenuArea.getText());
                seanceToEdit.setObservations(observationsArea.getText());
                seanceToEdit.setCoursId(idCours);
                seanceService.modifierSeance(seanceToEdit);
            } else {
                // AJOUT
                Seance n = new Seance(0, dateField.getValue(), LocalTime.parse(heureField.getText()), 
                           Integer.parseInt(dureeField.getText()), contenuArea.getText(), 
                           observationsArea.getText(), StatutSeance.EN_ATTENTE, null, idCours, enseignant.getId());
                seanceService.ajouterSeance(n);

                // Notification email au responsable pour validation (asynchrone)
                new Thread(() -> notifierResponsablePourValidation(n, idCours)).start();
            }
            closePopup();
        } catch (Exception e) { showError("Erreur : " + e.getMessage()); }
    }

    /**
     * Recherche le responsable de la classe concernée par le cours et lui envoie
     * une notification email pour lui signaler qu'une séance attend sa validation.
     *
     * @param seance  Séance nouvellement créée ou modifiée
     * @param idCours Identifiant du cours associé (pour retrouver la classe)
     */
    private void notifierResponsablePourValidation(Seance seance, int idCours) {
        try {
            Cours cours = coursDAO.getById(idCours);
            if (cours == null) return;

            Classe classe = classeDAO.getById(cours.getClasseId());
            if (classe == null) return;

            Utilisateur responsable = utilisateurDAO.getById(classe.getResponsableId());
            if (responsable == null || responsable.getEmail() == null || responsable.getEmail().isBlank()) return;

            EmailService.notifierDemandeValidationResponsable(
                    responsable.getEmail(),
                    responsable.getNom(),
                    responsable.getPrenom(),
                    enseignant.getNom(),
                    enseignant.getPrenom(),
                    cours.getIntitule(),
                    seance.getDateSeance().toString(),
                    seance.getContenu()
            );
        } catch (Exception e) {
            System.err.println("⚠️ Notification responsable non envoyée: " + e.getMessage());
        }
    }

    private boolean validateForm() {
        if (dateField.getValue() == null) return showError("Date obligatoire");
        if (!heureField.getText().matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) return showError("Heure invalide (HH:mm)");
        try {
            int d = Integer.parseInt(dureeField.getText());
            if (d <= 0 || d > 480) return showError("Durée entre 1 et 480 min");
        } catch (Exception e) { return showError("Durée doit être un nombre"); }
        if (contenuArea.getText().trim().length() < 10) return showError("Contenu trop court");
        if (coursCombo.getSelectionModel().isEmpty()) return showError("Sélectionnez un cours");
        return true;
    }

    private boolean showError(String msg) {
        errorLabel.setText(msg); errorLabel.setVisible(true); errorLabel.setManaged(true);
        return false;
    }

    private void closePopup() { ((Stage) cancelSeanceButton.getScene().getWindow()).close(); }
}