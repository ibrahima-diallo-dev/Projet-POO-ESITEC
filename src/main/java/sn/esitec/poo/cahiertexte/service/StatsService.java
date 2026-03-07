package sn.esitec.poo.cahiertexte.service;

import sn.esitec.poo.cahiertexte.dao.SeanceDAO;
import sn.esitec.poo.cahiertexte.dao.CoursDAO;
import sn.esitec.poo.cahiertexte.model.Cours;
import sn.esitec.poo.cahiertexte.model.Seance;
import sn.esitec.poo.cahiertexte.model.StatutSeance;
import java.util.List;

/**
 * Service de calcul des statistiques pédagogiques.
 * Fournit des indicateurs sur l'avancement des cours :
 * <ul>
 *   <li>Nombre de séances validées par cours</li>
 *   <li>Heures effectivement réalisées (somme des durées validées en minutes / 60)</li>
 *   <li>Taux d'avancement en pourcentage par rapport au volume horaire prévu</li>
 * </ul>
 * Utilisé par le tableau de bord du chef de département pour
 * visualiser l'avancement global de tous les cours.
 */
public class StatsService {

    private SeanceDAO seanceDAO = new SeanceDAO();
    private CoursDAO coursDAO = new CoursDAO();

    /**
     * Retourne le nombre de séances validées pour un cours donné.
     *
     * @param id_cours Identifiant du cours
     * @return Nombre de séances dont le statut est {@code VALIDEE}
     */
    public int getNombreSeancesValidees(int id_cours) {
        List<Seance> seances = seanceDAO.getSeancesParEnseignant(id_cours);
        int count = 0;
        for (Seance s : seances) {
            if (StatutSeance.VALIDEE.equals(s.getStatut())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calcule le total des heures effectuées pour un cours (séances validées uniquement).
     *
     * @param id_cours Identifiant du cours
     * @return Nombre d'heures effectuées (somme des durées en minutes / 60)
     */
    public int getHeuresEffectuees(int id_cours) {
        List<Seance> seances = seanceDAO.getSeancesParEnseignant(id_cours);
        int totalMinutes = 0;
        for (Seance s : seances) {
            if (StatutSeance.VALIDEE.equals(s.getStatut())) {
                totalMinutes += s.getDuree();
            }
        }
        return totalMinutes / 60;  // retourne en heures
    }

    /**
     * Calcule le taux d'avancement d'un cours en pourcentage.
     * <p>
     * Formule : {@code (heuresEffectuees / volumeHoraire) * 100}
     * </p>
     *
     * @param id_cours Identifiant du cours
     * @return Taux d'avancement entre 0 et 100 (%), ou 0 si le volume horaire est nul
     */
    public double getTauxAvancement(int id_cours) {
        List<Cours> cours = coursDAO.getTousLesCours();
        for (Cours c : cours) {
            if (c.getId() == id_cours) {
                int heuresEffectuees = getHeuresEffectuees(id_cours);
                int volumeHoraire = c.getVolumeHoraire();
                if (volumeHoraire == 0) return 0;
                return ((double) heuresEffectuees / volumeHoraire) * 100;
            }
        }
        return 0;
    }

    /**
     * Affiche dans la console les statistiques globales de tous les cours
     * (heures effectuées, volume horaire total et taux d'avancement).
     * Utilisé à des fins de débogage ou de supervision.
     */
    public void afficherStatsGlobales() {
        List<Cours> tousLesCours = coursDAO.getTousLesCours();
        System.out.println("===== STATISTIQUES GLOBALES =====");
        for (Cours c : tousLesCours) {
            double taux = getTauxAvancement(c.getId());
            System.out.println(c.getIntitule() + " → " +
                               getHeuresEffectuees(c.getId()) + "h / " +
                               c.getVolumeHoraire() + "h (" +
                               String.format("%.1f", taux) + "%)");
        }
    }
}