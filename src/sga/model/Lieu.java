package sga.model;

/**
 * Modèle représentant la table LIEU :
 * LIEU(id_lieu, libelle_lieu, emplacement, capacite_max, heure_ouverture, heure_fermeture, statut)
 */
public class Lieu {

    private Integer idLieu;
    private String libelleLieu;
    private String emplacement;
    private Integer capaciteMax;
    private Integer heureOuverture; // format HHMM, ex: 700
    private Integer heureFermeture; // format HHMM
    private String statut; // 'OUVERT','FERME','BLOQUE'

    public Lieu() {
    }

    public Lieu(Integer idLieu, String libelleLieu, String emplacement, Integer capaciteMax, Integer heureOuverture,
            Integer heureFermeture, String statut) {
        this.idLieu = idLieu;
        this.libelleLieu = libelleLieu;
        this.emplacement = emplacement;
        this.capaciteMax = capaciteMax;
        this.heureOuverture = heureOuverture;
        this.heureFermeture = heureFermeture;
        this.statut = statut;
    }

    /** Retourne true si le lieu est marqué BLOQUE */
    public boolean isBloque() {
        return this.statut != null && "BLOQUE".equalsIgnoreCase(this.statut);
    }

    /** Un lieu peut être bloqué si son statut n'est pas déjà BLOQUE */
    public boolean peutEtreBloque() {
        return this.statut == null || !"BLOQUE".equalsIgnoreCase(this.statut);
    }

    /** Un lieu peut être débloqué si son statut est BLOQUE */
    public boolean peutEtreDebloque() {
        return this.statut != null && "BLOQUE".equalsIgnoreCase(this.statut);
    }

    /**
     * Formate une heure au format HHMM en "HH:MM". Ex: 700 -> "07:00".
     * Si la valeur est nulle ou invalide, retourne "--:--".
     */
    public static String formaterHeure(Integer h) {
        if (h == null) {
            return "--:--";
        }
        int value = h.intValue();
        if (value < 0) {
            return "--:--";
        }
        int hh = value / 100;
        int mm = value % 100;
        if (hh < 0 || hh > 23 || mm < 0 || mm > 59) {
            return "--:--";
        }
        String sh = (hh < 10) ? ("0" + hh) : Integer.toString(hh);
        String sm = (mm < 10) ? ("0" + mm) : Integer.toString(mm);
        return sh + ":" + sm;
    }

    // Getters et setters

    public Integer getIdLieu() {
        return idLieu;
    }

    public void setIdLieu(Integer idLieu) {
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

    public Integer getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(Integer capaciteMax) {
        this.capaciteMax = capaciteMax;
    }

    public Integer getHeureOuverture() {
        return heureOuverture;
    }

    public void setHeureOuverture(Integer heureOuverture) {
        this.heureOuverture = heureOuverture;
    }

    public Integer getHeureFermeture() {
        return heureFermeture;
    }

    public void setHeureFermeture(Integer heureFermeture) {
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
        return "Lieu[idLieu=" + idLieu + ", libelle=" + libelleLieu + ", emplacement=" + emplacement
                + ", capaciteMax=" + capaciteMax + ", horaire=" + formaterHeure(heureOuverture) + "-"
                + formaterHeure(heureFermeture) + ", statut=" + statut + "]";
    }

}
