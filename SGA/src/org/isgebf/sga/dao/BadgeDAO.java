package org.isgebf.sga.dao;

import org.isgebf.sga.model.Badge;
import org.isgebf.sga.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DAO for Badge management
 */
public class BadgeDAO {

    public Badge createAndAssociate(Badge b) throws SQLException {
        final String insert = "INSERT INTO BADGE (NUM_BADGE, DATE_EMISSION, DATE_EXPIRATION, STATUT, ID_PERSONNE) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setString(1, b.getNumBadge());
            if (b.getDateEmission() != null) ps.setDate(2, new java.sql.Date(b.getDateEmission().getTime())); else ps.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            if (b.getDateExpiration() != null) ps.setDate(3, new java.sql.Date(b.getDateExpiration().getTime())); else ps.setNull(3, Types.DATE);
            ps.setString(4, b.getStatut());
            ps.setInt(5, b.getIdPersonne());
            int updated = ps.executeUpdate();
            if (updated == 0) throw new SQLException("Creating badge failed, no rows affected.");
        }
        return b;
    }

    public boolean deleteLogic(String numBadge) throws SQLException {
        final String sql = "UPDATE BADGE SET STATUT = 'REVOQUE' WHERE NUM_BADGE = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numBadge);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Badge> findActiveBadges() throws SQLException {
        final String sql = "SELECT NUM_BADGE, DATE_EMISSION, DATE_EXPIRATION, STATUT, ID_PERSONNE FROM BADGE WHERE STATUT = 'ACTIF'";
        List<Badge> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Badge b = mapBadge(rs);
                list.add(b);
            }
        }
        return list;
    }

    public List<Badge> findExpiredBadges() throws SQLException {
        final String sql = "SELECT NUM_BADGE, DATE_EMISSION, DATE_EXPIRATION, STATUT, ID_PERSONNE FROM BADGE WHERE DATE_EXPIRATION IS NOT NULL AND DATE_EXPIRATION < TRUNC(SYSDATE)";
        List<Badge> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Badge b = mapBadge(rs);
                list.add(b);
            }
        }
        return list;
    }

    public List<Badge> findRevokedBadges() throws SQLException {
        final String sql = "SELECT NUM_BADGE, DATE_EMISSION, DATE_EXPIRATION, STATUT, ID_PERSONNE FROM BADGE WHERE STATUT = 'REVOQUE'";
        List<Badge> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Badge b = mapBadge(rs);
                list.add(b);
            }
        }
        return list;
    }

    private Badge mapBadge(ResultSet rs) throws SQLException {
        Badge b = new Badge();
        b.setNumBadge(rs.getString("NUM_BADGE"));
        Date d1 = rs.getDate("DATE_EMISSION");
        if (d1 != null) b.setDateEmission(new Date(d1.getTime()));
        Date d2 = rs.getDate("DATE_EXPIRATION");
        if (d2 != null) b.setDateExpiration(new Date(d2.getTime()));
        b.setStatut(rs.getString("STATUT"));
        b.setIdPersonne(rs.getInt("ID_PERSONNE"));
        return b;
    }
}
