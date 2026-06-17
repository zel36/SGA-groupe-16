package org.isgebf.sga.dao;

import org.isgebf.sga.model.Profil;
import org.isgebf.sga.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Profil management
 */
public class ProfilDAO {

    public Profil create(Profil profil) throws SQLException {
        final String insert = "INSERT INTO PROFIL (LIBELLE_PROFIL, DESCRIPTION, DATE_CREATION, STATUT) VALUES (?, ?, ?, ?)";
        final String getId = "SELECT ID_PROFIL FROM PROFIL WHERE LIBELLE_PROFIL = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setString(1, profil.getLibelleProfil());
            ps.setString(2, profil.getDescription());
            if (profil.getDateCreation() != null) ps.setDate(3, new java.sql.Date(profil.getDateCreation().getTime())); else ps.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            ps.setString(4, profil.getStatut());
            int updated = ps.executeUpdate();
            if (updated == 0) throw new SQLException("Creating profil failed, no rows affected.");

            try (PreparedStatement ps2 = conn.prepareStatement(getId)) {
                ps2.setString(1, profil.getLibelleProfil());
                try (ResultSet rs = ps2.executeQuery()) {
                    if (rs.next()) profil.setIdProfil(rs.getInt(1));
                }
            }
        }
        return profil;
    }

    public boolean update(Profil profil) throws SQLException {
        final String sql = "UPDATE PROFIL SET LIBELLE_PROFIL = ?, DESCRIPTION = ?, DATE_CREATION = ?, STATUT = ? WHERE ID_PROFIL = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, profil.getLibelleProfil());
            ps.setString(2, profil.getDescription());
            if (profil.getDateCreation() != null) ps.setDate(3, new java.sql.Date(profil.getDateCreation().getTime())); else ps.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            ps.setString(4, profil.getStatut());
            ps.setInt(5, profil.getIdProfil());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteLogic(int idProfil) throws SQLException {
        final String sql = "UPDATE PROFIL SET STATUT = 'INACTIF' WHERE ID_PROFIL = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProfil);
            return ps.executeUpdate() > 0;
        }
    }

    public Profil findById(int id) throws SQLException {
        final String sql = "SELECT ID_PROFIL, LIBELLE_PROFIL, DESCRIPTION, DATE_CREATION, STATUT FROM PROFIL WHERE ID_PROFIL = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Profil p = new Profil();
                    p.setIdProfil(rs.getInt("ID_PROFIL"));
                    p.setLibelleProfil(rs.getString("LIBELLE_PROFIL"));
                    p.setDescription(rs.getString("DESCRIPTION"));
                    Date d = rs.getDate("DATE_CREATION");
                    if (d != null) p.setDateCreation(new java.util.Date(d.getTime()));
                    p.setStatut(rs.getString("STATUT"));
                    return p;
                }
            }
        }
        return null;
    }

    public List<Profil> getAllActive() throws SQLException {
        final String sql = "SELECT ID_PROFIL, LIBELLE_PROFIL, DESCRIPTION, DATE_CREATION, STATUT FROM PROFIL WHERE STATUT = 'ACTIF' ORDER BY LIBELLE_PROFIL";
        List<Profil> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Profil p = new Profil();
                p.setIdProfil(rs.getInt("ID_PROFIL"));
                p.setLibelleProfil(rs.getString("LIBELLE_PROFIL"));
                p.setDescription(rs.getString("DESCRIPTION"));
                Date d = rs.getDate("DATE_CREATION");
                if (d != null) p.setDateCreation(new java.util.Date(d.getTime()));
                p.setStatut(rs.getString("STATUT"));
                list.add(p);
            }
        }
        return list;
    }
}
