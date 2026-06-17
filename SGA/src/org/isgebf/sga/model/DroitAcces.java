package org.isgebf.sga.model;

/**
 * Model representing an access right mapping a Profil to a Lieu with time constraints.
 */
public class DroitAcces {
    private int idDroit;
    private int idProfil;
    private int idLieu;
    private int jourSemaine; // 1=Monday .. 7=Sunday (or according to system convention)
    private String heureDebut; // HH:mm
    private String heureFin;   // HH:mm

    public DroitAcces() {
    }

    public DroitAcces(int idDroit, int idProfil, int idLieu, int jourSemaine, String heureDebut, String heureFin) {
        this.idDroit = idDroit;
        this.idProfil = idProfil;
        this.idLieu = idLieu;
        this.jourSemaine = jourSemaine;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
    }

    public int getIdDroit() {
        return idDroit;
    }

    public void setIdDroit(int idDroit) {
        this.idDroit = idDroit;
    }

    public int getIdProfil() {
        return idProfil;
    }

    public void setIdProfil(int idProfil) {
        this.idProfil = idProfil;
    }

    public int getIdLieu() {
        return idLieu;
    }

    public void setIdLieu(int idLieu) {
        this.idLieu = idLieu;
    }

    public int getJourSemaine() {
        return jourSemaine;
    }

    public void setJourSemaine(int jourSemaine) {
        this.jourSemaine = jourSemaine;
    }

    public String getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(String heureDebut) {
        this.heureDebut = heureDebut;
    }

    public String getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(String heureFin) {
        this.heureFin = heureFin;
    }

    @Override
    public String toString() {
        return "DroitAcces{" +
                "idDroit=" + idDroit +
                ", idProfil=" + idProfil +
                ", idLieu=" + idLieu +
                ", jourSemaine=" + jourSemaine +
                ", periode='" + heureDebut + "-" + heureFin + '\'' +
                '}';
    }
}
