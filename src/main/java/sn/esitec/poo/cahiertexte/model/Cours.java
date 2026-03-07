package sn.esitec.poo.cahiertexte.model;

/**
 * Représente un cours dispensé dans une classe par un enseignant.
 * Un cours est caractérisé par son intitulé (ex: "Algorithmique"),
 * son volume horaire prévu (ex: 30 heures), l'enseignant qui le dispense
 * et la classe concernée.
 * Les {@link Seance} enregistrent la progression réelle du cours.
 */
public class Cours {

    private int id_cours;
    private String intitule;
    private int volume_horaire;
    private int id_enseignant;
    private int id_classe;

    /**
     * Crée un cours avec toutes ses informations.
     *
     * @param id_cours       Identifiant unique du cours en BDD
     * @param intitule       Nom du cours (ex: "Programmation Orientée Objet")
     * @param volume_horaire Volume horaire total prévu (en heures)
     * @param id_enseignant  Identifiant de l'enseignant qui dispense ce cours
     * @param id_classe      Identifiant de la classe concernée
     */
    public Cours(int id_cours, String intitule, int volume_horaire,
                 int id_enseignant, int id_classe) {
        this.id_cours = id_cours;
        this.intitule = intitule;
        this.volume_horaire = volume_horaire;
        this.id_enseignant = id_enseignant;
        this.id_classe = id_classe;
    }

    // Getters
    public int getId()              { return id_cours; }
    public String getIntitule()     { return intitule; }
    public int getVolumeHoraire()   { return volume_horaire; }
    public int getEnseignantId()    { return id_enseignant; }
    public int getClasseId()        { return id_classe; }

    // Setters
    public void setIntitule(String intitule)        { this.intitule = intitule; }
    public void setVolumeHoraire(int volume_horaire)            { this.volume_horaire = volume_horaire; }
    public void setEnseignantId(int id_enseignant)   { this.id_enseignant = id_enseignant; }

    @Override
    public String toString() {
        return intitule + " (" + volume_horaire + "h)";
    } 

}