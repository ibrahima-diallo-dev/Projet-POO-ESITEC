package sn.esitec.poo.cahiertexte.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Représente une séance de cours saisie par un enseignant.
 * Une séance correspond à un cours effectivement réalisé, avec sa date,
 * son heure, sa durée (en minutes), le contenu enseigné et les observations.
 * Chaque séance possède un {@link StatutSeance} :
 * <ul>
 *   <li>EN_ATTENTE : valeur par défaut à la création</li>
 *   <li>VALIDEE : après validation par le responsable</li>
 *   <li>REJETEE : avec un commentaire de motif obligatoire</li>
 * </ul>
 * La modification et la suppression ne sont autorisées que si la séance
 * est encore EN_ATTENTE (voir {@link #estModifiable()}).
 */
public class Seance {

    private int id_seance;
    private LocalDate date_seance;
    private LocalTime heure_debut;
    private int duree;
    private String contenu;
    private String observations;
    private StatutSeance statut;          // ✅ String → StatutSeance
    private String commentaire_rejet;
    private int id_cours;
    private int id_enseignant;

    /**
     * Crée une séance avec toutes ses informations.
     *
     * @param id_seance          Identifiant unique de la séance en BDD
     * @param date_seance        Date à laquelle la séance a eu lieu
     * @param heure_debut        Heure de début de la séance
     * @param duree              Durée de la séance en minutes
     * @param contenu            Contenu enseigné durant la séance (obligatoire)
     * @param observations       Observations complémentaires (facultatif)
     * @param statut             Statut de la séance ({@link StatutSeance})
     * @param commentaire_rejet  Motif de rejet si la séance est REJETEE (sinon null)
     * @param id_cours           Identifiant du cours auquel appartient la séance
     * @param id_enseignant      Identifiant de l'enseignant ayant saisi la séance
     */
    public Seance(int id_seance, LocalDate date_seance, LocalTime heure_debut,
                  int duree, String contenu, String observations,
                  StatutSeance statut, String commentaire_rejet, int id_cours, int id_enseignant) {  // ✅
        this.id_seance = id_seance;
        this.date_seance = date_seance;
        this.heure_debut = heure_debut;
        this.duree = duree;
        this.contenu = contenu;
        this.observations = observations;
        this.statut = statut;
        this.commentaire_rejet = commentaire_rejet;
        this.id_cours = id_cours;
        this.id_enseignant = id_enseignant;
    }

    // Getters
    public int getId()                    { return id_seance; }
    public LocalDate getDateSeance()      { return date_seance; }
    public LocalTime getHeureDebut()      { return heure_debut; }
    public int getDuree()                 { return duree; }
    public String getContenu()            { return contenu; }
    public String getObservations()       { return observations; }
    public StatutSeance getStatut()       { return statut; }   // ✅
    public String getCommentaireRejet()   { return commentaire_rejet; }
    public int getCoursId()               { return id_cours; }
    public int getEnseignantId()          { return id_enseignant; }

    // Setters
    public void setStatut(StatutSeance statut)                { this.statut = statut; }  // ✅
    public void setCommentaireRejet(String commentaire_rejet) { this.commentaire_rejet = commentaire_rejet; }
    public void setContenu(String contenu)                    { this.contenu = contenu; }
    public void setObservations(String observations)          { this.observations = observations; }
    public void setDateSeance(LocalDate date_seance)          { this.date_seance = date_seance; }
    public void setHeureDebut(LocalTime heure_debut)          { this.heure_debut = heure_debut; }
    public void setDuree(int duree)                           { this.duree = duree; }
    public void setCoursId(int id_cours)                      { this.id_cours = id_cours; }

    /**
     * Indique si cette séance peut encore être modifiée ou supprimée.
     * Une séance ne peut être modifiée que si elle est encore EN_ATTENTE.
     * Une fois validée ou rejetée, elle est verrouillée.
     *
     * @return true si le statut est EN_ATTENTE, false sinon
     */
    public boolean estModifiable() {
        return StatutSeance.EN_ATTENTE.equals(this.statut);  // ✅
    }

    @Override
    public String toString() {
        return "Séance du " + date_seance + " à " + heure_debut + " — " + statut;
    }
}