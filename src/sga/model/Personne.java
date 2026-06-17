package sga.model;

/**
 * Modèle représentant la table PERSONNE :
 * PERSONNE(id_personne, nom, prenom, statut, id_profil)
 */
public class Personne {

    /** Identifiant primaire */
    private Integer idPersonne;

    /** Nom de famille */
    private String nom;

    /** Prénom */
    private String prenom;

    /** Statut ('ACTIF' ou 'INACTIF') */
    private String statut;

    /** Référence vers le profil de la personne */
    private Integer idProfil;
    /** Identifiant de connexion */
    private String login;

    /** Mot de passe hashé (SHA-256) tel que stocké en base */
    private String motDePasse;

    /** Constructeur par défaut */
    public Personne() {
    }

    /** Constructeur pratique */
    public Personne(Integer idPersonne, String nom, String prenom, String statut, Integer idProfil, String login,
            String motDePasse) {
        this.idPersonne = idPersonne;
        this.nom = nom;
        this.prenom = prenom;
        this.statut = statut;
        this.idProfil = idProfil;
        this.login = login;
        this.motDePasse = motDePasse;
    }

    /**
     * Indique si la personne est active (statut = 'ACTIF').
     *
     * @return true si actif
     */
    public boolean isActif() {
        return this.statut != null && "ACTIF".equalsIgnoreCase(this.statut);
    }

    /** Retourne le nom complet au format "NOM Prénom" */
    public String getNomComplet() {
        StringBuilder sb = new StringBuilder();
        if (this.nom != null) {
            sb.append(this.nom);
        }
        if (this.prenom != null) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(this.prenom);
        }
        return sb.toString();
    }

    // Getters et setters

    public Integer getIdPersonne() {
        return idPersonne;
    }

    public void setIdPersonne(Integer idPersonne) {
        this.idPersonne = idPersonne;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Integer getIdProfil() {
        return idProfil;
    }

    public void setIdProfil(Integer idProfil) {
        this.idProfil = idProfil;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Retourne le mot de passe stocké (haché). Ne pas exposer ce setter pour des usages non sécurisés.
     */
    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    @Override
    public String toString() {
        return "Personne[idPersonne=" + idPersonne + ", nom=" + nom + ", prenom=" + prenom + ", statut=" + statut
                + ", idProfil=" + idProfil + "]";
    }

}
