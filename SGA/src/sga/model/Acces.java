package sga.model;

/**
 * Représente un accès (droits) lié à un Lieu.
 */
public class Acces {
    private int id;
    private String code;
    private Integer lieuId; // référence vers LIEU.id
    private String statut; // ACTIF, INACTIF

    public Acces() {}

    public Acces(int id, String code) {
        this.id = id;
        this.code = code;
    }

    public Acces(int id, String code, Integer lieuId, String statut) {
        this.id = id;
        this.code = code;
        this.lieuId = lieuId;
        this.statut = statut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getLieuId() {
        return lieuId;
    }

    public void setLieuId(Integer lieuId) {
        this.lieuId = lieuId;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public static class Statut {
        public static final String ACTIF = "ACTIF";
        public static final String INACTIF = "INACTIF";
    }
}
