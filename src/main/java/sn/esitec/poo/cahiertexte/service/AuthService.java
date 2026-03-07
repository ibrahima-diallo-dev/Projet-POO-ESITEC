package sn.esitec.poo.cahiertexte.service;

import sn.esitec.poo.cahiertexte.dao.UtilisateurDAO;
import sn.esitec.poo.cahiertexte.model.Utilisateur;

/**
 * Service d'authentification des utilisateurs.
 * Fait le lien entre le contrôleur de connexion ({@code LoginController})
 * et le DAO ({@link UtilisateurDAO}) pour vérifier les identifiants.
 * Vérifie en plus que l'email et le mot de passe ne sont pas vides
 * avant d'effectuer la requête en base de données.
 */
public class AuthService {

    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    /**
     * Tente de connecter un utilisateur avec ses identifiants.
     * Vérifie que les champs ne sont pas vides avant d'appeler le DAO.
     * Le compte doit avoir le statut VALIDE pour que la connexion réussisse.
     *
     * @param email       Adresse email saisie par l'utilisateur
     * @param mot_de_passe Mot de passe saisi par l'utilisateur
     * @return L'objet {@link Utilisateur} si la connexion réussit, null sinon
     */
    public Utilisateur connecter(String email, String mot_de_passe) {
        if (email == null || email.isEmpty() ||
            mot_de_passe == null || mot_de_passe.isEmpty()) {
            System.out.println(" Email et mot de passe obligatoires !");
            return null;
        }
        Utilisateur u = utilisateurDAO.login(email, mot_de_passe);
        if (u == null) {
            System.out.println(" Email ou mot de passe incorrect, ou compte non validé !");
        }
        return u;
    }

    /**
     * Vérifie si un utilisateur est actuellement connecté.
     *
     * @param u L'utilisateur à tester
     * @return true si l'utilisateur n'est pas null (session active), false sinon
     */
    public boolean estConnecte(Utilisateur u) {
        return u != null;
    }
}