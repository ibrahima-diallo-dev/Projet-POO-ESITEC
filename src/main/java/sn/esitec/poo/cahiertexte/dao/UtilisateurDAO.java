package sn.esitec.poo.cahiertexte.dao;

import sn.esitec.poo.cahiertexte.model.*;
import sn.esitec.poo.cahiertexte.utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Classe d'accès aux données (DAO) pour les utilisateurs.
 * Gère toutes les opérations CRUD sur la table 'utilisateurs' en BDD.
 * Permet :
 * <ul>
 *   <li>L'authentification (login)</li>
 *   <li>La création et validation de comptes</li>
 *   <li>La récupération des utilisateurs par rôle ou statut</li>
 * </ul>
 * La méthode {@link #construireUtilisateur(ResultSet)} instancie automatiquement
 * le bon sous-type (Enseignant, ResponsableClasse, ChefDepartement) selon le rôle.
 */
public class UtilisateurDAO {

    /**
     * Authentifie un utilisateur par email et mot de passe.
     * Également vérifie que le compte est au statut VALIDE.
     *
     * @param email       Adresse email de l'utilisateur
     * @param mot_de_passe Mot de passe en clair (doit correspondre à la valeur en BDD)
     * @return L'utilisateur trouvé (Enseignant, Responsable ou Chef), ou null si échec
     */
    public Utilisateur login(String email, String mot_de_passe) {
        String sql = "SELECT * FROM utilisateurs WHERE email = ? " +
                     "AND mot_de_passe = ? AND statut = 'VALIDE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, mot_de_passe);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return construireUtilisateur(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Ajoute un utilisateur en BDD et retourne son identifiant généré.
     * Le statut est automatiquement fixé à EN_ATTENTE à la création.
     *
     * @param u           L'utilisateur à insérer (le rôle est lu via getRole())
     * @param mot_de_passe Mot de passe en clair à stocker
     * @return L'ID généré par la BDD si succès, -1 en cas d'échec
     */
    public int ajouterUtilisateur(Utilisateur u, String mot_de_passe) {
        String sql = "INSERT INTO utilisateurs (nom_user, prenom_user, email, mot_de_passe, " +
                     "role, statut, date_creation) VALUES (?, ?, ?, ?, ?, 'EN_ATTENTE', CURRENT_TIMESTAMP)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, mot_de_passe);
            ps.setString(5, u.getRole().toString());  // ✅ enum → String pour la BDD
            
            System.out.println("🔄 Tentative d'ajout d'utilisateur: " + u.getEmail());
            int rowsAffected = ps.executeUpdate();
            System.out.println("📊 Lignes affectées: " + rowsAffected);
            if (rowsAffected > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    System.out.println("✅ Utilisateur ajouté avec ID: " + id);
                    return id; // Retourner l'ID généré
                }
            }
            System.out.println("❌ Échec de l'ajout: pas de clés générées");
            return -1;

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Ajoute un utilisateur et retourne true si l'opération a réussi.
     * Méthode de compatibilité s'appuyant sur {@link #ajouterUtilisateur}.
     *
     * @param u           L'utilisateur à insérer
     * @param mot_de_passe Mot de passe en clair
     * @return true si l'utilisateur a été créé, false sinon
     */
    public boolean ajouter(Utilisateur u, String mot_de_passe) {
        return ajouterUtilisateur(u, mot_de_passe) > 0;
    }

    /**
     * Valide un compte utilisateur en passant son statut à 'VALIDE'.
     * Après validation, l'utilisateur peut se connecter.
     *
     * @param id_user Identifiant de l'utilisateur à valider
     */
    public void validerCompte(int id_user) {
        System.out.println("🔄 Validation du compte ID: " + id_user);
        changerStatut(id_user, "VALIDE");
        System.out.println("✅ Compte ID " + id_user + " validé");
    }

    /**
     * Rejette un compte utilisateur en passant son statut à 'REJETE'.
     *
     * @param id_user Identifiant de l'utilisateur à rejeter
     */
    public void rejeterCompte(int id_user) {
        changerStatut(id_user, "REJETE");
    }

    private void changerStatut(int id_user, String statut) {
        String sql = "UPDATE utilisateurs SET statut = ? WHERE id_user = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut);
            ps.setInt(2, id_user);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    /**
     * Récupère la liste des utilisateurs dont le compte est EN_ATTENTE de validation.
     *
     * @return Liste des utilisateurs à valider (ou liste vide si aucun)
     */
    public List<Utilisateur> getUtilisateursEnAttente() {
        return getUtilisateursParStatut("EN_ATTENTE");
    }

    /**
     * Récupère tous les enseignants dont le compte est VALIDE.
     *
     * @return Liste des enseignants actifs
     */
    public List<Utilisateur> getEnseignants() {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs WHERE role = 'ENSEIGNANT' AND statut = 'VALIDE'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) liste.add(construireUtilisateur(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Récupère tous les responsables de classe dont le compte est VALIDE.
     *
     * @return Liste des responsables actifs
     */
    public List<Utilisateur> getResponsables() {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs WHERE role = 'RESPONSABLE_CLASSE' AND statut = 'VALIDE'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) liste.add(construireUtilisateur(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Récupère tous les utilisateurs actifs (statut VALIDE), tous rôles confondus.
     *
     * @return Liste de tous les utilisateurs validés
     */
    public List<Utilisateur> getUtilisateursActifs() {
        return getUtilisateursParStatut("VALIDE");
    }

    private List<Utilisateur> getUtilisateursParStatut(String statut) {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs WHERE statut = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(construireUtilisateur(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Récupère un utilisateur par son identifiant unique.
     *
     * @param id_user Identifiant de l'utilisateur recherché
     * @return L'utilisateur trouvé, ou null s'il n'existe pas
     */
    public Utilisateur getById(int id_user) {
        String sql = "SELECT * FROM utilisateurs WHERE id_user = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id_user);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return construireUtilisateur(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Construit l'objet utilisateur approprié à partir d'une ligne du ResultSet.
     * Instancie {@link Enseignant}, {@link ResponsableClasse} ou {@link ChefDepartement}
     * selon le rôle lu en BDD. Utilise {@link Role#fromString} pour la conversion.
     *
     * @param rs Le ResultSet positionné sur la ligne à lire
     * @return L'objet Utilisateur du bon type
     * @throws SQLException en cas d'erreur de lecture du ResultSet
     */
    private Utilisateur construireUtilisateur(ResultSet rs) throws SQLException {
        int id_user        = rs.getInt("id_user");
        String nom_user    = rs.getString("nom_user");
        String prenom_user = rs.getString("prenom_user");
        String email       = rs.getString("email");
        String statut      = rs.getString("statut");

        // SQLite stocke les dates en TEXT "yyyy-MM-dd HH:mm:ss", getTimestamp()
        // peut planter selon la version du driver. On lit en String pour être sûr.
        String rawCreation    = rs.getString("date_creation");
        String rawInscription = rs.getString("date_inscription");
        LocalDateTime date_creation    = parseDatetime(rawCreation);
        LocalDateTime date_inscription = parseDatetime(rawInscription);

        Role role = Role.fromString(rs.getString("role"));

        if (role == Role.ENSEIGNANT) {
            return new Enseignant(id_user, nom_user, prenom_user, email,
                                  statut, date_creation, date_inscription);
        } else if (role == Role.RESPONSABLE_CLASSE) {
            return new ResponsableClasse(id_user, nom_user, prenom_user, email,
                                         statut, date_creation, date_inscription);
        } else if (role == Role.CHEF_DEPARTEMENT) {
            return new ChefDepartement(id_user, nom_user, prenom_user, email,
                                       statut, date_creation, date_inscription);
        } else {
            return new Utilisateur(id_user, nom_user, prenom_user, email,
                                   role, statut, date_creation, date_inscription);
        }
    }

    /** Parse une chaîne SQLite "yyyy-MM-dd HH:mm:ss" en LocalDateTime. */
    private static LocalDateTime parseDatetime(String raw) {
        if (raw == null || raw.isBlank()) return LocalDateTime.now();
        try {
            // Cas "yyyy-MM-dd HH:mm:ss" → remplace l'espace par 'T' pour ISO
            return LocalDateTime.parse(raw.trim().replace(" ", "T"));
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
    public ObservableList<Utilisateur> getAllValidUsers() {
        ObservableList<Utilisateur> users = FXCollections.observableArrayList();
        String query = "SELECT * FROM utilisateurs WHERE statut = 'VALIDE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(construireUtilisateur(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}