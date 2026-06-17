package org.isgebf.sga.controller;

import org.isgebf.sga.dao.PersonneDAO;
import org.isgebf.sga.model.Personne;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Controller for Personne: validation and business logic before DAO operations.
 */
public class PersonneController {

    private static final List<String> ALLOWED_FONCTIONS = Arrays.asList("ETUDIANT", "ENSEIGNANT", "ADMIN", "TECHNICIEN", "AUTRE");
    private final PersonneDAO dao = new PersonneDAO();

    public Personne create(Personne p) throws SQLException, IllegalArgumentException {
        validateForCreate(p);
        // default statut
        if (p.getStatut() == null) p.setStatut("ACTIF");
        return dao.create(p);
    }

    public boolean update(Personne p) throws SQLException, IllegalArgumentException {
        validateForUpdate(p);
        return dao.update(p);
    }

    public boolean deleteLogic(int idPersonne) throws SQLException {
        return dao.deleteLogic(idPersonne);
    }

    public Personne findById(int id) throws SQLException {
        return dao.findById(id);
    }

    public List<Personne> search(String term) throws SQLException {
        return dao.searchByNameOrFonction(term == null ? "" : term);
    }

    public List<Personne> getAllActive() throws SQLException {
        return dao.getAllActive();
    }

    private void validateForCreate(Personne p) {
        if (p == null) throw new IllegalArgumentException("Personne cannot be null");
        if (p.getNom() == null || p.getNom().trim().isEmpty()) throw new IllegalArgumentException("Nom is required");
        if (p.getPrenom() == null || p.getPrenom().trim().isEmpty()) throw new IllegalArgumentException("Prenom is required");
        if (p.getFonction() == null || !ALLOWED_FONCTIONS.contains(p.getFonction().toUpperCase())) throw new IllegalArgumentException("Fonction must be one of: " + ALLOWED_FONCTIONS);
        if (p.getLogin() == null || p.getLogin().trim().isEmpty()) throw new IllegalArgumentException("Login is required");
        if (p.getMotDePasse() == null || p.getMotDePasse().length() < 6) throw new IllegalArgumentException("Password must be at least 6 characters");
        if (p.getDateNaissance() != null && p.getDateNaissance().after(new Date())) throw new IllegalArgumentException("Date de naissance cannot be in the future");
    }

    private void validateForUpdate(Personne p) {
        if (p == null) throw new IllegalArgumentException("Personne cannot be null");
        if (p.getIdPersonne() <= 0) throw new IllegalArgumentException("Valid idPersonne is required");
        validateForCreate(p);
    }
}
