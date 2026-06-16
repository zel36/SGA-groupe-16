package sga.dao;

import sga.model.Badge;

import java.sql.SQLException;
import java.util.List;

public interface BadgeDAO {
    Badge findById(int id) throws SQLException;
    List<Badge> findAll() throws SQLException;
    void insert(Badge b) throws SQLException;
    void update(Badge b) throws SQLException;
    void delete(int id) throws SQLException;
}
