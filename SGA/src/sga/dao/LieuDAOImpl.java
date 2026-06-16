package sga.dao;

import sga.model.Lieu;
import sga.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LieuDAOImpl implements LieuDAO {

    @Override
    public Lieu findById(int id) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT id, nom FROM LIEU WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new Lieu(rs.getInt("id"), rs.getString("nom"));
            return null;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public List<Lieu> findAll() throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT id, nom FROM LIEU");
            ResultSet rs = ps.executeQuery();
            List<Lieu> list = new ArrayList<>();
            while (rs.next()) list.add(new Lieu(rs.getInt("id"), rs.getString("nom")));
            return list;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public void insert(Lieu l) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("INSERT INTO LIEU (id, nom) VALUES (LIEU_SEQ.NEXTVAL, ?)");
            ps.setString(1, l.getNom());
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
    public void update(Lieu l) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("UPDATE LIEU SET nom = ? WHERE id = ?");
            ps.setString(1, l.getNom());
            ps.setInt(2, l.getId());
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
            PreparedStatement ps = c.prepareStatement("DELETE FROM LIEU WHERE id = ?");
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
