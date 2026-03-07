package sn.esitec.poo.cahiertexte.utils;

import sn.esitec.poo.cahiertexte.model.Utilisateur;

/**
 * Singleton permettant de stocker l'utilisateur connecté durant toute la session.
 */
public class SessionManager {
    private static SessionManager instance;
    private Utilisateur utilisateurConnecte;

    private SessionManager() {}

    /**
     * Retourne l'instance unique du {@code SessionManager} (lazy initialization).
     *
     * @return Instance singleton de {@code SessionManager}
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Retourne l'utilisateur actuellement connecté.
     *
     * @return L'utilisateur connecté, ou {@code null} si aucune session active
     */
    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    /**
     * Enregistre l'utilisateur connecté en session.
     *
     * @param utilisateur L'utilisateur qui vient de s'authentifier
     */
    public void setUtilisateurConnecte(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
    }

    /**
     * Efface la session en cours (déconnexion de l'utilisateur).
     * Réinitialise la référence de l'utilisateur connecté à {@code null}.
     */
    public void cleanUserSession() {
        utilisateurConnecte = null;
    }
}