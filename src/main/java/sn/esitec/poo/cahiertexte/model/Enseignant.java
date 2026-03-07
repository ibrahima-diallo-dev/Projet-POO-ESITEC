package sn.esitec.poo.cahiertexte.model;

import java.time.LocalDateTime;

/**
 * Représente un enseignant dans le système.
 * Un enseignant peut :
 * - Saisir, modifier et supprimer ses séances (si statut EN_ATTENTE)
 * - Consulter ses cours assignés par le chef de département
 * - Générer sa fiche de suivi pédagogique en PDF
 * Son rôle est automatiquement fixé à {@link Role#ENSEIGNANT}.
 */
public class Enseignant extends Utilisateur {

    /**
     * Crée un enseignant avec toutes ses informations.
     * Le rôle ENSEIGNANT est assigné automatiquement via la classe parente.
     *
     * @param id_enseignant    Identifiant unique de l'enseignant
     * @param nom_user         Nom de famille
     * @param prenom_user      Prénom
     * @param email            Adresse email de connexion
     * @param statut           Statut du compte (EN_ATTENTE, VALIDE, REJETE)
     * @param date_creation    Date de création du compte
     * @param date_inscription Date d'inscription effective
     */
    public Enseignant(int id_enseignant, String nom_user, String prenom_user,
                      String email, String statut, LocalDateTime date_creation, LocalDateTime date_inscription) {
        super(id_enseignant, nom_user, prenom_user, email, Role.ENSEIGNANT, statut,date_creation,date_inscription);
    }
    
}