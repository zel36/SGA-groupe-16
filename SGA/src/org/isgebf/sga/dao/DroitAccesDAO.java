package org.isgebf.sga.dao;

import org.isgebf.sga.model.DroitAcces;
import org.isgebf.sga.util.DBConnection;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for managing access rights (DroitAcces)
 */
public class DroitAccesDAO {

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    public DroitAcces addDroit(DroitAcces d) throws SQLException {
        final String insert = "INSERT INTO DROIT_ACCES (ID_PROFIL, ID_LIEU, JOUR_SEMAINE, HEURE_DEBUT, HEURE_FIN) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getIdProfil());
            ps.setInt(2, d.getIdLieu());
            ps.setInt(3, d.getJourSemaine());
            ps.setString(4, d.getHeureDebut());
            ps.setString(5, d.getHeureFin());
            int updated = ps.executeUpdate();
            if (updated == 0) throw new SQLException("Creating droit failed, no rows affected.");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) d.setIdDroit(rs.getInt(1));
            }
        }
        return d;
    }

    /**
     * Logical removal of a droit. Sets STATUT='INACTIF' if STATUT column exists, otherwise deletes.
     */
    public boolean removeDroit(int idDroit) throws SQLException {
        final String update = "UPDATE DROIT_ACCES SET STATUT = 'INACTIF' WHERE ID_DROIT = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(update)) {
            ps.setInt(1, idDroit);
            int changed = ps.executeUpdate();
            if (changed > 0) return true;
        }
        // Fallback: physical delete if STATUT column not present
        final String delete = "DELETE FROM DROIT_ACCES WHERE ID_DROIT = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(delete)) {
            ps.setInt(1, idDroit);
            return ps.executeUpdate() > 0;
        }
    }

    public List<DroitAcces> findDroitsByProfil(int idProfil) throws SQLException {
        final String sql = "SELECT ID_DROIT, ID_PROFIL, ID_LIEU, JOUR_SEMAINE, HEURE_DEBUT, HEURE_FIN FROM DROIT_ACCES WHERE ID_PROFIL = ? AND (STATUT IS NULL OR STATUT != 'INACTIF')";
        List<DroitAcces> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProfil);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DroitAcces d = new DroitAcces();
                    d.setIdDroit(rs.getInt("ID_DROIT"));
                    d.setIdProfil(rs.getInt("ID_PROFIL"));
                    d.setIdLieu(rs.getInt("ID_LIEU"));
                    d.setJourSemaine(rs.getInt("JOUR_SEMAINE"));
                    d.setHeureDebut(rs.getString("HEURE_DEBUT"));
                    d.setHeureFin(rs.getString("HEURE_FIN"));
                    list.add(d);
                }
            }
        }
        return list;
    }

    /**
     * Checks whether profile has access to lieu at given day/time.
     * @param idProfil profile id
     * @param idLieu lieu id
     * @param currentDay day of week (1..7)
     * @param currentTime time string HH:mm
     * @return true if authorized
     */
    public boolean checkAccessAuthorization(int idProfil, int idLieu, int currentDay, String currentTime) throws SQLException {
        final String sql = "SELECT HEURE_DEBUT, HEURE_FIN FROM DROIT_ACCES WHERE ID_PROFIL = ? AND ID_LIEU = ? AND JOUR_SEMAINE = ? AND (STATUT IS NULL OR STATUT != 'INACTIF')";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProfil);
            ps.setInt(2, idLieu);
            ps.setInt(3, currentDay);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String debut = rs.getString("HEURE_DEBUT");
                    String fin = rs.getString("HEURE_FIN");
                    if (isTimeWithinInterval(currentTime, debut, fin)) return true;
                }
            }
        }
        return false;
    }

    private boolean isTimeWithinInterval(String time, String debut, String fin) {
        try {
            int t = timeToMinutes(time);
            int d = timeToMinutes(debut);
            int f = timeToMinutes(fin);
            if (d <= f) {
                return t >= d && t <= f;
            } else {
                // overnight interval
                return t >= d || t <= f;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private int timeToMinutes(String hhmm) throws ParseException {
        if (hhmm == null) throw new ParseException("null time", 0);
        String[] parts = hhmm.split(":");
        if (parts.length != 2) throw new ParseException(hhmm, 0);
        int h = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);
        return h * 60 + m;
    }
}
