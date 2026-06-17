package org.isgebf.sga.dao;

import org.isgebf.sga.model.Lieu;
import org.isgebf.sga.util.DBConnection;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DAO for Lieu (locations)
 */
public class LieuDAO {

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    public Lieu create(Lieu l) throws SQLException {
        final String insert = "INSERT INTO LIEU (LIBELLE_LIEU, EMPLACEMENT, CAPACITE_MAX, HEURE_OUVERTURE, HEURE_FERMETURE, STATUT) VALUES (?, ?, ?, ?, ?, ?)";
        final String getId = "SELECT ID_LIEU FROM LIEU WHERE LIBELLE_LIEU = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setString(1, l.getLibelleLieu());
            ps.setString(2, l.getEmplacement());
            ps.setInt(3, l.getCapaciteMax());
            ps.setString(4, l.getHeureOuverture());
            ps.setString(5, l.getHeureFermeture());
            ps.setString(6, l.getStatut());
            int updated = ps.executeUpdate();
            if (updated == 0) throw new SQLException("Creating lieu failed, no rows affected.");
            try (PreparedStatement ps2 = conn.prepareStatement(getId)) {
                ps2.setString(1, l.getLibelleLieu());
                try (ResultSet rs = ps2.executeQuery()) {
                    if (rs.next()) l.setIdLieu(rs.getInt(1));
                }
            }
        }
        return l;
    }

    public boolean update(Lieu l) throws SQLException {
        final String sql = "UPDATE LIEU SET LIBELLE_LIEU = ?, EMPLACEMENT = ?, CAPACITE_MAX = ?, HEURE_OUVERTURE = ?, HEURE_FERMETURE = ?, STATUT = ? WHERE ID_LIEU = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, l.getLibelleLieu());
            ps.setString(2, l.getEmplacement());
            ps.setInt(3, l.getCapaciteMax());
            ps.setString(4, l.getHeureOuverture());
            ps.setString(5, l.getHeureFermeture());
            ps.setString(6, l.getStatut());
            ps.setInt(7, l.getIdLieu());
            return ps.executeUpdate() > 0;
        }
    }

    public Lieu findById(int id) throws SQLException {
        final String sql = "SELECT ID_LIEU, LIBELLE_LIEU, EMPLACEMENT, CAPACITE_MAX, HEURE_OUVERTURE, HEURE_FERMETURE, STATUT FROM LIEU WHERE ID_LIEU = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Lieu l = new Lieu();
                    l.setIdLieu(rs.getInt("ID_LIEU"));
                    l.setLibelleLieu(rs.getString("LIBELLE_LIEU"));
                    l.setEmplacement(rs.getString("EMPLACEMENT"));
                    l.setCapaciteMax(rs.getInt("CAPACITE_MAX"));
                    l.setHeureOuverture(rs.getString("HEURE_OUVERTURE"));
                    l.setHeureFermeture(rs.getString("HEURE_FERMETURE"));
                    l.setStatut(rs.getString("STATUT"));
                    return l;
                }
            }
        }
        return null;
    }

    /**
     * Returns all lieux with real-time computed statut (OUVERT/FERME) depending on current time and configured hours.
     */
    public List<Lieu> getAllWithRealTimeStatus() throws SQLException {
        final String sql = "SELECT ID_LIEU, LIBELLE_LIEU, EMPLACEMENT, CAPACITE_MAX, HEURE_OUVERTURE, HEURE_FERMETURE, STATUT FROM LIEU ORDER BY LIBELLE_LIEU";
        List<Lieu> list = new ArrayList<>();
        Date now = new Date();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Lieu l = new Lieu();
                l.setIdLieu(rs.getInt("ID_LIEU"));
                l.setLibelleLieu(rs.getString("LIBELLE_LIEU"));
                l.setEmplacement(rs.getString("EMPLACEMENT"));
                l.setCapaciteMax(rs.getInt("CAPACITE_MAX"));
                String ouverture = rs.getString("HEURE_OUVERTURE");
                String fermeture = rs.getString("HEURE_FERMETURE");
                l.setHeureOuverture(ouverture);
                l.setHeureFermeture(fermeture);
                String dbStatut = rs.getString("STATUT");

                // compute realtime status
                boolean ouvert = true;
                if (ouverture != null && fermeture != null) {
                    try {
                        Date hO = TIME_FORMAT.parse(ouverture);
                        Date hF = TIME_FORMAT.parse(fermeture);
                        // Build today times by replacing date part with today
                        Date todayO = mergeDateWithTime(now, hO);
                        Date todayF = mergeDateWithTime(now, hF);
                        if (todayO.after(todayF)) {
                            // overnight schedule (e.g., 20:00 - 06:00)
                            ouvert = !(now.after(todayF) && now.before(todayO));
                        } else {
                            ouvert = !(now.before(todayO) || now.after(todayF));
                        }
                    } catch (ParseException e) {
                        // if parse fails, fall back to DB statut
                        ouvert = "ACTIF".equalsIgnoreCase(dbStatut);
                    }
                }
                l.setStatut(ouvert ? "OUVERT" : "FERME");
                list.add(l);
            }
        }
        return list;
    }

    private Date mergeDateWithTime(Date date, Date time) {
        // return date with time fields copied from 'time'
        java.util.Calendar cDate = java.util.Calendar.getInstance();
        cDate.setTime(date);
        java.util.Calendar cTime = java.util.Calendar.getInstance();
        cTime.setTime(time);
        cDate.set(java.util.Calendar.HOUR_OF_DAY, cTime.get(java.util.Calendar.HOUR_OF_DAY));
        cDate.set(java.util.Calendar.MINUTE, cTime.get(java.util.Calendar.MINUTE));
        cDate.set(java.util.Calendar.SECOND, 0);
        cDate.set(java.util.Calendar.MILLISECOND, 0);
        return cDate.getTime();
    }
}
