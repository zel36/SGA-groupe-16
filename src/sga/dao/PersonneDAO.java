package sga.dao;

import sga.model.Personne;

import java.sql.SQLException;
import java.util.List;

/**
 * DAO pour la table PERSONNE selon la spécification SGA.
 */
public interface PersonneDAO {

    Personne findById(int id) throws SQLException;

    /** Retourne uniquement les personnes actives */
    List<Personne> findAll() throws SQLException;

    /** Recherche par nom (UPPER LIKE '%motCle%') */
    List<Personne> findByNom(String motCle) throws SQLException;

    void insert(Personne p) throws SQLException;

    void update(Personne p) throws SQLException;

    /** Désactive (soft delete) une personne en mettant statut='INACTIF' */
    void desactiver(int idPersonne) throws SQLException;

    /**
     * Authentifie un opérateur par login/motDePasse en comparant sha256(motDePasse)
     * avec la colonne mot_de_passe.
     *
     * @return Personne si authentification réussie, null sinon
     */
    Personne authentifier(String login, String motDePasse) throws SQLException;

}
