package org.isgebf.sga.dao;

import org.isgebf.sga.model.Personne;
import org.isgebf.sga.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO providing CRUD operations for Personne.
 */
public class PersonneDAO {

    public Personne create(Personne p) throws SQLException {
        final String sql = "INSERT INTO PERSONNE (ID_PERSONNE, NOM, PRENOM, DATE_NAISSANCE, FONCTION, LOGIN, MOT_DE_PASSE, STATUT, ID_PROFIL) VALUES (PERSONNE_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING ID_PERSONNE INTO ?";
        // Note: RETURNING INTO with Oracle requires use of CallableStatement or specific handling. We'll use a simple INSERT and then query by unique login.
        final String insert = "INSERT INTO PERSONNE (NOM, PRENOM, DATE_NAISSANCE, FONCTION, LOGIN, MOT_DE_PASSE, STATUT, ID_PROFIL) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        final String getId = "SELECT ID_PERSONNE FROM PERSONNE WHERE LOGIN = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            if (p.getDateNaissance() != null) ps.setDate(3, new java.sql.Date(p.getDateNaissance().getTime())); else ps.setNull(3, Types.DATE);
            ps.setString(4, p.getFonction());
            ps.setString(5, p.getLogin());
            ps.setString(6, p.getMotDePasse());
            ps.setString(7, p.getStatut());
            if (p.getIdProfil() != null) ps.setInt(8, p.getIdProfil()); else ps.setNull(8, Types.INTEGER);
            int updated = ps.executeUpdate();
            if (updated == 0) throw new SQLException("Creating personne failed, no rows affected.");

            try (PreparedStatement ps2 = conn.prepareStatement(getId)) {
                ps2.setString(1, p.getLogin());
                try (ResultSet rs = ps2.executeQuery()) {
                    if (rs.next()) {
                        p.setIdPersonne(rs.getInt(1));
                    }
                }
            }
        }
        return p;
    }

    public boolean update(Personne p) throws SQLException {
        final String sql = "UPDATE PERSONNE SET NOM = ?, PRENOM = ?, DATE_NAISSANCE = ?, FONCTION = ?, LOGIN = ?, MOT_DE_PASSE = ?, STATUT = ?, ID_PROFIL = ? WHERE ID_PERSONNE = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            if (p.getDateNaissance() != null) ps.setDate(3, new java.sql.Date(p.getDateNaissance().getTime())); else ps.setNull(3, Types.DATE);
            ps.setString(4, p.getFonction());
            ps.setString(5, p.getLogin());
            ps.setString(6, p.getMotDePasse());
            ps.setString(7, p.getStatut());
            if (p.getIdProfil() != null) ps.setInt(8, p.getIdProfil()); else ps.setNull(8, Types.INTEGER);
            ps.setInt(9, p.getIdPersonne());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteLogic(int idPersonne) throws SQLException {
        final String sql = "UPDATE PERSONNE SET STATUT = 'INACTIF' WHERE ID_PERSONNE = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPersonne);
            return ps.executeUpdate() > 0;
        }
    }

    public Personne findById(int id) throws SQLException {
        final String sql = "SELECT ID_PERSONNE, NOM, PRENOM, DATE_NAISSANCE, FONCTION, LOGIN, MOT_DE_PASSE, STATUT, ID_PROFIL FROM PERSONNE WHERE ID_PERSONNE = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Personne p = new Personne();
                    p.setIdPersonne(rs.getInt("ID_PERSONNE"));
                    p.setNom(rs.getString("NOM"));
                    p.setPrenom(rs.getString("PRENOM"));
                    Date d = rs.getDate("DATE_NAISSANCE");
                    if (d != null) p.setDateNaissance(new java.util.Date(d.getTime()));
                    p.setFonction(rs.getString("FONCTION"));
                    p.setLogin(rs.getString("LOGIN"));
                    p.setMotDePasse(rs.getString("MOT_DE_PASSE"));
                    p.setStatut(rs.getString("STATUT"));
                    int idProfil = rs.getInt("ID_PROFIL");
                    if (!rs.wasNull()) p.setIdProfil(idProfil);
                    return p;
                }
            }
        }
        return null;
    }

    public List<Personne> searchByNameOrFonction(String term) throws SQLException {
        final String sql = "SELECT ID_PERSONNE, NOM, PRENOM, DATE_NAISSANCE, FONCTION, LOGIN, MOT_DE_PASSE, STATUT, ID_PROFIL FROM PERSONNE WHERE (LOWER(NOM) LIKE ? OR LOWER(PRENOM) LIKE ? OR LOWER(FONCTION) LIKE ?) ORDER BY NOM, PRENOM";
        List<Personne> result = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + term.toLowerCase() + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Personne p = new Personne();
                    p.setIdPersonne(rs.getInt("ID_PERSONNE"));
                    p.setNom(rs.getString("NOM"));
                    p.setPrenom(rs.getString("PRENOM"));
                    Date d = rs.getDate("DATE_NAISSANCE");
                    if (d != null) p.setDateNaissance(new java.util.Date(d.getTime()));
                    p.setFonction(rs.getString("FONCTION"));
                    p.setLogin(rs.getString("LOGIN"));
                    p.setMotDePasse(rs.getString("MOT_DE_PASSE"));
                    p.setStatut(rs.getString("STATUT"));
                    int idProfil = rs.getInt("ID_PROFIL");
                    if (!rs.wasNull()) p.setIdProfil(idProfil);
                    result.add(p);
                }
            }
        }
        return result;
    }

    public List<Personne> getAllActive() throws SQLException {
        final String sql = "SELECT ID_PERSONNE, NOM, PRENOM, DATE_NAISSANCE, FONCTION, LOGIN, MOT_DE_PASSE, STATUT, ID_PROFIL FROM PERSONNE WHERE STATUT = 'ACTIF' ORDER BY NOM, PRENOM";
        List<Personne> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Personne p = new Personne();
                p.setIdPersonne(rs.getInt("ID_PERSONNE"));
                p.setNom(rs.getString("NOM"));
                p.setPrenom(rs.getString("PRENOM"));
                Date d = rs.getDate("DATE_NAISSANCE");
                if (d != null) p.setDateNaissance(new java.util.Date(d.getTime()));
                p.setFonction(rs.getString("FONCTION"));
                p.setLogin(rs.getString("LOGIN"));
                p.setMotDePasse(rs.getString("MOT_DE_PASSE"));
                p.setStatut(rs.getString("STATUT"));
                int idProfil = rs.getInt("ID_PROFIL");
                if (!rs.wasNull()) p.setIdProfil(idProfil);
                list.add(p);
            }
        }
        return list;
    }
}
