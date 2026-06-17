package org.isgebf.sga.model;

import java.util.Date;

/**
 * Model representing a Personne (user) in the SGA system.
 */
public class Personne {
    private int idPersonne;
    private String nom;
    private String prenom;
    private Date dateNaissance;
    private String fonction;
    private String login;
    private String motDePasse;
    private String statut;
    private Integer idProfil;

    public Personne() {
    }

    public Personne(int idPersonne, String nom, String prenom, Date dateNaissance, String fonction, String login, String motDePasse, String statut, Integer idProfil) {
        this.idPersonne = idPersonne;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.fonction = fonction;
        this.login = login;
        this.motDePasse = motDePasse;
        this.statut = statut;
        this.idProfil = idProfil;
    }

    public int getIdPersonne() {
        return idPersonne;
    }

    public void setIdPersonne(int idPersonne) {
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

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
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

    @Override
    public String toString() {
        return "Personne{" +
                "idPersonne=" + idPersonne +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", fonction='" + fonction + '\'' +
                ", login='" + login + '\'' +
                ", statut='" + statut + '\'' +
                ", idProfil=" + idProfil +
                '}';
    }
}
