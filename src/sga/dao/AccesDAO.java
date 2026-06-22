package sga.dao;

import sga.model.Acces;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface DAO pour la gestion des accès (table ACCES).
 *
 * Fonctions couvertes :
 *  - Créer un accès (avec type, lieu, libellé, description)
 *  - Modifier les caractéristiques d'un accès existant
 *  - Lister tous les accès avec filtrage par lieu et par type
 *  - Rechercher un accès par libellé, type ou lieu
 *  - Désactiver logiquement un accès (statut INACTIF)
 */
public interface AccesDAO {

    /** Retrouve un accès par son identifiant. */
    Acces findById(int idAcces) throws SQLException;

    /** Retourne tous les accès (actifs et inactifs). */
    List<Acces> findAll() throws SQLException;

    /**
     * Liste les accès avec filtres optionnels.
     *
     * @param idLieu    filtre sur l'identifiant du lieu (null = tous les lieux)
     * @param typeAcces filtre sur le type (null = tous les types)
     */
    List<Acces> findByFilter(Integer idLieu, String typeAcces) throws SQLException;

    /**
     * Recherche par libellé (LIKE), type exact, ou identifiant de lieu.
     * Tous les paramètres sont optionnels (null = ignoré).
     *
     * @param libelleKeyword  mot-clé partiel sur libelle_acces (null = ignoré)
     * @param typeAcces       type exact (null = ignoré)
     * @param idLieu          identifiant du lieu (null = ignoré)
     */
    List<Acces> search(String libelleKeyword, String typeAcces, Integer idLieu) throws SQLException;

    /** Insère un nouvel accès et met à jour idAcces avec la valeur générée. */
    void insert(Acces acces) throws SQLException;

    /** Met à jour les caractéristiques d'un accès existant. */
    void update(Acces acces) throws SQLException;

    /**
     * Désactivation logique : passe le statut à INACTIF.
     * La suppression physique est interdite (intégrité référentielle).
     */
    void desactiver(int idAcces) throws SQLException;
}
