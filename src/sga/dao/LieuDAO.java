package sga.dao;

import sga.model.Lieu;

import java.sql.SQLException;
import java.util.List;

/**
 * DAO pour la table LIEU selon la spécification SGA.
 */
public interface LieuDAO {

    Lieu findById(int id) throws SQLException;

    List<Lieu> findAll() throws SQLException;

    void insert(Lieu l) throws SQLException;

    void update(Lieu l) throws SQLException;

    /** Désactivation physique interdite : bloquerLieu met statut='BLOQUE' */
    boolean bloquerLieu(int idLieu, String motif, int idOperateur, String nomOperateur) throws SQLException;

    boolean debloquerLieu(int idLieu, String justification, int idOperateur, String nomOperateur) throws SQLException;

}
