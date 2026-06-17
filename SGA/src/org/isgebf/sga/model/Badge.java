package org.isgebf.sga.model;

import java.util.Date;

/**
 * Model representing a Badge (Léo card) in the SGA system.
 */
public class Badge {
    private String numBadge;
    private Date dateEmission;
    private Date dateExpiration;
    private String statut; // ACTIF, REVOQUE, EXPIRE
    private int idPersonne;

    public Badge() {
    }

    public Badge(String numBadge, Date dateEmission, Date dateExpiration, String statut, int idPersonne) {
        this.numBadge = numBadge;
        this.dateEmission = dateEmission;
        this.dateExpiration = dateExpiration;
        this.statut = statut;
        this.idPersonne = idPersonne;
    }

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

    public int getIdPersonne() {
        return idPersonne;
    }

    public void setIdPersonne(int idPersonne) {
        this.idPersonne = idPersonne;
    }

    @Override
    public String toString() {
        return "Badge{" +
                "numBadge='" + numBadge + '\'' +
                ", statut='" + statut + '\'' +
                ", idPersonne=" + idPersonne +
                '}';
    }
}
