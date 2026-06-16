package sga.dao;

import sga.model.Badge;
import sga.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BadgeDAOImpl implements BadgeDAO {

    @Override
    public Badge findById(int id) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT id, code FROM BADGE WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new Badge(rs.getInt("id"), rs.getString("code"));
            return null;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public List<Badge> findAll() throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT id, code FROM BADGE");
            ResultSet rs = ps.executeQuery();
            List<Badge> list = new ArrayList<>();
            while (rs.next()) list.add(new Badge(rs.getInt("id"), rs.getString("code")));
            return list;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public void insert(Badge b) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("INSERT INTO BADGE (id, code) VALUES (BADGE_SEQ.NEXTVAL, ?)");
            ps.setString(1, b.getCode());
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
    public void update(Badge b) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("UPDATE BADGE SET code = ? WHERE id = ?");
            ps.setString(1, b.getCode());
            ps.setInt(2, b.getId());
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
            PreparedStatement ps = c.prepareStatement("DELETE FROM BADGE WHERE id = ?");
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
