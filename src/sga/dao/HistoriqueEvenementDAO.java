package sga.dao;

import sga.model.HistoriqueEvenement;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO pour la lecture du journal HISTORIQUE_EVENEMENT (lecture seule côté application).
 */
public interface HistoriqueEvenementDAO {

	List<HistoriqueEvenement> findAll() throws SQLException;

	List<HistoriqueEvenement> findByType(String type) throws SQLException;

	List<HistoriqueEvenement> findByPeriode(Date debut, Date fin) throws SQLException;

	int compterAccordes(Date debut, Date fin) throws SQLException;

	int compterRefuses(Date debut, Date fin) throws SQLException;

}
