package sn.esitec.poo.cahiertexte.model;

import java.time.LocalDateTime;

/**
 * Représente le chef de département dans le système.
 * Le chef de département est l'administrateur principal, il peut :
 * - Créer et valider immédiatement des comptes enseignants et responsables
 * - Assigner des cours aux enseignants (avec notification email automatique)
 * - Visualiser les statistiques globales de tous les cours
 * - Générer des rapports PDF pour chaque enseignant
 * Son rôle est automatiquement fixé à {@link Role#CHEF_DEPARTEMENT}.
 */
public class ChefDepartement extends Utilisateur{
    
    /**
     * Crée un chef de département avec toutes ses informations.
     * Le rôle CHEF_DEPARTEMENT est assigné automatiquement via la classe parente.
     *
     * @param id_user          Identifiant unique du chef
     * @param nom_user         Nom de famille
     * @param prenom_user      Prénom
     * @param email            Adresse email de connexion
     * @param statut           Statut du compte (EN_ATTENTE, VALIDE, REJETE)
     * @param date_creation    Date de création du compte
     * @param date_inscription Date d'inscription effective
     */
    public ChefDepartement(int id_user, String nom_user, String prenom_user,
                              String email, String statut,LocalDateTime date_creation, LocalDateTime date_inscription) {
        super(id_user, nom_user, prenom_user, email, Role.CHEF_DEPARTEMENT, statut,date_creation,date_inscription);
    }
    
}