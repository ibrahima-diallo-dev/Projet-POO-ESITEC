package sn.esitec.poo.cahiertexte.model;

import java.time.LocalDateTime;

/**
 * Représente un responsable de classe dans le système.
 * Un responsable de classe peut :
 * - Consulter le cahier de texte de sa classe
 * - Valider ou rejeter les séances saisies par les enseignants
 * - Voir les statistiques d'avancement de sa classe
 * Son rôle est automatiquement fixé à {@link Role#RESPONSABLE_CLASSE}.
 * Chaque responsable est associé à une unique classe dans la table 'classes'.
 */
public class ResponsableClasse extends Utilisateur{

    /**
     * Crée un responsable de classe avec toutes ses informations.
     * Le rôle RESPONSABLE_CLASSE est assigné automatiquement via la classe parente.
     *
     * @param id_responsable   Identifiant unique du responsable
     * @param nom_user         Nom de famille
     * @param prenom_user      Prénom
     * @param email            Adresse email de connexion
     * @param statut           Statut du compte (EN_ATTENTE, VALIDE, REJETE)
     * @param date_creation    Date de création du compte
     * @param date_inscription Date d'inscription effective
     */
    public ResponsableClasse(int id_responsable, String nom_user, String prenom_user,
                              String email, String statut,LocalDateTime date_creation, LocalDateTime date_inscription) {
        super(id_responsable, nom_user, prenom_user, email, Role.RESPONSABLE_CLASSE, statut,date_creation,date_inscription);
    }
    
}