package sga.dao;

import sga.model.Badge;
import sga.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class BadgeDAOImpl implements BadgeDAO {

    @Override
    public Badge findById(int id) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = DBConnection.getConnection();
            ps = c.prepareStatement("SELECT id, code, id_personne, statut FROM BADGE WHERE id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return new Badge(rs.getInt("id"), rs.getString("code"), (rs.getObject("id_personne") == null) ? null : rs.getInt("id_personne"), rs.getString("statut"));
            return null;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public List<Badge> findAll() throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = DBConnection.getConnection();
            ps = c.prepareStatement("SELECT id, code, id_personne, statut FROM BADGE");
            rs = ps.executeQuery();
            List<Badge> list = new ArrayList<>();
            while (rs.next()) list.add(new Badge(rs.getInt("id"), rs.getString("code"), (rs.getObject("id_personne") == null) ? null : rs.getInt("id_personne"), rs.getString("statut")));
            return list;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public void insert(Badge b) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        PreparedStatement deactivate = null;
        ResultSet keys = null;
        try {
            c = DBConnection.getConnection();
            // If the badge is ACTIF for a person, ensure uniqueness: deactivate others
            if (Badge.Statut.ACTIF.equals(b.getStatut()) && b.getPersonneId() != null) {
                deactivate = c.prepareStatement("UPDATE BADGE SET statut = ? WHERE id_personne = ? AND statut = ?");
                deactivate.setString(1, Badge.Statut.INACTIF);
                deactivate.setInt(2, b.getPersonneId());
                deactivate.setString(3, Badge.Statut.ACTIF);
                deactivate.executeUpdate();
                deactivate.close();
            }

            String sql = "INSERT INTO BADGE (id, code, id_personne, statut) VALUES (BADGE_SEQ.NEXTVAL, ?, ?, ?)";
            ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, b.getCode());
            if (b.getPersonneId() != null) ps.setInt(2, b.getPersonneId()); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, b.getStatut() == null ? Badge.Statut.ACTIF : b.getStatut());
            ps.executeUpdate();
            try { keys = ps.getGeneratedKeys(); if (keys != null && keys.next()) b.setId(keys.getInt(1)); } catch (SQLException ignored) {}
            c.commit();
        } catch (SQLException ex) {
            if (c != null) c.rollback();
            throw ex;
        } finally {
            if (keys != null) try { keys.close(); } catch (SQLException ignored) {}
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
            if (deactivate != null) try { deactivate.close(); } catch (SQLException ignored) {}
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public void update(Badge b) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        PreparedStatement deactivate = null;
        try {
            c = DBConnection.getConnection();
            // If setting this badge ACTIF, deactivate others for the same person
            if (Badge.Statut.ACTIF.equals(b.getStatut()) && b.getPersonneId() != null) {
                deactivate = c.prepareStatement("UPDATE BADGE SET statut = ? WHERE id_personne = ? AND statut = ? AND id <> ?");
                deactivate.setString(1, Badge.Statut.INACTIF);
                deactivate.setInt(2, b.getPersonneId());
                deactivate.setString(3, Badge.Statut.ACTIF);
                deactivate.setInt(4, b.getId());
                deactivate.executeUpdate();
                deactivate.close();
            }

            ps = c.prepareStatement("UPDATE BADGE SET code = ?, id_personne = ?, statut = ? WHERE id = ?");
            ps.setString(1, b.getCode());
            if (b.getPersonneId() != null) ps.setInt(2, b.getPersonneId()); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, b.getStatut() == null ? Badge.Statut.INACTIF : b.getStatut());
            ps.setInt(4, b.getId());
            ps.executeUpdate();
            c.commit();
        } catch (SQLException ex) {
            if (c != null) c.rollback();
            throw ex;
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
            if (deactivate != null) try { deactivate.close(); } catch (SQLException ignored) {}
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = DBConnection.getConnection();
            // suppression logique : on marque le badge INACTIF
            ps = c.prepareStatement("UPDATE BADGE SET statut = ? WHERE id = ?");
            ps.setString(1, Badge.Statut.INACTIF);
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
