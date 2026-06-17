package org.isgebf.sga.model;

import java.util.Date;

/**
 * Model representing a Profil (role) in the SGA system.
 */
public class Profil {
    private int idProfil;
    private String libelleProfil;
    private String description;
    private Date dateCreation;
    private String statut;

    public Profil() {
    }

    public Profil(int idProfil, String libelleProfil, String description, Date dateCreation, String statut) {
        this.idProfil = idProfil;
        this.libelleProfil = libelleProfil;
        this.description = description;
        this.dateCreation = dateCreation;
        this.statut = statut;
    }

    public int getIdProfil() {
        return idProfil;
    }

    public void setIdProfil(int idProfil) {
        this.idProfil = idProfil;
    }

    public String getLibelleProfil() {
        return libelleProfil;
    }

    public void setLibelleProfil(String libelleProfil) {
        this.libelleProfil = libelleProfil;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Profil{" +
                "idProfil=" + idProfil +
                ", libelleProfil='" + libelleProfil + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}
