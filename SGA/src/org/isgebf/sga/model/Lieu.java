package org.isgebf.sga.model;

/**
 * Model representing a physical location (room) in the SGA system.
 */
public class Lieu {
    private int idLieu;
    private String libelleLieu;
    private String emplacement;
    private int capaciteMax;
    private String heureOuverture; // format HH:mm
    private String heureFermeture; // format HH:mm
    private String statut;

    public Lieu() {
    }

    public Lieu(int idLieu, String libelleLieu, String emplacement, int capaciteMax, String heureOuverture, String heureFermeture, String statut) {
        this.idLieu = idLieu;
        this.libelleLieu = libelleLieu;
        this.emplacement = emplacement;
        this.capaciteMax = capaciteMax;
        this.heureOuverture = heureOuverture;
        this.heureFermeture = heureFermeture;
        this.statut = statut;
    }

    public int getIdLieu() {
        return idLieu;
    }

    public void setIdLieu(int idLieu) {
        this.idLieu = idLieu;
    }

    public String getLibelleLieu() {
        return libelleLieu;
    }

    public void setLibelleLieu(String libelleLieu) {
        this.libelleLieu = libelleLieu;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public int getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(int capaciteMax) {
        this.capaciteMax = capaciteMax;
    }

    public String getHeureOuverture() {
        return heureOuverture;
    }

    public void setHeureOuverture(String heureOuverture) {
        this.heureOuverture = heureOuverture;
    }

    public String getHeureFermeture() {
        return heureFermeture;
    }

    public void setHeureFermeture(String heureFermeture) {
        this.heureFermeture = heureFermeture;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Lieu{" +
                "idLieu=" + idLieu +
                ", libelleLieu='" + libelleLieu + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}
