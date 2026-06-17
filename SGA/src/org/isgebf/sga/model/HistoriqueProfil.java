package org.isgebf.sga.model;

import java.util.Date;

/**
 * Model representing changes of profile assignment for a person.
 */
public class HistoriqueProfil {
    private int idHistorique;
    private int idPersonne;
    private Integer idAncienProfil;
    private Integer idNouveauProfil;
    private Date dateChangement;
    private String motif;
    private String effectuePar;

    public HistoriqueProfil() {
    }

    public HistoriqueProfil(int idHistorique, int idPersonne, Integer idAncienProfil, Integer idNouveauProfil, Date dateChangement, String motif, String effectuePar) {
        this.idHistorique = idHistorique;
        this.idPersonne = idPersonne;
        this.idAncienProfil = idAncienProfil;
        this.idNouveauProfil = idNouveauProfil;
        this.dateChangement = dateChangement;
        this.motif = motif;
        this.effectuePar = effectuePar;
    }

    public int getIdHistorique() {
        return idHistorique;
    }

    public void setIdHistorique(int idHistorique) {
        this.idHistorique = idHistorique;
    }

    public int getIdPersonne() {
        return idPersonne;
    }

    public void setIdPersonne(int idPersonne) {
        this.idPersonne = idPersonne;
    }

    public Integer getIdAncienProfil() {
        return idAncienProfil;
    }

    public void setIdAncienProfil(Integer idAncienProfil) {
        this.idAncienProfil = idAncienProfil;
    }

    public Integer getIdNouveauProfil() {
        return idNouveauProfil;
    }

    public void setIdNouveauProfil(Integer idNouveauProfil) {
        this.idNouveauProfil = idNouveauProfil;
    }

    public Date getDateChangement() {
        return dateChangement;
    }

    public void setDateChangement(Date dateChangement) {
        this.dateChangement = dateChangement;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getEffectuePar() {
        return effectuePar;
    }

    public void setEffectuePar(String effectuePar) {
        this.effectuePar = effectuePar;
    }

    @Override
    public String toString() {
        return "HistoriqueProfil{" +
                "idHistorique=" + idHistorique +
                ", idPersonne=" + idPersonne +
                ", idAncienProfil=" + idAncienProfil +
                ", idNouveauProfil=" + idNouveauProfil +
                '}';
    }
}
