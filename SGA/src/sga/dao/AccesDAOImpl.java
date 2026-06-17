package sga.dao;

import sga.model.Acces;
import sga.model.Lieu;
import sga.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO JDBC pour Acces. Chaque Acces appartient à un seul Lieu (id_lieu FK).
 */
public class AccesDAOImpl implements AccesDAO {

    @Override
    public Acces findById(int id) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = DBConnection.getConnection();
            // LEFT JOIN permet de récupérer le lieu même si la contrainte est temporairement absente
            ps = c.prepareStatement("SELECT a.id, a.code, a.id_lieu, a.statut, l.nom as lieu_nom FROM ACCES a LEFT JOIN LIEU l ON a.id_lieu = l.id WHERE a.id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Acces a = new Acces(rs.getInt("id"), rs.getString("code"), (rs.getObject("id_lieu") == null) ? null : rs.getInt("id_lieu"), rs.getString("statut"));
                return a;
            }
            return null;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public List<Acces> findAll() throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = DBConnection.getConnection();
            ps = c.prepareStatement("SELECT a.id, a.code, a.id_lieu, a.statut, l.nom as lieu_nom FROM ACCES a LEFT JOIN LIEU l ON a.id_lieu = l.id");
            rs = ps.executeQuery();
            List<Acces> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Acces(rs.getInt("id"), rs.getString("code"), (rs.getObject("id_lieu") == null) ? null : rs.getInt("id_lieu"), rs.getString("statut")));
            }
            return list;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public void insert(Acces a) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet keys = null;
        try {
            c = DBConnection.getConnection();
            String sql = "INSERT INTO ACCES (id, code, id_lieu, statut) VALUES (ACCES_SEQ.NEXTVAL, ?, ?, ?)";
            ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, a.getCode());
            if (a.getLieuId() != null) ps.setInt(2, a.getLieuId()); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, a.getStatut() == null ? Acces.Statut.ACTIF : a.getStatut());
            ps.executeUpdate();
            try { keys = ps.getGeneratedKeys(); if (keys != null && keys.next()) a.setId(keys.getInt(1)); } catch (SQLException ignored) {}
            c.commit();
        } catch (SQLException ex) {
            if (c != null) c.rollback();
            throw ex;
        } finally {
            if (keys != null) try { keys.close(); } catch (SQLException ignored) {}
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public void update(Acces a) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = DBConnection.getConnection();
            ps = c.prepareStatement("UPDATE ACCES SET code = ?, id_lieu = ?, statut = ? WHERE id = ?");
            ps.setString(1, a.getCode());
            if (a.getLieuId() != null) ps.setInt(2, a.getLieuId()); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, a.getStatut() == null ? Acces.Statut.ACTIF : a.getStatut());
            ps.setInt(4, a.getId());
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
            ps = c.prepareStatement("UPDATE ACCES SET statut = ? WHERE id = ?");
            ps.setString(1, Acces.Statut.INACTIF);
            ps.setInt(2, id);
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
