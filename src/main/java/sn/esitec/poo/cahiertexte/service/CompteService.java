package sn.esitec.poo.cahiertexte.service;

import sn.esitec.poo.cahiertexte.dao.UtilisateurDAO;
import sn.esitec.poo.cahiertexte.model.*;
import java.util.List;

/**
 * Service métier pour la gestion des comptes utilisateurs.
 * Gère la création, la validation et le rejet des comptes
 * enseignants et responsables de classe.
 * Le chef de département utilise ce service pour :
 * <ul>
 *   <li>Créer un compte auto-validé ({@link #creerEtValiderEnseignant})</li>
 *   <li>Valider ou rejeter un compte en attente</li>
 *   <li>Lister les comptes selon leur rôle ou statut</li>
 * </ul>
 * Les validations métier (email, longueur du mot de passe) sont effectuées
 * ici avant de déléguer l'opération à {@link UtilisateurDAO}.
 */
public class CompteService {

    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    /**
     * Crée un compte enseignant avec le statut EN_ATTENTE.
     * Le compte doit ensuite être validé manuellement par le chef.
     *
     * @param nom         Nom de l'enseignant
     * @param prenom      Prénom de l'enseignant
     * @param email       Adresse email (doit contenir '@' et '.')
     * @param mot_de_passe Mot de passe (6 caractères minimum)
     * @return true si le compte a été créé, false si validation échouée ou erreur SQL
     */
    public boolean creerEnseignant(String nom, String prenom,
                                    String email, String mot_de_passe) {
        if (!emailValide(email)) {
            System.out.println("⛔ Email invalide !");
            return false;
        }
        if (mot_de_passe.length() < 6) {
            System.out.println("⛔ Mot de passe trop court (6 caractères minimum) !");
            return false;
        }
        Enseignant e = new Enseignant(0, nom, prenom, email, "EN_ATTENTE", null, null);
        return utilisateurDAO.ajouter(e, mot_de_passe);
    }

    /**
     * Crée un compte responsable de classe avec le statut EN_ATTENTE.
     * Le compte doit ensuite être validé manuellement par le chef.
     *
     * @param nom         Nom du responsable
     * @param prenom      Prénom du responsable
     * @param email       Adresse email (doit contenir '@' et '.')
     * @param mot_de_passe Mot de passe (6 caractères minimum)
     * @return true si le compte a été créé, false si validation échouée ou erreur SQL
     */
    public boolean creerResponsable(String nom, String prenom,
                                     String email, String mot_de_passe) {
        if (!emailValide(email)) {
            System.out.println("⛔ Email invalide !");
            return false;
        }
        if (mot_de_passe.length() < 6) {
            System.out.println("⛔ Mot de passe trop court (6 caractères minimum) !");
            return false;
        }
        ResponsableClasse r = new ResponsableClasse(0, nom, prenom, email, "EN_ATTENTE", null, null);
        return utilisateurDAO.ajouter(r, mot_de_passe);
    }

    // ✅ Valider un compte
    public void validerCompte(int id_user) {
        utilisateurDAO.validerCompte(id_user);
    }

    // ❌ Rejeter un compte
    public void rejeterCompte(int id_user) {
        utilisateurDAO.rejeterCompte(id_user);
    }

    // 📋 Comptes en attente de validation
    public List<Utilisateur> getComptesEnAttente() {
        return utilisateurDAO.getUtilisateursEnAttente();
    }

    // 📋 Tous les enseignants
    public List<Utilisateur> getEnseignants() {
        return utilisateurDAO.getEnseignants();
    }

    // 📋 Tous les responsables
    public List<Utilisateur> getResponsables() {
        return utilisateurDAO.getResponsables();
    }

    // 📋 Tous les utilisateurs actifs (validés)
    public List<Utilisateur> getUtilisateursActifs() {
        return utilisateurDAO.getUtilisateursActifs();
    }

    /**
     * Crée un compte enseignant ET le valide immédiatement.
     * Utilisé par le chef de département pour contourner l'étape d'attente.
     * Récupère l'ID généré puis appelle directement la validation.
     *
     * @param nom         Nom de l'enseignant
     * @param prenom      Prénom de l'enseignant
     * @param email       Adresse email
     * @param mot_de_passe Mot de passe (6 caractères minimum)
     * @return true si le compte a été créé et validé avec succès
     */
    public boolean creerEtValiderEnseignant(String nom, String prenom,
                                           String email, String mot_de_passe) {
        System.out.println("🔄 Création et validation d'enseignant: " + email);
        if (!emailValide(email)) {
            System.out.println("⛔ Email invalide !");
            return false;
        }
        if (mot_de_passe.length() < 6) {
            System.out.println("⛔ Mot de passe trop court (6 caractères minimum) !");
            return false;
        }
        
        // Créer l'enseignant et récupérer l'ID
        Enseignant e = new Enseignant(0, nom, prenom, email, "EN_ATTENTE", null, null);
        int nouvelId = utilisateurDAO.ajouterUtilisateur(e, mot_de_passe);
        
        if (nouvelId > 0) {
            // Valider immédiatement le compte
            System.out.println("✅ Utilisateur créé avec ID " + nouvelId + ", validation en cours...");
            validerCompte(nouvelId);
            System.out.println("✅ Compte validé pour ID " + nouvelId);
            return true;
        }
        System.out.println("❌ Échec de la création de l'utilisateur");
        return false;
    }

    /**
     * Crée un compte responsable ET le valide immédiatement.
     * Utilisé par le chef de département pour contourner l'étape d'attente.
     *
     * @param nom         Nom du responsable
     * @param prenom      Prénom du responsable
     * @param email       Adresse email
     * @param mot_de_passe Mot de passe (6 caractères minimum)
     * @return true si le compte a été créé et validé avec succès
     */
    public boolean creerEtValiderResponsable(String nom, String prenom,
                                            String email, String mot_de_passe) {
        if (!emailValide(email)) {
            System.out.println("⛔ Email invalide !");
            return false;
        }
        if (mot_de_passe.length() < 6) {
            System.out.println("⛔ Mot de passe trop court (6 caractères minimum) !");
            return false;
        }
        
        // Créer le responsable et récupérer l'ID
        ResponsableClasse r = new ResponsableClasse(0, nom, prenom, email, "EN_ATTENTE", null, null);
        int nouvelId = utilisateurDAO.ajouterUtilisateur(r, mot_de_passe);
        
        if (nouvelId > 0) {
            // Valider immédiatement le compte
            validerCompte(nouvelId);
            return true;
        }
        return false;
    }

    /**
     * Vérifie basiquement qu'un email est valide (contient '@' et '.').
     *
     * @param email L'adresse email à vérifier
     * @return true si l'email semble valide, false sinon
     */
    private boolean emailValide(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}