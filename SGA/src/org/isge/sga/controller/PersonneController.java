package org.isge.sga.controller;

import sga.dao.PersonneDAO;
import sga.dao.PersonneDAOImpl;
import sga.model.Personne;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller faisant le lien entre une vue Swing et le DAO Personne.
 *
 * Il expose des méthodes simples pour créer / mettre à jour / supprimer
 * et récupérer des personnes. Il effectue une validation basique des champs
 * (nom et prénom non vides) avant d'appeler le DAO.
 */
public class PersonneController {

    private final PersonneDAO personneDAO;

    public PersonneController() {
        this.personneDAO = new PersonneDAOImpl();
    }

    public PersonneController(PersonneDAO personneDAO) {
        this.personneDAO = personneDAO;
    }

    /**
     * Crée une personne après validation des champs.
     */
    public Personne createPersonne(Personne p) throws SQLException {
        validatePersonne(p);
        personneDAO.insert(p);
        return p;
    }

    /**
     * Met à jour une personne après validation.
     */
    public Personne updatePersonne(Personne p) throws SQLException {
        if (p.getId() <= 0) {
            throw new IllegalArgumentException("Personne id requis pour la mise à jour");
        }
        validatePersonne(p);
        personneDAO.update(p);
        return p;
    }

    /**
     * Suppression logique : passe le statut en INACTIF.
     */
    public void deletePersonne(int id) throws SQLException {
        // If you need logical deletion (STATUT = 'INACTIF'), update the DAO implementation accordingly.
        personneDAO.delete(id);
    }

    public Personne findById(int id) throws SQLException {
        return personneDAO.findById(id);
    }

    public List<Personne> findAll() throws SQLException {
        return personneDAO.findAll();
    }

    /**
     * Validation basique des données de la personne.
     *
     * @throws IllegalArgumentException en cas de données invalides
     */
    public void validatePersonne(Personne p) {
        if (p == null) {
            throw new IllegalArgumentException("Personne ne peut pas être null");
        }
        if (p.getNom() == null || p.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        if (p.getPrenom() == null || p.getPrenom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom est obligatoire");
        }
    }

    /**
     * Méthode utilitaire utilisée par une vue : crée et persiste une personne
     * à partir de champs simples.
     */
    public Personne createFromForm(String nom, String prenom) throws SQLException {
        Personne p = new Personne();
        p.setNom(nom);
        p.setPrenom(prenom);
        return createPersonne(p);
    }

    /**
     * Méthode utilitaire pour mise à jour depuis une vue.
     */
    public Personne updateFromForm(int id, String nom, String prenom) throws SQLException {
        Personne p = new Personne(id, nom, prenom);
        return updatePersonne(p);
    }
}
