package sga.controller;

import sga.dao.PersonneDAO;
import sga.dao.PersonneDAOImpl;
import sga.model.Personne;
import sga.util.ValidateurSaisie;

import java.util.List;

/**
 * Contrôleur pour la gestion des personnes.
 */
public class PersonneController {

    private PersonneDAO personneDAO = new PersonneDAOImpl();

    /** Liste les personnes actives. */
    public List<Personne> listerPersonnes() {
        try {
            return personneDAO.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    /** Crée une nouvelle personne. */
    public String creerPersonne(Personne p) {
        if (p == null) {
            return "ERREUR : Données personelles manquantes.";
        }
        if (!ValidateurSaisie.nonVide(p.getNom()) || !ValidateurSaisie.nonVide(p.getPrenom())) {
            return "ERREUR : Le nom et le prénom sont obligatoires.";
        }
        try {
            // Par sécurité, définir statut par défaut
            if (p.getStatut() == null) {
                p.setStatut("ACTIF");
            }
            personneDAO.insert(p);
            return "SUCCÈS : Personne créée.";
        } catch (Exception e) {
            return "ERREUR : " + e.getMessage();
        }
    }

    /** Modifie une personne existante. */
    public String modifierPersonne(Personne p) {
        if (p == null || p.getIdPersonne() == null) {
            return "ERREUR : Identifiant de la personne requis.";
        }
        try {
            personneDAO.update(p);
            return "SUCCÈS : Personne modifiée.";
        } catch (Exception e) {
            return "ERREUR : " + e.getMessage();
        }
    }

    /** Désactive (soft delete) une personne. */
    public String desactiverPersonne(int id) {
        try {
            personneDAO.desactiver(id);
            return "SUCCÈS : Personne désactivée.";
        } catch (Exception e) {
            return "ERREUR : " + e.getMessage();
        }
    }

    /** Recherche des personnes par nom ou prénom. */
    public List<Personne> rechercherPersonne(String nom) {
        try {
            return personneDAO.findByNom(nom);
        } catch (Exception e) {
            return null;
        }
    }

}
