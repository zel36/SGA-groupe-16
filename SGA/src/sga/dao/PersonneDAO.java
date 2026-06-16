package sga.dao;

import sga.model.Personne;

import java.sql.SQLException;
import java.util.List;

public interface PersonneDAO {
    Personne findById(int id) throws SQLException;
    List<Personne> findAll() throws SQLException;
    void insert(Personne p) throws SQLException;
    void update(Personne p) throws SQLException;
    void delete(int id) throws SQLException;
}
