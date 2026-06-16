package sga.dao;

import sga.model.Lieu;

import java.sql.SQLException;
import java.util.List;

public interface LieuDAO {
    Lieu findById(int id) throws SQLException;
    List<Lieu> findAll() throws SQLException;
    void insert(Lieu l) throws SQLException;
    void update(Lieu l) throws SQLException;
    void delete(int id) throws SQLException;
}
