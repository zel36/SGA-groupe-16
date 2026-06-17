package sga.dao;

import sga.model.HistoriqueEvenement;
import sga.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation JDBC du DAO HistoriqueEvenementDAO.
 */
public class HistoriqueEvenementDAOImpl implements HistoriqueEvenementDAO {

    @Override
    public List<HistoriqueEvenement> findAll() throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(
                    "SELECT id_evenement, type_evenement, date_evenement, statut_resultat, description_justification, num_badge, id_acces, id_lieu, id_personne, effectue_par FROM HISTORIQUE_EVENEMENT ORDER BY date_evenement DESC");
            ResultSet rs = ps.executeQuery();
            List<HistoriqueEvenement> list = new ArrayList<HistoriqueEvenement>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public List<HistoriqueEvenement> findByType(String type) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(
                    "SELECT id_evenement, type_evenement, date_evenement, statut_resultat, description_justification, num_badge, id_acces, id_lieu, id_personne, effectue_par FROM HISTORIQUE_EVENEMENT WHERE type_evenement = ? ORDER BY date_evenement DESC");
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            List<HistoriqueEvenement> list = new ArrayList<HistoriqueEvenement>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public List<HistoriqueEvenement> findByPeriode(Date debut, Date fin) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(
                    "SELECT id_evenement, type_evenement, date_evenement, statut_resultat, description_justification, num_badge, id_acces, id_lieu, id_personne, effectue_par FROM HISTORIQUE_EVENEMENT WHERE date_evenement BETWEEN ? AND ? ORDER BY date_evenement DESC");
            ps.setTimestamp(1, debut == null ? null : new Timestamp(debut.getTime()));
            ps.setTimestamp(2, fin == null ? null : new Timestamp(fin.getTime()));
            ResultSet rs = ps.executeQuery();
            List<HistoriqueEvenement> list = new ArrayList<HistoriqueEvenement>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public int compterAccordes(Date debut, Date fin) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(
                    "SELECT COUNT(*) FROM HISTORIQUE_EVENEMENT WHERE statut_resultat = 'ACCORDE' AND date_evenement BETWEEN ? AND ?");
            ps.setTimestamp(1, debut == null ? null : new Timestamp(debut.getTime()));
            ps.setTimestamp(2, fin == null ? null : new Timestamp(fin.getTime()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public int compterRefuses(Date debut, Date fin) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(
                    "SELECT COUNT(*) FROM HISTORIQUE_EVENEMENT WHERE statut_resultat = 'REFUSE' AND date_evenement BETWEEN ? AND ?");
            ps.setTimestamp(1, debut == null ? null : new Timestamp(debut.getTime()));
            ps.setTimestamp(2, fin == null ? null : new Timestamp(fin.getTime()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    private HistoriqueEvenement mapRow(ResultSet rs) throws SQLException {
        HistoriqueEvenement h = new HistoriqueEvenement();
        h.setIdEvenement(rs.getInt("id_evenement"));
        h.setTypeEvenement(rs.getString("type_evenement"));
        Timestamp ts = rs.getTimestamp("date_evenement");
        if (ts != null) {
            h.setDateEvenement(new Timestamp(ts.getTime()));
        }
        h.setStatutResultat(rs.getString("statut_resultat"));
        h.setDescriptionJustification(rs.getString("description_justification"));
        h.setNumBadge(rs.getString("num_badge"));
        int idLieu = rs.getInt("id_lieu");
        if (rs.wasNull()) {
            h.setIdLieu(null);
        } else {
            h.setIdLieu(idLieu);
        }
        int idPersonne = rs.getInt("id_personne");
        if (rs.wasNull()) {
            h.setIdPersonne(null);
        } else {
            h.setIdPersonne(idPersonne);
        }
        h.setEffectuePar(rs.getString("effectue_par"));
        return h;
    }

}
