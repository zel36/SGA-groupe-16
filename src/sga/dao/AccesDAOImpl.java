package sga.dao;

import sga.model.Acces;
import sga.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation JDBC de AccesDAO.
 *
 * Toutes les requêtes utilisent les vraies colonnes de la table ACCES :
 *   id_acces, libelle_acces, type_acces, description, statut, id_lieu
 *
 * Un accès appartient à un seul lieu (FK id_lieu → LIEU.id_lieu).
 * Un lieu peut posséder plusieurs accès.
 */
public class AccesDAOImpl implements AccesDAO {

    // ─── Mapping ResultSet → Acces ────────────────────────────────────────────
    private Acces map(ResultSet rs) throws SQLException {
        Acces a = new Acces();
        a.setIdAcces(rs.getInt("id_acces"));
        a.setLibelleAcces(rs.getString("libelle_acces"));
        a.setTypeAcces(rs.getString("type_acces"));
        a.setDescription(rs.getString("description"));
        a.setStatut(rs.getString("statut"));
        int idLieu = rs.getInt("id_lieu");
        a.setIdLieu(rs.wasNull() ? null : idLieu);
        // libelle_lieu chargé si le JOIN est présent
        try { a.setLibelleLieu(rs.getString("libelle_lieu")); } catch (SQLException ignored) {}
        return a;
    }

