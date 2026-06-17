package sga.model;

/**
 * Représente un lieu accessible dans le SGA.
 */
public class Lieu {
    private int id;
    private String nom;
    private String statut; // OUVERT, FERME, BLOQUE

    public Lieu() {}

    public Lieu(int id, String nom) {
        this.id = id;
        this.nom = nom;
        this.statut = Statut.OUVERT;
    }

    public Lieu(int id, String nom, String statut) {
        this.id = id;
        this.nom = nom;
        this.statut = statut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return nom;
    }

    public static class Statut {
        public static final String OUVERT = "OUVERT";
        public static final String FERME = "FERME";
        public static final String BLOQUE = "BLOQUE";
    }
}
