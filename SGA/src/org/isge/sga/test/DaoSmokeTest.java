package org.isge.sga.test;

import sga.dao.*;
import sga.model.*;
import sga.util.DBConnection;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Mini-runner de fumée (smoke test) pour valider la persistance Oracle via
 * les DAO du Lot 1 : PersonneDAO, BadgeDAO, LieuDAO, AccesDAO.
 *
 * Usage: exécuter la méthode main depuis l'IDE ou en java -cp ...
 */
public class DaoSmokeTest {

    public static void main(String[] args) {
        PersonneDAO personneDao = new PersonneDAOImpl();
        LieuDAO lieuDao = new LieuDAOImpl();
        BadgeDAO badgeDao = new BadgeDAOImpl();
        AccesDAO accesDao = new AccesDAOImpl();

        System.out.println("--- DAO Smoke Test START ---");

        // Helper to print SQL exceptions with Oracle specifics
        java.util.function.Consumer<SQLException> sqlPrinter = (ex) -> {
            printSqlException(ex);
        };

        // 1) Lieu
        Lieu l = new Lieu();
        l.setNom("LieuSmokeTest");
        l.setStatut(Lieu.Statut.OUVERT);
        try {
            lieuDao.insert(l);
            System.out.println("Inserted Lieu id=" + l.getId());
            Lieu foundLieu = lieuDao.findById(l.getId());
            System.out.println("Found Lieu: " + foundLieu);
        } catch (SQLException ex) {
            System.err.println("Failed to insert/find Lieu: " + ex.getMessage());
            sqlPrinter.accept(ex);
        }

        // 2) Personne
        Personne p = new Personne();
        p.setNom("Doe");
        p.setPrenom("John");
        try {
            personneDao.insert(p);
            System.out.println("Inserted Personne id=" + p.getId());
            Personne foundP = personneDao.findById(p.getId());
            System.out.println("Found Personne: " + foundP + " statut=" + foundP.getStatut());

            // Update person
            p.setNom("Doe-Updated");
            personneDao.update(p);
            Personne updated = personneDao.findById(p.getId());
            System.out.println("Updated Personne: " + updated);

            // Soft-delete person
            personneDao.delete(p.getId());
            Personne deleted = personneDao.findById(p.getId());
            System.out.println("After delete, Personne statut=" + (deleted == null ? "NULL" : deleted.getStatut()));
        } catch (SQLException ex) {
            System.err.println("Personne operations failed: " + ex.getMessage());
            sqlPrinter.accept(ex);
        }

        // 3) Badge (linked to person)
        Badge b = new Badge();
        b.setCode("SMOKE-" + System.currentTimeMillis());
        if (p.getId() > 0) b.setPersonneId(p.getId());
        b.setStatut(Badge.Statut.ACTIF);
        try {
            badgeDao.insert(b);
            System.out.println("Inserted Badge id=" + b.getId());
            Badge foundB = badgeDao.findById(b.getId());
            System.out.println("Found Badge: " + foundB + " statut=" + foundB.getStatut());

            // update badge
            b.setCode(b.getCode() + "-U");
            badgeDao.update(b);
            Badge updatedB = badgeDao.findById(b.getId());
            System.out.println("Updated Badge: " + updatedB + " code=" + updatedB.getCode());

            // soft-delete badge
            badgeDao.delete(b.getId());
            Badge deletedB = badgeDao.findById(b.getId());
            System.out.println("After delete, Badge statut=" + (deletedB == null ? "NULL" : deletedB.getStatut()));
        } catch (SQLException ex) {
            System.err.println("Badge operations failed: " + ex.getMessage());
            sqlPrinter.accept(ex);
        }

        // 4) Acces (linked to lieu)
        Acces a = new Acces();
        a.setCode("ACCES-SMOKE");
        if (l.getId() > 0) a.setLieuId(l.getId());
        a.setStatut(Acces.Statut.ACTIF);
        try {
            accesDao.insert(a);
            System.out.println("Inserted Acces id=" + a.getId());
            Acces foundA = accesDao.findById(a.getId());
            System.out.println("Found Acces: " + foundA + " statut=" + foundA.getStatut());

            // update acces
            a.setCode("ACCES-SMOKE-UPD");
            accesDao.update(a);
            Acces updatedA = accesDao.findById(a.getId());
            System.out.println("Updated Acces: " + updatedA + " code=" + updatedA.getCode());

            // soft-delete acces
            accesDao.delete(a.getId());
            Acces deletedA = accesDao.findById(a.getId());
            System.out.println("After delete, Acces statut=" + (deletedA == null ? "NULL" : deletedA.getStatut()));
        } catch (SQLException ex) {
            System.err.println("Acces operations failed: " + ex.getMessage());
            sqlPrinter.accept(ex);
        } finally {
            System.out.println("--- DAO Smoke Test END ---");
            DBConnection.closeQuietly(null);
        }
    }

    private static void printSqlException(SQLException ex) {
        System.err.println("SQLException caught: message=" + ex.getMessage());
        System.err.println("SQLState=" + ex.getSQLState() + " errorCode=" + ex.getErrorCode());
        Throwable next = ex.getCause();
        if (next != null) System.err.println("Cause: " + next);
        SQLException e = ex;
        while (e != null) {
            System.err.println("-> Next exception: " + e.getMessage());
            e = e.getNextException();
        }
        if (ex instanceof SQLIntegrityConstraintViolationException || (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("constraint"))) {
            System.err.println("Detected constraint violation (unique/foreign key/check). See Oracle message above for constraint name and details.");
        }
    }
}
