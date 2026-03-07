package sn.esitec.poo.cahiertexte.model;

/**
 * Enumération des statuts possibles d'une séance dans le cahier de texte.
 * Le cycle de vie d'une séance suit cette progression :
 * <pre>
 *   [Enseignant saisit]  →  EN_ATTENTE
 *   [Responsable valide] →  VALIDEE
 *   [Responsable rejette]→  REJETEE
 * </pre>
 * Seules les séances EN_ATTENTE peuvent être modifiées ou supprimées
 * par l'enseignant (voir {@link Seance#estModifiable()}).
 * La valeur est stockée en BDD sous forme de String (ex: "EN_ATTENTE").
 */
public enum StatutSeance {
    /** Séance saisie par l'enseignant, en attente de validation par le responsable. */
    EN_ATTENTE,
    /** Séance validée par le responsable de classe. */
    VALIDEE,
    /** Séance rejetée par le responsable avec un commentaire de motif. */
    REJETEE;

    /**
     * Convertit un String provenant de la base de données en enum StatutSeance.
     *
     * @param statut La valeur texte du statut (ex: "EN_ATTENTE")
     * @return Le StatutSeance correspondant
     * @throws IllegalArgumentException si le statut est inconnu
     */
    public static StatutSeance fromString(String statut) {
        switch (statut) {
            case "EN_ATTENTE": return EN_ATTENTE;
            case "VALIDEE":    return VALIDEE;
            case "REJETEE":    return REJETEE;
            default: throw new IllegalArgumentException("Statut inconnu : " + statut);
        }
    }

    // Convertir en String pour envoyer à la BDD
    @Override
    public String toString() {
        return this.name();
    }
}