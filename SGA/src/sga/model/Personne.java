package sga.model;

import org.isge.sga.model.entity.Profil;

/**
 * Représente une personne dans le système.
 * Contient désormais une association vers un {@link Profil} et un champ de statut métier.
 */
public class Personne {
    private int id;
    private String nom;
    private String prenom;
    private Profil profil; // association vers la table PROFIL (id_profil)
    private String statut; // valeur métier: 'ACTIF' par défaut, 'INACTIF' quand supprimé

    public Personne() {}

    public Personne(int id, String nom, String prenom) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.statut = "ACTIF";
    }

    public Personne(int id, String nom, String prenom, Profil profil, String statut) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.profil = profil;
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Profil getProfil() {
        return profil;
    }

    public void setProfil(Profil profil) {
        this.profil = profil;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        String p = (profil == null) ? "" : " (" + profil.toString() + ")";
        return (nom == null ? "" : nom) + " " + (prenom == null ? "" : prenom) + p;
    }
}
