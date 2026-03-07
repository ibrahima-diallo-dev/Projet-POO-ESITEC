package sn.esitec.poo.cahiertexte.model;

import java.time.LocalDateTime;

/**
 * Classe de base représentant un utilisateur du système.
 * Tous les types d'utilisateurs (Enseignant, ResponsableClasse, ChefDepartement)
 * héritent de cette classe.
 * Un utilisateur possède un rôle, un statut (EN_ATTENTE, VALIDE, REJETE)
 * et des dates de création et d'inscription.
 */
public class Utilisateur {


    private int id_user;
    private String nom_user;
    private String prenom_user;
    private String email;
    private Role role;
    private String statut;
    private LocalDateTime date_creation ;
    private LocalDateTime date_inscription ;

    /**
     * Constructeur complet avec toutes les informations de l'utilisateur.
     *
     * @param id_user          Identifiant unique de l'utilisateur en BDD
     * @param nom_user         Nom de famille
     * @param prenom_user      Prénom
     * @param email            Adresse email (utilisée pour la connexion)
     * @param role             Rôle de l'utilisateur (enum Role)
     * @param statut           Statut du compte : EN_ATTENTE, VALIDE ou REJETE
     * @param date_creation    Date de création du compte
     * @param date_inscription Date d'inscription effective
     */
    public Utilisateur(int id_user, String nom_user, String prenom_user,
                       String email, Role role, String statut, LocalDateTime date_creation , LocalDateTime date_inscription) {
        this.id_user = id_user;
        this.nom_user = nom_user;
        this.prenom_user = prenom_user;
        this.email = email;
        this.role = role;
        this.statut = statut;
        this.date_creation=date_creation;
        this.date_inscription=date_inscription;
    }

    // Getters
    public int getId()          { return id_user; }
    public String getNom()      { return nom_user; }
    public String getPrenom()   { return prenom_user; }
    public String getEmail()    { return email; }
    public Role getRole()       { return role; }
    public String getStatut()   { return statut; }
    public LocalDateTime getDateCreation()   { return date_creation; }
    public LocalDateTime getDateInscription()   { return date_inscription; }

    // Setters
    public void setStatut(String statut)       { this.statut = statut; }
    public void setMotDePasse(String mdp)      { /* Mot de passe géré en DAO */ }

    @Override
    public String toString() {
        return prenom_user + " " + nom_user + " (" + role + ")";
    }
        /**
     * Constructeur simplifié : les dates de création et d'inscription
     * sont automatiquement initialisées à la date et heure actuelles.
     *
     * @param id_user     Identifiant unique de l'utilisateur
     * @param nom_user    Nom de famille
     * @param prenom_user Prénom
     * @param email       Adresse email
     * @param role        Rôle de l'utilisateur
     * @param statut      Statut du compte
     */
    public Utilisateur(int id_user, String nom_user, String prenom_user, String email, Role role, String statut) {
    this(id_user, nom_user, prenom_user, email, role, statut, LocalDateTime.now(), LocalDateTime.now());
}
}