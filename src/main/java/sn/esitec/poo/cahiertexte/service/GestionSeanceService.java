package sn.esitec.poo.cahiertexte.service;

import sn.esitec.poo.cahiertexte.dao.SeanceDAO;
import sn.esitec.poo.cahiertexte.dao.CoursDAO;
import sn.esitec.poo.cahiertexte.model.Seance;
import sn.esitec.poo.cahiertexte.model.Cours;
import java.util.List;

/**
 * Service métier pour la gestion des séances du cahier de texte.
 * Applique les règles métier avant de déléguer les opérations au DAO :
 * <ul>
 *   <li>Le contenu est obligatoire pour ajouter une séance</li>
 *   <li>La durée doit être strictement positive</li>
 *   <li>Une séance ne peut être modifiée que si elle est EN_ATTENTE</li>
 *   <li>Un commentaire est obligatoire pour le rejet</li>
 * </ul>
 * Ce service est utilisé par {@code EnseignantController}
 * et {@code ResponsableController}.
 */
public class GestionSeanceService {

    private SeanceDAO seanceDAO = new SeanceDAO();
    private CoursDAO coursDAO = new CoursDAO();

    /**
     * Ajoute une nouvelle séance après vérification des données.
     * Vérifie que le contenu n'est pas vide et que la durée est valide.
     *
     * @param s La séance à ajouter
     * @return true si l'ajout a réussi, false si validation métier ou SQL échouée
     */
    public boolean ajouterSeance(Seance s) {
        if (s.getContenu() == null || s.getContenu().isEmpty()) {
            System.out.println("Le contenu de la séance est obligatoire !");
            return false;
        }
        if (s.getDuree() <= 0) {
            System.out.println("La durée doit être supérieure à 0 !");
            return false;
        }
        return seanceDAO.ajouter(s);
    }

    /**
     * Modifie une séance existante si elle est encore EN_ATTENTE.
     * Délègue à {@link Seance#estModifiable()} la vérification du statut.
     *
     * @param s La séance avec les nouvelles valeurs (doit exister en BDD)
     * @return true si la modification a réussi, false si interdite ou erreur SQL
     */
    public boolean modifierSeance(Seance s) {
        if (!s.estModifiable()) {
            System.out.println("Cette séance ne peut plus être modifiée !");
            return false;
        }
        return seanceDAO.modifier(s);
    }

    /**
     * Valide une séance : passe son statut à VALIDEE.
     * La notification email est gérée dans le contrôleur appelant.
     *
     * @param id_seance Identifiant de la séance à valider
     */
    public void validerSeance(int id_seance) {
        seanceDAO.valider(id_seance);
    }

    /**
     * Rejette une séance avec un motif obligatoire.
     * Le commentaire doit être non vide, sinon l'opération est ignorée.
     *
     * @param id_seance  Identifiant de la séance à rejeter
     * @param commentaire Motif du rejet (obligatoire, non vide)
     */
    public void rejeterSeance(int id_seance, String commentaire) {
        if (commentaire == null || commentaire.isEmpty()) {
            System.out.println("Un commentaire est obligatoire lors d'un rejet !");
            return;
        }
        seanceDAO.rejeter(id_seance, commentaire);
    }

    // 📋 Séances d'un enseignant
    public List<Seance> getSeancesEnseignant(int id_enseignant) {
        return seanceDAO.getSeancesParEnseignant(id_enseignant);
    }

    // 📋 Séances en attente pour un cours
    public List<Seance> getSeancesEnAttente(int id_cours) {
        return seanceDAO.getSeancesEnAttente(id_cours);
    }

    /**
     * Supprime une séance de la BDD (uniquement si EN_ATTENTE).
     *
     * @param id_seance Identifiant de la séance à supprimer
     * @return true si la suppression a été effectuée, false sinon
     */
    public boolean supprimerSeance(int id_seance) {
        return seanceDAO.supprimer(id_seance);
    }

    // 📋 NOUVEAU : Toutes les séances d'une classe (pour Responsable)
    public List<Seance> getSeancesParClasse(int id_classe) {
        return seanceDAO.getSeancesParClasse(id_classe);
    }

    // 📋 Cours d'un enseignant
    public List<Cours> getCoursDeLEnseignant(int id_enseignant) {
        return coursDAO.getCoursDeLEnseignant(id_enseignant);
    }
}