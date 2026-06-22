package sga.model;

/**
 * Modèle représentant la table ACCES :
 * ACCES(id_acces, libelle_acces, type_acces, description, statut, id_lieu)
 *
 * Contrainte : un accès appartient à un seul lieu (FK id_lieu → LIEU.id_lieu)
 * Un lieu peut avoir plusieurs accès.
 */
public class Acces {

    // Types d'accès valides (CHECK constraint en BD)
    public static final String TYPE_PORTE      = "PORTE";
    public static final String TYPE_BARRIERE   = "BARRIERE";
    public static final String TYPE_PORTILLON  = "PORTILLON";
    public static final String TYPE_PARKING    = "PARKING";
    public static final String TYPE_ASCENSEUR  = "ASCENSEUR";

    public static final String[] TYPES_VALIDES = {
        TYPE_PORTE, TYPE_BARRIERE, TYPE_PORTILLON, TYPE_PARKING, TYPE_ASCENSEUR
    };

    // Statuts
    public static final String STATUT_ACTIF   = "ACTIF";
    public static final String STATUT_INACTIF = "INACTIF";

    private Integer idAcces;
    private String  libelleAcces;
    private String  typeAcces;
    private String  description;
    private String  statut;
    private Integer idLieu;

    // Champ transient : libellé du lieu (chargé par JOIN, non persisté ici)
    private String libelleLieu;

    public Acces() {}

    public Acces(Integer idAcces, String libelleAcces, String typeAcces,
                 String description, String statut, Integer idLieu) {
        this.idAcces      = idAcces;
        this.libelleAcces = libelleAcces;
        this.typeAcces    = typeAcces;
        this.description  = description;
        this.statut       = statut;
        this.idLieu       = idLieu;
    }

    // ---- Getters / Setters ----

    public Integer getIdAcces()              { return idAcces; }
    public void    setIdAcces(Integer id)    { this.idAcces = id; }

    public String  getLibelleAcces()               { return libelleAcces; }
    public void    setLibelleAcces(String libelle) { this.libelleAcces = libelle; }

    public String  getTypeAcces()              { return typeAcces; }
    public void    setTypeAcces(String type)   { this.typeAcces = type; }

    public String  getDescription()                { return description; }
    public void    setDescription(String desc)     { this.description = desc; }

    public String  getStatut()               { return statut; }
    public void    setStatut(String statut)  { this.statut = statut; }

    public Integer getIdLieu()               { return idLieu; }
    public void    setIdLieu(Integer idLieu) { this.idLieu = idLieu; }

    public String  getLibelleLieu()                { return libelleLieu; }
    public void    setLibelleLieu(String libelleLieu) { this.libelleLieu = libelleLieu; }

    @Override
    public String toString() {
        return "Acces[id=" + idAcces + ", libelle=" + libelleAcces
                + ", type=" + typeAcces + ", lieu=" + idLieu
                + ", statut=" + statut + "]";
    }
}
