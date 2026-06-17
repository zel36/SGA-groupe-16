package sga.dao;

import sga.model.Acces;

import java.sql.SQLException;
import java.util.List;

public interface AccesDAO {
    Acces findById(int id) throws SQLException;
    List<Acces> findAll() throws SQLException;
    void insert(Acces a) throws SQLException;
    void update(Acces a) throws SQLException;
    void delete(int id) throws SQLException;
}
