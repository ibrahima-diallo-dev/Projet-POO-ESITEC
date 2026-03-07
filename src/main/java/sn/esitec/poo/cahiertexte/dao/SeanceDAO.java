package sn.esitec.poo.cahiertexte.dao;

import sn.esitec.poo.cahiertexte.model.Seance;
import sn.esitec.poo.cahiertexte.model.StatutSeance;
import sn.esitec.poo.cahiertexte.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe d'accès aux données (DAO) pour les séances.
 * Gère toutes les opérations sur la table 'seances' en BDD.
 * Permet :
 * <ul>
 *   <li>Ajouter, modifier et supprimer des séances (si EN_ATTENTE uniquement)</li>
 *   <li>Valider ou rejeter une séance (changement de statut)</li>
 *   <li>Récupérer les séances par enseignant, par classe ou par cours</li>
 * </ul>
 * La méthode {@link #construireSeance(ResultSet)} convertit le ResultSet
 * en objet {@link Seance} avec conversion de date et de statut.
 */
public class SeanceDAO {

    /**
     * Insère une nouvelle séance en BDD.
     * Le statut est implicitement fixé à EN_ATTENTE par la BDD (à la création).
     *
     * @param s La séance à insérer
     * @return true si l'insertion a réussi, false sinon
     */
    public boolean ajouter(Seance s) {
        String sql = "INSERT INTO seances (date_seance, heure_debut, duree, contenu, " +
                     "observations, id_cours, id_enseignant) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(s.getDateSeance()));
            ps.setTime(2, Time.valueOf(s.getHeureDebut()));
            ps.setInt(3, s.getDuree());
            ps.setString(4, s.getContenu());
            ps.setString(5, s.getObservations());
            ps.setInt(6, s.getCoursId());
            ps.setInt(7, s.getEnseignantId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Met à jour une séance existante en BDD.
     * La modification n'est autorisée que si la séance est encore EN_ATTENTE.
     *
     * @param s La séance avec les nouvelles valeurs (son ID doit exister en BDD)
     * @return true si la mise à jour a réussi, false si interdite ou erreur SQL
     */
    public boolean modifier(Seance s) {
        if (!s.estModifiable()) {
            System.out.println("Cette séance ne peut plus être modifiée !");
            return false;
        }
        String sql = "UPDATE seances SET date_seance=?, heure_debut=?, duree=?, " +
                     "contenu=?, observations=? WHERE id_seance=? AND statut='EN_ATTENTE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(s.getDateSeance()));
            ps.setTime(2, Time.valueOf(s.getHeureDebut()));
            ps.setInt(3, s.getDuree());
            ps.setString(4, s.getContenu());
            ps.setString(5, s.getObservations());
            ps.setInt(6, s.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Valide une séance : passe son statut à VALIDEE et efface le commentaire.
     * Un email de notification est envoyé depuis le service appelant.
     *
     * @param id_seance Identifiant de la séance à valider
     */
    public void valider(int id_seance) {
        changerStatut(id_seance, StatutSeance.VALIDEE, null);
    }

    /**
     * Rejette une séance : passe son statut à REJETEE et enregistre le motif.
     * Un email de notification est envoyé depuis le contrôleur appelant.
     *
     * @param id_seance         Identifiant de la séance à rejeter
     * @param commentaire_rejet Motif obligatoire du rejet (affi sur le tableau de l'enseignant)
     */
    public void rejeter(int id_seance, String commentaire_rejet) {
        System.out.println("🔴 [SeanceDAO] Rejet de la séance " + id_seance + " avec motif: " + commentaire_rejet);
        changerStatut(id_seance, StatutSeance.REJETEE, commentaire_rejet);
    }

    // 🔄 Changer le statut d'une séance
    private void changerStatut(int id_seance, StatutSeance statut, String commentaire_rejet) {
        String sql = "UPDATE seances SET statut=?, commentaire_rejet=? WHERE id_seance=?";
        System.out.println("🔄 [SeanceDAO] SQL: " + sql);
        System.out.println("   statut=" + statut + ", commentaire=" + commentaire_rejet + ", id=" + id_seance);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut.toString());
            if (commentaire_rejet != null) {
                ps.setString(2, commentaire_rejet);
            } else {
                ps.setNull(2, Types.VARCHAR);
            }
            ps.setInt(3, id_seance);
            int rowsAffected = ps.executeUpdate();
            System.out.println("✅ [SeanceDAO] Mise à jour effectuée - " + rowsAffected + " ligne(s) affectée(s)");
        } catch (SQLException e) {
            System.out.println("❌ [SeanceDAO] Erreur SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Récupère toutes les séances d'un enseignant, tous statuts confondus.
     *
     * @param id_enseignant Identifiant de l'enseignant
     * @return Liste des séances de cet enseignant
     */
    public List<Seance> getSeancesParEnseignant(int id_enseignant) {
        return getSeances("WHERE id_enseignant = ?", id_enseignant);
    }

    /**
     * Récupère les séances d'un cours dont le statut est EN_ATTENTE.
     *
     * @param id_cours Identifiant du cours
     * @return Liste des séances EN_ATTENTE pour ce cours
     */
    public List<Seance> getSeancesEnAttente(int id_cours) {
        return getSeances("WHERE id_cours = ? AND statut = 'EN_ATTENTE'", id_cours);
    }

    /**
     * Récupère toutes les séances liées à une classe.
     * Fait une jointure entre la table 'seances' et 'cours' via id_cours.
     * Utilisé par le responsable de classe pour voir le cahier de texte.
     *
     * @param id_classe Identifiant de la classe
     * @return Liste de toutes les séances de cette classe (tous statuts)
     */
    public List<Seance> getSeancesParClasse(int id_classe) {
        List<Seance> liste = new ArrayList<>();
        String sql = "SELECT s.* FROM seances s " +
                     "JOIN cours c ON s.id_cours = c.id_cours " +
                     "WHERE c.id_classe = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id_classe);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(construireSeance(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Supprime une séance de la BDD.
     * La suppression n'est autorisée que si la séance est encore EN_ATTENTE.
     *
     * @param id_seance Identifiant de la séance à supprimer
     * @return true si la suppression a été effectuée, false si impossible ou erreur
     */
    public boolean supprimer(int id_seance) {
        String sql = "DELETE FROM seances WHERE id_seance = ? AND statut = 'EN_ATTENTE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id_seance);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Récupère toutes les séances de la BDD, tous cours confondus.
     * Utilisé par le chef de département pour les statistiques globales.
     *
     * @return Liste complète de toutes les séances
     */
    public List<Seance> getToutesLesSeances() {
        List<Seance> liste = new ArrayList<>();
        String sql = "SELECT * FROM seances";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) liste.add(construireSeance(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Méthode utilitaire : exécute une requête SELECT avec une condition
     * et un paramètre entier, et retourne la liste des séances correspondantes.
     *
     * @param condition Clause WHERE SQL (ex: "WHERE id_enseignant = ?")
     * @param parametre Valeur du paramètre à substituer dans la requête
     * @return Liste des séances résultantes
     */
    private List<Seance> getSeances(String condition, int parametre) {
        List<Seance> liste = new ArrayList<>();
        String sql = "SELECT * FROM seances " + condition;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, parametre);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(construireSeance(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Construit un objet {@link Seance} à partir d'une ligne du ResultSet.
     * Effectue les conversions SQL → Java : Date, Time, et StatutSeance.
     *
     * @param rs Le ResultSet positionné sur la ligne à lire
     * @return Un objet Seance complètement initialisé
     * @throws SQLException en cas d'erreur de lecture
     */
    private Seance construireSeance(ResultSet rs) throws SQLException {
        return new Seance(
            rs.getInt("id_seance"),
            rs.getDate("date_seance").toLocalDate(),
            rs.getTime("heure_debut").toLocalTime(),
            rs.getInt("duree"),
            rs.getString("contenu"),
            rs.getString("observations"),
            StatutSeance.fromString(rs.getString("statut")),
            rs.getString("commentaire_rejet"),
            rs.getInt("id_cours"),
            rs.getInt("id_enseignant")
        );
    }
}