
package sga.model;

import java.sql.Timestamp;

/**
 * Modèle représentant un enregistrement du journal HISTORIQUE_EVENEMENT.
 */
public class HistoriqueEvenement {

	private Integer idEvenement;
	private String typeEvenement; // e.g. 'ACCES_PHYSIQUE', 'BLOCAGE_BADGE', ...
	private Timestamp dateEvenement;
	private String statutResultat; // 'ACCORDE','REFUSE','SUCCES','ECHEC'
	private String descriptionJustification; // NOT NULL selon spec
	private String numBadge;
	private Integer idLieu;
	private Integer idPersonne;
	private String effectuePar;

	public HistoriqueEvenement() {
	}

	// Getters et setters

	public Integer getIdEvenement() {
		return idEvenement;
	}

	public void setIdEvenement(Integer idEvenement) {
		this.idEvenement = idEvenement;
	}

	public String getTypeEvenement() {
		return typeEvenement;
	}

	public void setTypeEvenement(String typeEvenement) {
		this.typeEvenement = typeEvenement;
	}

	public Timestamp getDateEvenement() {
		return dateEvenement;
	}

	public void setDateEvenement(Timestamp dateEvenement) {
		this.dateEvenement = dateEvenement;
	}

	public String getStatutResultat() {
		return statutResultat;
	}

	public void setStatutResultat(String statutResultat) {
		this.statutResultat = statutResultat;
	}

	public String getDescriptionJustification() {
		return descriptionJustification;
	}

	public void setDescriptionJustification(String descriptionJustification) {
		this.descriptionJustification = descriptionJustification;
	}

	public String getNumBadge() {
		return numBadge;
	}

	public void setNumBadge(String numBadge) {
		this.numBadge = numBadge;
	}

	public Integer getIdLieu() {
		return idLieu;
	}

	public void setIdLieu(Integer idLieu) {
		this.idLieu = idLieu;
	}

	public Integer getIdPersonne() {
		return idPersonne;
	}

	public void setIdPersonne(Integer idPersonne) {
		this.idPersonne = idPersonne;
	}

	public String getEffectuePar() {
		return effectuePar;
	}

	public void setEffectuePar(String effectuePar) {
		this.effectuePar = effectuePar;
	}

}
