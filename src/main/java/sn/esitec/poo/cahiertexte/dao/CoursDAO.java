package sn.esitec.poo.cahiertexte.dao;

import sn.esitec.poo.cahiertexte.model.Cours;
import sn.esitec.poo.cahiertexte.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe d'accès aux données (DAO) pour les cours.
 * Gère les opérations sur la table 'cours' en BDD.
 * Un cours est assigné par le chef de département à un enseignant
 * pour une classe donnée, avec un volume horaire prévu.
 * Les {@link sn.esitec.poo.cahiertexte.model.Seance} font référence
 * à un cours via son identifiant.
 */
public class CoursDAO {

    /**
     * Insère un nouveau cours en BDD.
     *
     * @param c Le cours à insérer (intitulé, volume horaire, id_enseignant, id_classe)
     * @return true si l'insertion a réussi, false sinon
     */
    public boolean ajouter(Cours c) {
        String sql = "INSERT INTO cours (intitule, volume_horaire, id_enseignant, id_classe) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getIntitule());
            ps.setInt(2, c.getVolumeHoraire());
            ps.setInt(3, c.getEnseignantId());
            ps.setInt(4, c.getClasseId());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Récupère la liste de tous les cours assignés à un enseignant.
     * Utilisé dans le tableau de bord de l'enseignant pour afficher ses matières.
     *
     * @param id_enseignant Identifiant de l'enseignant
     * @return Liste des cours de cet enseignant
     */
    public List<Cours> getCoursDeLEnseignant(int id_enseignant) {
        List<Cours> liste = new ArrayList<>();
        String sql = "SELECT * FROM cours WHERE id_enseignant = ?";
        System.out.println("DEBUG CoursDAO: Recherche cours pour id_enseignant=" + id_enseignant);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id_enseignant);
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                Cours c = new Cours(
                    rs.getInt("id_cours"),
                    rs.getString("intitule"),
                    rs.getInt("volume_horaire"),
                    rs.getInt("id_enseignant"),
                    rs.getInt("id_classe")
                );
                System.out.println("  Cours trouvé: " + c.getIntitule() + " (id_cours=" + c.getId() + ")");
                liste.add(c);
            }
            System.out.println("DEBUG CoursDAO: Total trovés: " + count);
        } catch (SQLException e) {
            System.out.println("DEBUG CoursDAO: ERREUR SQL - " + e.getMessage());
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Récupère l'intégralité des cours en BDD, tous enseignants confondus.
     * Utilisé par le chef de département pour les statistiques globales.
     *
     * @return Liste de tous les cours
     */
    public List<Cours> getTousLesCours() {
        List<Cours> liste = new ArrayList<>();
        String sql = "SELECT * FROM cours";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                liste.add(new Cours(
                    rs.getInt("id_cours"),
                    rs.getString("intitule"),
                    rs.getInt("volume_horaire"),
                    rs.getInt("id_enseignant"),
                    rs.getInt("id_classe")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Récupère un cours par son identifiant unique.
     *
     * @param id_cours Identifiant du cours recherché
     * @return Le cours trouvé, ou null s'il n'existe pas
     */
    public Cours getById(int id_cours) {
        String sql = "SELECT * FROM cours WHERE id_cours = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id_cours);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Cours(
                    rs.getInt("id_cours"),
                    rs.getString("intitule"),
                    rs.getInt("volume_horaire"),
                    rs.getInt("id_enseignant"),
                    rs.getInt("id_classe")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}