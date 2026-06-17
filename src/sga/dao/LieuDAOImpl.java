package sga.dao;

import sga.model.Lieu;
import sga.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LieuDAOImpl implements LieuDAO {

    @Override
    public Lieu findById(int id) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(
                    "SELECT id_lieu, libelle_lieu, emplacement, capacite_max, heure_ouverture, heure_fermeture, statut FROM LIEU WHERE id_lieu = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Lieu l = new Lieu();
                l.setIdLieu(rs.getInt("id_lieu"));
                l.setLibelleLieu(rs.getString("libelle_lieu"));
                l.setEmplacement(rs.getString("emplacement"));
                int cap = rs.getInt("capacite_max");
                if (rs.wasNull()) {
                    l.setCapaciteMax(null);
                } else {
                    l.setCapaciteMax(cap);
                }
                int ho = rs.getInt("heure_ouverture");
                if (rs.wasNull()) {
                    l.setHeureOuverture(null);
                } else {
                    l.setHeureOuverture(ho);
                }
                int hf = rs.getInt("heure_fermeture");
                if (rs.wasNull()) {
                    l.setHeureFermeture(null);
                } else {
                    l.setHeureFermeture(hf);
                }
                l.setStatut(rs.getString("statut"));
                return l;
            }
            return null;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public List<Lieu> findAll() throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(
                    "SELECT id_lieu, libelle_lieu, emplacement, capacite_max, heure_ouverture, heure_fermeture, statut FROM LIEU");
            ResultSet rs = ps.executeQuery();
            List<Lieu> list = new ArrayList<Lieu>();
            while (rs.next()) {
                Lieu l = new Lieu();
                l.setIdLieu(rs.getInt("id_lieu"));
                l.setLibelleLieu(rs.getString("libelle_lieu"));
                l.setEmplacement(rs.getString("emplacement"));
                int cap = rs.getInt("capacite_max");
                if (rs.wasNull()) {
                    l.setCapaciteMax(null);
                } else {
                    l.setCapaciteMax(cap);
                }
                int ho = rs.getInt("heure_ouverture");
                if (rs.wasNull()) {
                    l.setHeureOuverture(null);
                } else {
                    l.setHeureOuverture(ho);
                }
                int hf = rs.getInt("heure_fermeture");
                if (rs.wasNull()) {
                    l.setHeureFermeture(null);
                } else {
                    l.setHeureFermeture(hf);
                }
                l.setStatut(rs.getString("statut"));
                list.add(l);
            }
            return list;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public void insert(Lieu l) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO LIEU (id_lieu, libelle_lieu, emplacement, capacite_max, heure_ouverture, heure_fermeture, statut) VALUES (LIEU_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, l.getLibelleLieu());
            ps.setString(2, l.getEmplacement());
            if (l.getCapaciteMax() != null) {
                ps.setInt(3, l.getCapaciteMax());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            if (l.getHeureOuverture() != null) {
                ps.setInt(4, l.getHeureOuverture());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            if (l.getHeureFermeture() != null) {
                ps.setInt(5, l.getHeureFermeture());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            ps.setString(6, l.getStatut());
            ps.executeUpdate();
            c.commit();
        } catch (SQLException ex) {
            if (c != null) c.rollback();
            throw ex;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public void update(Lieu l) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(
                    "UPDATE LIEU SET libelle_lieu = ?, emplacement = ?, capacite_max = ?, heure_ouverture = ?, heure_fermeture = ?, statut = ? WHERE id_lieu = ?");
            ps.setString(1, l.getLibelleLieu());
            ps.setString(2, l.getEmplacement());
            if (l.getCapaciteMax() != null) {
                ps.setInt(3, l.getCapaciteMax());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            if (l.getHeureOuverture() != null) {
                ps.setInt(4, l.getHeureOuverture());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            if (l.getHeureFermeture() != null) {
                ps.setInt(5, l.getHeureFermeture());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            ps.setString(6, l.getStatut());
            ps.setInt(7, l.getIdLieu());
            ps.executeUpdate();
            c.commit();
        } catch (SQLException ex) {
            if (c != null) c.rollback();
            throw ex;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    /**
     * Bloque le lieu et insère un historique dans une transaction JDBC.
     */
    @Override
    public boolean bloquerLieu(int idLieu, String motif, int idOperateur, String nomOperateur) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. mise à jour statut
            PreparedStatement psUpdate = conn.prepareStatement("UPDATE LIEU SET statut = 'BLOQUE' WHERE id_lieu = ?");
            psUpdate.setInt(1, idLieu);
            psUpdate.executeUpdate();
            psUpdate.close();

            // 2. insertion historique
            PreparedStatement psHist = conn.prepareStatement(
                    "INSERT INTO HISTORIQUE_EVENEMENT(type_evenement, statut_resultat, description_justification, id_lieu, effectue_par) VALUES(?, ?, ?, ?, ?)");
            psHist.setString(1, "BLOCAGE_LIEU");
            psHist.setString(2, "SUCCES");
            psHist.setString(3, motif);
            psHist.setInt(4, idLieu);
            String effectuePar = idOperateur + ":" + (nomOperateur == null ? "" : nomOperateur);
            psHist.setString(5, effectuePar);
            psHist.executeUpdate();
            psHist.close();

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignore) {
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignore) {
                }
            }
        }
    }

    /**
     * Débloque le lieu et insère un historique dans une transaction JDBC.
     */
    @Override
    public boolean debloquerLieu(int idLieu, String justification, int idOperateur, String nomOperateur)
            throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement psUpdate = conn.prepareStatement("UPDATE LIEU SET statut = 'OUVERT' WHERE id_lieu = ?");
            psUpdate.setInt(1, idLieu);
            psUpdate.executeUpdate();
            psUpdate.close();

            PreparedStatement psHist = conn.prepareStatement(
                    "INSERT INTO HISTORIQUE_EVENEMENT(type_evenement, statut_resultat, description_justification, id_lieu, effectue_par) VALUES(?, ?, ?, ?, ?)");
            psHist.setString(1, "DEBLOCAGE_LIEU");
            psHist.setString(2, "SUCCES");
            psHist.setString(3, justification);
            psHist.setInt(4, idLieu);
            String effectuePar = idOperateur + ":" + (nomOperateur == null ? "" : nomOperateur);
            psHist.setString(5, effectuePar);
            psHist.executeUpdate();
            psHist.close();

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignore) {
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignore) {
                }
            }
        }
    }
}
