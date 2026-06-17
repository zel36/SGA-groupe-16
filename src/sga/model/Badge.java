
package sga.model;

import java.sql.Date;

/**
 * Modèle représentant la table BADGE :
 * BADGE(num_badge VARCHAR2 PK, date_emission DATE, date_expiration DATE, statut, id_personne)
 */
public class Badge {

	/** Numéro du badge (PK) */
	private String numBadge;

	/** Date d'émission du badge */
	private Date dateEmission;

	/** Date d'expiration du badge */
	private Date dateExpiration;

	/** Statut du badge (ACTIF, SUSPENDU, EXPIRE, REVOQUE, BLOQUE) */
	private String statut;

	/** Identifiant de la personne propriétaire du badge */
	private Integer idPersonne;

	/** Constructeur par défaut */
	public Badge() {
	}

	/** Constructeur complet */
	public Badge(String numBadge, Date dateEmission, Date dateExpiration, String statut, Integer idPersonne) {
		this.numBadge = numBadge;
		this.dateEmission = dateEmission;
		this.dateExpiration = dateExpiration;
		this.statut = statut;
		this.idPersonne = idPersonne;
	}

	/**
	 * Indique si le badge peut être bloqué.
	 * Un badge peut être bloqué seulement s'il est actuellement ACTIF.
	 *
	 * @return true si le badge peut être bloqué
	 */
	public boolean peutEtreBloque() {
		return this.statut != null && "ACTIF".equalsIgnoreCase(this.statut);
	}

	/**
	 * Indique si le badge peut être débloqué.
	 * Un badge peut être débloqué s'il est BLOQUE ou SUSPENDU.
	 *
	 * @return true si le badge peut être débloqué
	 */
	public boolean peutEtreDebloque() {
		if (this.statut == null) {
			return false;
		}
		String s = this.statut.toUpperCase();
		return "BLOQUE".equals(s) || "SUSPENDU".equals(s);
	}

	/**
	 * Indique si le badge est expiré par rapport à la date système actuelle.
	 * Si dateExpiration est null, on considère que le badge n'est pas expiré.
	 *
	 * @return true si expiré
	 */
	public boolean isExpire() {
		if (this.dateExpiration == null) {
			return false;
		}
		Date now = new Date(System.currentTimeMillis());
		return this.dateExpiration.before(now);
	}

	// Getters et setters

	public String getNumBadge() {
		return numBadge;
	}

	public void setNumBadge(String numBadge) {
		this.numBadge = numBadge;
	}

	public Date getDateEmission() {
		return dateEmission;
	}

	public void setDateEmission(Date dateEmission) {
		this.dateEmission = dateEmission;
	}

	public Date getDateExpiration() {
		return dateExpiration;
	}

	public void setDateExpiration(Date dateExpiration) {
		this.dateExpiration = dateExpiration;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public Integer getIdPersonne() {
		return idPersonne;
	}

	public void setIdPersonne(Integer idPersonne) {
		this.idPersonne = idPersonne;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Badge[numBadge=").append(numBadge)
		  .append(", dateEmission=").append(dateEmission)
		  .append(", dateExpiration=").append(dateExpiration)
		  .append(", statut=").append(statut)
		  .append(", idPersonne=").append(idPersonne)
		  .append("]");
		return sb.toString();
	}

}
