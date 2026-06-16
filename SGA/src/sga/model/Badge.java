package sga.model;

/**
 * Représentation minimale d'un badge.
 */
public class Badge {
    private int id;
    private String code;

    public Badge() {}

    public Badge(int id, String code) {
        this.id = id;
        this.code = code;
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

    @Override
    public String toString() {
        return code;
    }
}
