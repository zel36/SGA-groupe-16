package sga.dao;

import sga.model.Personne;
import sga.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT id, nom, prenom FROM PERSONNE WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Personne p = new Personne(rs.getInt("id"), rs.getString("nom"), rs.getString("prenom"));
                return p;
            }
            return null;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public List<Personne> findAll() throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT id, nom, prenom FROM PERSONNE");
            ResultSet rs = ps.executeQuery();
            List<Personne> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Personne(rs.getInt("id"), rs.getString("nom"), rs.getString("prenom")));
            }
            return list;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public void insert(Personne p) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("INSERT INTO PERSONNE (id, nom, prenom) VALUES (PERSONNE_SEQ.NEXTVAL, ?, ?)");
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.executeUpdate();
            c.commit();
        } catch (SQLException ex) {
            if (c != null) c.rollback();
            throw ex;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public void update(Personne p) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("UPDATE PERSONNE SET nom = ?, prenom = ? WHERE id = ?");
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setInt(3, p.getId());
            ps.executeUpdate();
            c.commit();
        } catch (SQLException ex) {
            if (c != null) c.rollback();
            throw ex;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("DELETE FROM PERSONNE WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            c.commit();
        } catch (SQLException ex) {
            if (c != null) c.rollback();
            throw ex;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }
}
