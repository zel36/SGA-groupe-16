package sga.dao;

import sga.model.Badge;
import sga.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation JDBC de BadgeDAO.
 */
public class BadgeDAOImpl implements BadgeDAO {

	private static final String SELECT_BY_NUM = "SELECT num_badge, date_emission, date_expiration, statut, id_personne FROM BADGE WHERE num_badge = ?";
	private static final String SELECT_ALL = "SELECT num_badge, date_emission, date_expiration, statut, id_personne FROM BADGE";
	private static final String SELECT_BY_STATUT = "SELECT num_badge, date_emission, date_expiration, statut, id_personne FROM BADGE WHERE statut = ?";
	private static final String INSERT = "INSERT INTO BADGE(num_badge, date_emission, date_expiration, statut, id_personne) VALUES(?,?,?,?,?)";
	private static final String UPDATE_STATUT = "UPDATE BADGE SET statut = ? WHERE num_badge = ?";
	private static final String SELECT_ID_PERSONNE = "SELECT id_personne FROM BADGE WHERE num_badge = ?";
	private static final String INSERT_HIST = "INSERT INTO HISTORIQUE_EVENEMENT(type_evenement, statut_resultat, description_justification, num_badge, id_personne, effectue_par) VALUES(?,?,?,?,?,?)";

	@Override
	public Badge findByNumBadge(String numBadge) {
		if (numBadge == null) {
			return null;
		}
		
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(SELECT_BY_NUM)) {
			
			ps.setString(1, numBadge);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapRow(rs);
				}
				return null;
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erreur lors de la recherche du badge : " + numBadge, e);
		}
	}

	@Override
	public List<Badge> findAll() {
		List<Badge> list = new ArrayList<>();
		
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(SELECT_ALL); 
			 ResultSet rs = ps.executeQuery()) {
			
			while (rs.next()) {
				list.add(mapRow(rs));
			}
			return list;
		} catch (SQLException e) {
			throw new RuntimeException("Erreur lors de la récupération de tous les badges", e);
		}
	}

	@Override
	public List<Badge> findByStatut(String statut) {
		List<Badge> list = new ArrayList<>();
		if (statut == null) {
			return list;
		}
		
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(SELECT_BY_STATUT)) {
			
			ps.setString(1, statut);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapRow(rs));
				}
			}
			return list;
		} catch (SQLException e) {
			throw new RuntimeException("Erreur lors de la recherche des badges par statut : " + statut, e);
		}
	}

	@Override
	public void insert(Badge badge) {
		if (badge == null) {
			throw new IllegalArgumentException("Le badge ne peut pas être null");
		}
		
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(INSERT)) {
			
			ps.setString(1, badge.getNumBadge());
			
			if (badge.getDateEmission() != null) {
				ps.setDate(2, badge.getDateEmission());
			} else {
				ps.setNull(2, Types.DATE);
			}
			
			if (badge.getDateExpiration() != null) {
				ps.setDate(3, badge.getDateExpiration());
			} else {
				ps.setNull(3, Types.DATE);
			}
			
			ps.setString(4, badge.getStatut());
			
			if (badge.getIdPersonne() != null) {
				ps.setInt(5, badge.getIdPersonne());
			} else {
				ps.setNull(5, Types.INTEGER);
			}
			
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Erreur lors de l'insertion du badge : " + badge.getNumBadge(), e);
		}
	}

	@Override
	public boolean bloquerBadge(String numBadge, String motif, int idOperateur, String nomOperateur) {
		return transactionUpdateEtatEtHistorique(numBadge, motif, idOperateur, nomOperateur, "BLOQUE",
				"BLOCAGE_BADGE");
	}

	@Override
	public boolean debloquerBadge(String numBadge, String justification, int idOperateur, String nomOperateur) {
		return transactionUpdateEtatEtHistorique(numBadge, justification, idOperateur, nomOperateur, "ACTIF",
				"DEBLOCAGE_BADGE");
	}

	/**
	 * Met à jour le statut du badge et insère une ligne dans l'historique dans une transaction sécurisée.
	 */
	private boolean transactionUpdateEtatEtHistorique(String numBadge, String justification, int idOperateur,
			String nomOperateur, String nouveauStatut, String typeEvenement) {
		if (numBadge == null) {
			throw new IllegalArgumentException("Le numBadge ne peut pas être null");
		}
		
		// Déclaration hors du try pour pouvoir gérer proprement le rollback dans le catch
		Connection conn = null;
		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);
			
			// 1. Mise à jour du statut du badge
			try (PreparedStatement psUpdate = conn.prepareStatement(UPDATE_STATUT)) {
				psUpdate.setString(1, nouveauStatut);
				psUpdate.setString(2, numBadge);
				psUpdate.executeUpdate();
			}
			
			// 2. Récupération de l'id_personne associé au badge
			Integer idPersonne = null;
			try (PreparedStatement psSelect = conn.prepareStatement(SELECT_ID_PERSONNE)) {
				psSelect.setString(1, numBadge);
				try (ResultSet rs = psSelect.executeQuery()) {
					if (rs.next()) {
						int v = rs.getInt(1);
						if (!rs.wasNull()) {
							idPersonne = v;
						}
					}
				}
			}
			
			// 3. Insertion dans l'historique
			try (PreparedStatement psHist = conn.prepareStatement(INSERT_HIST)) {
				psHist.setString(1, typeEvenement);
				psHist.setString(2, "SUCCES");
				psHist.setString(3, justification);
				psHist.setString(4, numBadge);
				
				if (idPersonne != null) {
					psHist.setInt(5, idPersonne);
				} else {
					psHist.setNull(5, Types.INTEGER);
				}
				
				String effectuePar = idOperateur + ":" + (nomOperateur == null ? "" : nomOperateur);
				psHist.setString(6, effectuePar);
				
				psHist.executeUpdate();
			}
			
			// Validation de la transaction globale
			conn.commit();
			return true;

		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException rollbackEx) {
					// Ignoré ou journalisé pour ne pas masquer l'exception d'origine
				}
			}
			return false;
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close(); // Fermeture manuelle nécessaire car déclarée en dehors du bloc try-with-resources
				} catch (SQLException ignore) {
				}
			}
		}
	}

	private Badge mapRow(ResultSet rs) throws SQLException {
		Badge b = new Badge();
		b.setNumBadge(rs.getString("num_badge"));
		b.setDateEmission(rs.getDate("date_emission"));
		b.setDateExpiration(rs.getDate("date_expiration"));
		b.setStatut(rs.getString("statut"));
		
		int idPers = rs.getInt("id_personne");
		if (rs.wasNull()) {
			b.setIdPersonne(null);
		} else {
			b.setIdPersonne(idPers);
		}
		return b;
	}
}