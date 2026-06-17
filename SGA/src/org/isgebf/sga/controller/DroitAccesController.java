package org.isgebf.sga.controller;

import org.isgebf.sga.dao.DroitAccesDAO;
import org.isgebf.sga.model.DroitAcces;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

/**
 * Controller for DroitAcces business logic: checks time overlaps and delegates DAO operations.
 */
public class DroitAccesController {

    private final DroitAccesDAO dao = new DroitAccesDAO();

    public DroitAcces addDroit(DroitAcces d) throws SQLException, IllegalArgumentException {
        validateDroit(d);
        // check overlaps for same profil, same lieu, same day
        List<DroitAcces> existing = dao.findDroitsByProfil(d.getIdProfil());
        for (DroitAcces e : existing) {
            if (e.getIdLieu() == d.getIdLieu() && e.getJourSemaine() == d.getJourSemaine()) {
                if (intervalsOverlap(e.getHeureDebut(), e.getHeureFin(), d.getHeureDebut(), d.getHeureFin())) {
                    throw new IllegalArgumentException("Time interval overlaps with existing droit (id=" + e.getIdDroit() + ")");
                }
            }
        }
        return dao.addDroit(d);
    }

    public boolean removeDroit(int idDroit) throws SQLException {
        return dao.removeDroit(idDroit);
    }

    public List<DroitAcces> findDroitsByProfil(int idProfil) throws SQLException {
        return dao.findDroitsByProfil(idProfil);
    }

    public boolean checkAccess(int idProfil, int idLieu, int dayOfWeek, String timeHHMM) throws SQLException {
        return dao.checkAccessAuthorization(idProfil, idLieu, dayOfWeek, timeHHMM);
    }

    private void validateDroit(DroitAcces d) {
        if (d == null) throw new IllegalArgumentException("DroitAcces cannot be null");
        if (d.getIdProfil() <= 0) throw new IllegalArgumentException("idProfil is required");
        if (d.getIdLieu() <= 0) throw new IllegalArgumentException("idLieu is required");
        if (d.getJourSemaine() < 1 || d.getJourSemaine() > 7) throw new IllegalArgumentException("jourSemaine must be 1..7");
        if (d.getHeureDebut() == null || d.getHeureFin() == null) throw new IllegalArgumentException("heureDebut and heureFin required");
        try {
            toMinutes(d.getHeureDebut());
            toMinutes(d.getHeureFin());
        } catch (ParseException e) {
            throw new IllegalArgumentException("heureDebut/heureFin must be in HH:mm format");
        }
    }

    private boolean intervalsOverlap(String aStart, String aEnd, String bStart, String bEnd) {
        try {
            int aS = toMinutes(aStart);
            int aE = toMinutes(aEnd);
            int bS = toMinutes(bStart);
            int bE = toMinutes(bEnd);
            if (aS <= aE) {
                if (bS <= bE) {
                    return !(aE < bS || bE < aS);
                } else {
                    // b over midnight
                    return !(aE < bS && bE < aS);
                }
            } else {
                // a over midnight
                if (bS <= bE) {
                    return !(aE < bS && bE < aS);
                } else {
                    // both over midnight - consider overlapping
                    return true;
                }
            }
        } catch (ParseException e) {
            return false;
        }
    }

    private int toMinutes(String hhmm) throws ParseException {
        if (hhmm == null) throw new ParseException("null", 0);
        String[] parts = hhmm.split(":");
        if (parts.length != 2) throw new ParseException(hhmm, 0);
        int h = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);
        if (h < 0 || h > 23 || m < 0 || m > 59) throw new ParseException(hhmm, 0);
        return h * 60 + m;
    }
}
