package sga.dao;

import sga.model.Badge;

import java.sql.SQLException;
import java.util.List;

/**
 * DAO pour la table BADGE selon la spécification SGA.
 */
public interface BadgeDAO {

    Badge findByNumBadge(String numBadge) throws SQLException;

    List<Badge> findAll() throws SQLException;

    List<Badge> findByStatut(String statut) throws SQLException;

    void insert(Badge b) throws SQLException;

    /**
     * Bloque un badge dans une transaction et insert un historique.
     * Retourne true si succès.
     */
    boolean bloquerBadge(String numBadge, String motif, int idOperateur, String nomOperateur) throws SQLException;

    /**
     * Débloque un badge dans une transaction et insert un historique.
     * Retourne true si succès.
     */
    boolean debloquerBadge(String numBadge, String justification, int idOperateur, String nomOperateur)
            throws SQLException;

}
