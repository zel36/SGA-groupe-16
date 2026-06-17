package sga.dao;

import sga.model.Personne;
import sga.util.DBConnection;
import org.isge.sga.model.entity.Profil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation basique JDBC de PersonneDAO.
 * Les noms de colonnes/tables doivent correspondre à votre schéma Oracle.
 */
public class PersonneDAOImpl implements PersonneDAO {

    @Override
    public Personne findById(int id) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = DBConnection.getConnection();
            String sql = "SELECT p.id_personne, p.nom, p.prenom, p.statut, p.id_profil, pr.libelle_profil " +
                    "FROM PERSONNE p INNER JOIN PROFIL pr ON p.id_profil = pr.id_profil " +
                    "WHERE p.id_personne = ?";
            ps = c.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Profil profil = new Profil(rs.getLong("id_profil"), rs.getString("libelle_profil"));
                Personne p = new Personne(rs.getInt("id_personne"), rs.getString("nom"), rs.getString("prenom"), profil, rs.getString("statut"));
                return p;
            }
            return null;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public List<Personne> findAll() throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = DBConnection.getConnection();
            String sql = "SELECT p.id_personne, p.nom, p.prenom, p.statut, p.id_profil, pr.libelle_profil " +
                    "FROM PERSONNE p INNER JOIN PROFIL pr ON p.id_profil = pr.id_profil";
            ps = c.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Personne> list = new ArrayList<>();
            while (rs.next()) {
                Profil profil = new Profil(rs.getLong("id_profil"), rs.getString("libelle_profil"));
                list.add(new Personne(rs.getInt("id_personne"), rs.getString("nom"), rs.getString("prenom"), profil, rs.getString("statut")));
            }
            return list;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public void insert(Personne p) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = DBConnection.getConnection();
            String sql = "INSERT INTO PERSONNE (id_personne, nom, prenom, id_profil, statut) VALUES (PERSONNE_SEQ.NEXTVAL, ?, ?, ?, ?)";
            ps = c.prepareStatement(sql);
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            if (p.getProfil() != null && p.getProfil().getId() != null) {
                ps.setLong(3, p.getProfil().getId());
            } else {
                ps.setNull(3, Types.BIGINT);
            }
            String statut = (p.getStatut() == null) ? "ACTIF" : p.getStatut();
            ps.setString(4, statut);
            ps.executeUpdate();
            c.commit();
        } catch (SQLException ex) {
            if (c != null) c.rollback();
            throw ex;
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public void update(Personne p) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = DBConnection.getConnection();
            String sql = "UPDATE PERSONNE SET nom = ?, prenom = ?, id_profil = ?, statut = ? WHERE id_personne = ?";
            ps = c.prepareStatement(sql);
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            if (p.getProfil() != null && p.getProfil().getId() != null) {
                ps.setLong(3, p.getProfil().getId());
            } else {
                ps.setNull(3, Types.BIGINT);
            }
            ps.setString(4, p.getStatut() == null ? "ACTIF" : p.getStatut());
            ps.setInt(5, p.getId());
            ps.executeUpdate();
            c.commit();
        } catch (SQLException ex) {
            if (c != null) c.rollback();
            throw ex;
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = DBConnection.getConnection();
            // Suppression logique : on marque le statut en INACTIF
            ps = c.prepareStatement("UPDATE PERSONNE SET statut = 'INACTIF' WHERE id_personne = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            c.commit();
        } catch (SQLException ex) {
            if (c != null) c.rollback();
            throw ex;
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
            DBConnection.closeQuietly(c);
        }
    }
}