    // ─── findById ─────────────────────────────────────────────────────────────
    @Override
    public Acces findById(int idAcces) throws SQLException {
        String sql =
            "SELECT a.id_acces, a.libelle_acces, a.type_acces, a.description, a.statut, " +
            "       a.id_lieu, l.libelle_lieu " +
            "FROM   ACCES a " +
            "LEFT JOIN LIEU l ON a.id_lieu = l.id_lieu " +
            "WHERE  a.id_acces = ?";
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = DBConnection.getConnection();
            ps = c.prepareStatement(sql);
            ps.setInt(1, idAcces);
            rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        } finally {
            close(rs, ps, c);
        }
    }

    // ─── findAll ──────────────────────────────────────────────────────────────
    @Override
    public List<Acces> findAll() throws SQLException {
        String sql =
            "SELECT a.id_acces, a.libelle_acces, a.type_acces, a.description, a.statut, " +
            "       a.id_lieu, l.libelle_lieu " +
            "FROM   ACCES a " +
            "LEFT JOIN LIEU l ON a.id_lieu = l.id_lieu " +
            "ORDER BY a.libelle_acces";
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = DBConnection.getConnection();
            ps = c.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Acces> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } finally {
            close(rs, ps, c);
        }
    }

    // ─── findByFilter ─────────────────────────────────────────────────────────
    @Override
    public List<Acces> findByFilter(Integer idLieu, String typeAcces) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT a.id_acces, a.libelle_acces, a.type_acces, a.description, a.statut, " +
            "       a.id_lieu, l.libelle_lieu " +
            "FROM   ACCES a " +
            "LEFT JOIN LIEU l ON a.id_lieu = l.id_lieu " +
            "WHERE  1=1 ");
        if (idLieu != null)      sql.append("AND a.id_lieu = ? ");
        if (typeAcces != null && !typeAcces.isBlank()) sql.append("AND a.type_acces = ? ");
        sql.append("ORDER BY a.libelle_acces");

        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = DBConnection.getConnection();
            ps = c.prepareStatement(sql.toString());
            int idx = 1;
            if (idLieu != null)      ps.setInt(idx++, idLieu);
            if (typeAcces != null && !typeAcces.isBlank()) ps.setString(idx++, typeAcces.toUpperCase());
            rs = ps.executeQuery();
            List<Acces> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } finally {
            close(rs, ps, c);
        }
    }

    // ─── search ───────────────────────────────────────────────────────────────
    @Override
    public List<Acces> search(String libelleKeyword, String typeAcces, Integer idLieu) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT a.id_acces, a.libelle_acces, a.type_acces, a.description, a.statut, " +
            "       a.id_lieu, l.libelle_lieu " +
            "FROM   ACCES a " +
            "LEFT JOIN LIEU l ON a.id_lieu = l.id_lieu " +
            "WHERE  1=1 ");
        if (libelleKeyword != null && !libelleKeyword.isBlank())
            sql.append("AND UPPER(a.libelle_acces) LIKE ? ");
        if (typeAcces != null && !typeAcces.isBlank())
            sql.append("AND a.type_acces = ? ");
        if (idLieu != null)
            sql.append("AND a.id_lieu = ? ");
        sql.append("ORDER BY a.libelle_acces");

        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = DBConnection.getConnection();
            ps = c.prepareStatement(sql.toString());
            int idx = 1;
            if (libelleKeyword != null && !libelleKeyword.isBlank())
                ps.setString(idx++, "%" + libelleKeyword.toUpperCase() + "%");
            if (typeAcces != null && !typeAcces.isBlank())
                ps.setString(idx++, typeAcces.toUpperCase());
            if (idLieu != null)
                ps.setInt(idx++, idLieu);
            rs = ps.executeQuery();
            List<Acces> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } finally {
            close(rs, ps, c);
        }
    }

    // ─── insert ───────────────────────────────────────────────────────────────
    @Override
    public void insert(Acces acces) throws SQLException {
        // Oracle utilise GENERATED BY DEFAULT AS IDENTITY, pas de séquence manuelle
        String sql =
            "INSERT INTO ACCES (libelle_acces, type_acces, description, statut, id_lieu) " +
            "VALUES (?, ?, ?, ?, ?)";
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet keys = null;
        try {
            c = DBConnection.getConnection();
            ps = c.prepareStatement(sql, new String[]{"id_acces"});
            ps.setString(1, acces.getLibelleAcces());
            ps.setString(2, acces.getTypeAcces());
            ps.setString(3, acces.getDescription());
            ps.setString(4, acces.getStatut() != null ? acces.getStatut() : Acces.STATUT_ACTIF);
            if (acces.getIdLieu() != null) ps.setInt(5, acces.getIdLieu());
            else ps.setNull(5, Types.INTEGER);
            ps.executeUpdate();
            // Récupérer la clé générée
            keys = ps.getGeneratedKeys();
            if (keys != null && keys.next()) {
                acces.setIdAcces(keys.getInt(1));
            }
            c.commit();
        } catch (SQLException ex) {
            if (c != null) try { c.rollback(); } catch (SQLException ignored) {}
            throw ex;
        } finally {
            close(keys, ps, c);
        }
    }

    // ─── update ───────────────────────────────────────────────────────────────
    @Override
    public void update(Acces acces) throws SQLException {
        String sql =
            "UPDATE ACCES SET libelle_acces = ?, type_acces = ?, description = ?, " +
            "                 statut = ?, id_lieu = ? " +
            "WHERE  id_acces = ?";
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = DBConnection.getConnection();
            ps = c.prepareStatement(sql);
            ps.setString(1, acces.getLibelleAcces());
            ps.setString(2, acces.getTypeAcces());
            ps.setString(3, acces.getDescription());
            ps.setString(4, acces.getStatut() != null ? acces.getStatut() : Acces.STATUT_ACTIF);
            if (acces.getIdLieu() != null) ps.setInt(5, acces.getIdLieu());
            else ps.setNull(5, Types.INTEGER);
            ps.setInt(6, acces.getIdAcces());
            int rows = ps.executeUpdate();
            c.commit();
            if (rows == 0) throw new SQLException("Aucun accès trouvé avec id_acces=" + acces.getIdAcces());
        } catch (SQLException ex) {
            if (c != null) try { c.rollback(); } catch (SQLException ignored) {}
            throw ex;
        } finally {
            close(null, ps, c);
        }
    }

    // ─── desactiver ───────────────────────────────────────────────────────────
    @Override
    public void desactiver(int idAcces) throws SQLException {
        String sql = "UPDATE ACCES SET statut = ? WHERE id_acces = ?";
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = DBConnection.getConnection();
            ps = c.prepareStatement(sql);
            ps.setString(1, Acces.STATUT_INACTIF);
            ps.setInt(2, idAcces);
            int rows = ps.executeUpdate();
            c.commit();
            if (rows == 0) throw new SQLException("Aucun accès trouvé avec id_acces=" + idAcces);
        } catch (SQLException ex) {
            if (c != null) try { c.rollback(); } catch (SQLException ignored) {}
            throw ex;
        } finally {
            close(null, ps, c);
        }
    }

    // ─── Utilitaire fermeture ─────────────────────────────────────────────────
    private void close(ResultSet rs, PreparedStatement ps, Connection c) {
        if (rs  != null) try { rs.close();  } catch (SQLException ignored) {}
        if (ps  != null) try { ps.close();  } catch (SQLException ignored) {}
        DBConnection.closeQuietly(c);
    }
}
