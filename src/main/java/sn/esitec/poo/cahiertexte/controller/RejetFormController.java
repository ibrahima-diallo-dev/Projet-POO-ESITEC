package sn.esitec.poo.cahiertexte.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import sn.esitec.poo.cahiertexte.dao.UtilisateurDAO;
import sn.esitec.poo.cahiertexte.model.Seance;
import sn.esitec.poo.cahiertexte.model.Utilisateur;
import sn.esitec.poo.cahiertexte.service.EmailService;
import sn.esitec.poo.cahiertexte.service.GestionSeanceService;

/**
 * Contrôleur du formulaire pop-up de rejet de séance ({@code rejet-form.fxml}).
 * <p>
 * Utilisé par {@link ResponsableController} pour saisir le motif de rejet.
 * Valide le formulaire (minimum 10 caractères), appelle
 * {@link sn.esitec.poo.cahiertexte.service.GestionSeanceService#rejeterSeance}
 * et envoie un email asynchrone à l'enseignant via
 * {@link sn.esitec.poo.cahiertexte.service.EmailService}.
 * </p>
 */
public class RejetFormController {

    @FXML private TextArea motifArea;
    @FXML public Label errorLabel;
    @FXML private Button confirmRejetButton, cancelRejetButton;

    private int idSeance;
    private Seance seance;
    private ResponsableController parentController;
    private final GestionSeanceService seanceService = new GestionSeanceService();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    /**
     * Définit l'identifiant de la séance à rejeter.
     *
     * @param id Identifiant de la séance
     */
    public void setIdSeance(int id) { this.idSeance = id; }

    /**
     * Définit la séance complète à rejeter (permet de récupérer
     * l'email de l'enseignant pour la notification).
     *
     * @param s Séance concernée par le rejet
     */
    public void setSeance(Seance s) {
        this.seance = s;
        this.idSeance = s.getId();
    }

    /**
     * Référence le contrôleur parent afin de rafraîchir le tableau après le rejet.
     *
     * @param pc Instance du {@link ResponsableController} ayant ouvert ce pop-up
     */
    public void setParentController(ResponsableController pc) { this.parentController = pc; }

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        cancelRejetButton.setOnAction(e -> close());
        confirmRejetButton.setOnAction(e -> handleConfirm());
    }

    private void handleConfirm() {
        String motif = motifArea.getText().trim();

        if (motif.isEmpty()) {
            showError("Le motif du rejet est obligatoire.");
            return;
        }
        if (motif.length() < 10) {
            showError("Le motif doit faire au moins 10 caractères.");
            return;
        }

        confirmRejetButton.setDisable(true);
        confirmRejetButton.setText("En cours...");

        final String motifFinal = motif;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // 1. Enregistrer le rejet en BDD
                seanceService.rejeterSeance(idSeance, motifFinal);

                // 2. Envoyer l'email à l'enseignant
                if (seance != null) {
                    Utilisateur ens = utilisateurDAO.getById(seance.getEnseignantId());
                    if (ens != null) {
                        EmailService.notifierRejet(
                            ens.getEmail(),
                            ens.getNom(),
                            seance.getContenu(),
                            motifFinal
                        );
                        System.out.println("✅ Email rejet envoyé à : " + ens.getEmail());
                    } else {
                        System.out.println("⚠️ Enseignant introuvable pour id: " 
                            + seance.getEnseignantId());
                    }
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            if (parentController != null) parentController.refreshTable();
            close();
        });

        task.setOnFailed(e -> {
            confirmRejetButton.setDisable(false);
            confirmRejetButton.setText("Confirmer le rejet");
            showError("Erreur : " + task.getException().getMessage());
            task.getException().printStackTrace();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void close() {
        ((Stage) cancelRejetButton.getScene().getWindow()).close();
    }
}