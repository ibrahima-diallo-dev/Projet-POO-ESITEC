package sn.esitec.poo.cahiertexte.dao;

import sn.esitec.poo.cahiertexte.model.Classe;
import sn.esitec.poo.cahiertexte.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe d'accès aux données (DAO) pour les classes pédagogiques.
 * Gère les opérations sur la table 'classes' en BDD.
 * Chaque classe a un nom, une filière et un responsable de classe associé.
 * Cette DAO est utilisée notamment par le ResponsableController
 * pour retrouver automatiquement la classe dont il a la charge.
 */
public class ClasseDAO {

    /**
     * Insère une nouvelle classe pédagogique en BDD.
     *
     * @param c La classe à insérer (nom, filière, id_responsable)
     * @return true si l'insertion a réussi, false sinon
     */
    public boolean ajouter(Classe c) {
        String sql = "INSERT INTO classes (nom_classe, filiere, id_responsable) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNom());
            ps.setString(2, c.getFiliere());
            ps.setInt(3, c.getResponsableId());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Récupère toutes les classes enregistrées en BDD.
     * Utilisé par le chef de département pour les ComboBox d'assignation de cours.
     *
     * @return Liste de toutes les classes
     */
    public List<Classe> getToutesLesClasses() {
        List<Classe> liste = new ArrayList<>();
        String sql = "SELECT * FROM classes";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                liste.add(construireClasse(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Retrouve la classe assignée à un responsable donné.
     * Appelé au chargement du tableau de bord du responsable pour
     * déterminer quelle classe il supervise.
     *
     * @param id_responsable Identifiant du responsable de classe
     * @return La classe associée, ou null si aucune classe ne lui est assignée
     */
    public Classe getClasseParResponsable(int id_responsable) {
        String sql = "SELECT * FROM classes WHERE id_responsable = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id_responsable);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return construireClasse(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Récupère une classe par son identifiant unique.
     *
     * @param id_classe Identifiant de la classe recherchée
     * @return La classe trouvée, ou null si elle n'existe pas
     */
    public Classe getById(int id_classe) {
        String sql = "SELECT * FROM classes WHERE id_classe = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id_classe);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return construireClasse(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Construit un objet {@link Classe} à partir d'une ligne du ResultSet.
     *
     * @param rs Le ResultSet positionné sur la ligne à lire
     * @return Un objet Classe initialisé
     * @throws SQLException en cas d'erreur de lecture
     */
    private Classe construireClasse(ResultSet rs) throws SQLException {
        return new Classe(
            rs.getInt("id_classe"),
            rs.getString("nom_classe"),
            rs.getString("filiere"),
            rs.getInt("id_responsable")
        );
    }
}