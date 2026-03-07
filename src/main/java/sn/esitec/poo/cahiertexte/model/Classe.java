package sn.esitec.poo.cahiertexte.model;

/**
 * Représente une classe pédagogique dans le système.
 * Une classe est caractérisée par son nom (ex: "L3 Info"),
 * sa filière (ex: "Informatique") et son responsable de classe.
 * Chaque cours ({@link Cours}) est rattaché à une classe.
 * Chaque classe a un unique {@link ResponsableClasse} qui supervise
 * les séances des enseignants.
 */
public class Classe {
    private int id_classe;
    private String nom_classe;
    private String filiere;
    private int id_responsable;

    /**
     * Crée une nouvelle classe pédagogique.
     *
     * @param id_classe      Identifiant unique de la classe en BDD
     * @param nom_classe     Nom de la classe (ex: "L3 Informatique")
     * @param filiere        Filière d'appartenance (ex: "Génie Logiciel")
     * @param id_responsable Identifiant du responsable de classe assigné
     */
    public Classe(int id_classe, String nom_classe, String filiere, int id_responsable) {
        this.id_classe = id_classe;
        this.nom_classe = nom_classe;
        this.filiere = filiere;
        this.id_responsable = id_responsable;
    }

    // Getters
    public int getId()              { return id_classe; }
    public String getNom()          { return nom_classe; }
    public String getFiliere()      { return filiere; }
    public int getResponsableId()   { return id_responsable; }

    // Setters
    public void setNom(String nom_classe)              { this.nom_classe = nom_classe; }
    public void setFiliere(String filiere)      { this.filiere = filiere; }
    public void setResponsableId(int id_responsable)        { this.id_responsable = id_responsable; }

    @Override
    public String toString() {
        return nom_classe + " — " + filiere;
    }
}