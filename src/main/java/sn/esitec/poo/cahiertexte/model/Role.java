package sn.esitec.poo.cahiertexte.model;

/**
 * Enumération des rôles possibles pour un utilisateur du système.
 * Chaque rôle donne accès à un tableau de bord spécifique et à des
 * fonctionnalités différentes dans l'application.
 * <ul>
 *   <li>{@link #CHEF_DEPARTEMENT} : accès complet (gestion, statistiques, PDF)</li>
 *   <li>{@link #ENSEIGNANT} : saisie et gestion de ses séances</li>
 *   <li>{@link #RESPONSABLE_CLASSE} : validation des séances de sa classe</li>
 * </ul>
 * La valeur est stockée en BDD sous forme de String (ex: "ENSEIGNANT").
 */
public enum Role {
    /** Administrateur principal : gère les comptes, les cours et les rapports. */
    CHEF_DEPARTEMENT,
    /** Dispense les cours et saisit les séances dans le cahier de texte. */
    ENSEIGNANT,
    /** Supervise et valide les séances saisies pour sa classe. */
    RESPONSABLE_CLASSE;

    /**
     * Convertit un String provenant de la base de données en enum Role.
     *
     * @param role La valeur texte du rôle (ex: "ENSEIGNANT")
     * @return Le Role correspondant
     * @throws IllegalArgumentException si le rôle est inconnu
     */
    public static Role fromString(String role) {
        switch (role) {
            case "CHEF_DEPARTEMENT":   return CHEF_DEPARTEMENT;
            case "ENSEIGNANT":         return ENSEIGNANT;
            case "RESPONSABLE_CLASSE": return RESPONSABLE_CLASSE;
            default: throw new IllegalArgumentException("Rôle inconnu : " + role);
        }
    }

    // Convertir en String pour envoyer à la BDD
    @Override
    public String toString() {
        return this.name();
    }
}