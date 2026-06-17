package sga.controller;

import sga.dao.BadgeDAO;
import sga.dao.BadgeDAOImpl;
import sga.dao.HistoriqueEvenementDAO;
import sga.dao.HistoriqueEvenementDAOImpl;
import sga.dao.LieuDAO;
import sga.dao.LieuDAOImpl;
import sga.model.Badge;
import sga.model.HistoriqueEvenement;
import sga.model.Lieu;
import sga.model.Personne;
import sga.util.ValidateurSaisie;

import java.sql.Date;
import java.util.List;

/**
 * Contrôleur de supervision : opérations de blocage/déblocage et consultations.
 */
public class SupervisionController {

    private BadgeDAO badgeDAO = new BadgeDAOImpl();
    private LieuDAO lieuDAO = new LieuDAOImpl();
    private HistoriqueEvenementDAO historiqueDAO = new HistoriqueEvenementDAOImpl();
    private Personne operateur;

    public SupervisionController(Personne operateur) {
        this.operateur = operateur;
    }

    public String bloquerBadge(String numBadge, String motif) {
        if (!ValidateurSaisie.motifNonVide(motif)) {
            return "ERREUR : Le motif est obligatoire pour un blocage.";
        }
        try {
            Badge b = badgeDAO.findByNumBadge(numBadge);
            if (b == null) {
                return "ERREUR : Badge introuvable.";
            }
            if (!b.peutEtreBloque()) {
                return "ERREUR : Le badge ne peut pas être bloqué dans son état actuel.";
            }
            boolean ok = badgeDAO.bloquerBadge(numBadge, motif, operateur.getIdPersonne(), operateur.getNomComplet());
            if (ok) {
                return "SUCCÈS : Badge bloqué.";
            }
            return "ERREUR : Échec lors du blocage du badge.";
        } catch (Exception e) {
            return "ERREUR : " + e.getMessage();
        }
    }

    public String debloquerBadge(String numBadge, String justification) {
        if (!ValidateurSaisie.motifNonVide(justification)) {
            return "ERREUR : La justification est obligatoire pour un déblocage.";
        }
        try {
            Badge b = badgeDAO.findByNumBadge(numBadge);
            if (b == null) {
                return "ERREUR : Badge introuvable.";
            }
            if (!b.peutEtreDebloque()) {
                return "ERREUR : Le badge ne peut pas être débloqué dans son état actuel.";
            }
            boolean ok = badgeDAO.debloquerBadge(numBadge, justification, operateur.getIdPersonne(), operateur.getNomComplet());
            if (ok) {
                return "SUCCÈS : Badge débloqué.";
            }
            return "ERREUR : Échec lors du déblocage du badge.";
        } catch (Exception e) {
            return "ERREUR : " + e.getMessage();
        }
    }

    public String bloquerLieu(int idLieu, String motif) {
        if (!ValidateurSaisie.motifNonVide(motif)) {
            return "ERREUR : Le motif est obligatoire pour un blocage.";
        }
        try {
            Lieu l = lieuDAO.findById(idLieu);
            if (l == null) {
                return "ERREUR : Lieu introuvable.";
            }
            if (!l.peutEtreBloque()) {
                return "ERREUR : Le lieu ne peut pas être bloqué dans son état actuel.";
            }
            boolean ok = lieuDAO.bloquerLieu(idLieu, motif, operateur.getIdPersonne(), operateur.getNomComplet());
            if (ok) {
                return "SUCCÈS : Lieu bloqué.";
            }
            return "ERREUR : Échec lors du blocage du lieu.";
        } catch (Exception e) {
            return "ERREUR : " + e.getMessage();
        }
    }

    public String debloquerLieu(int idLieu, String justification) {
        if (!ValidateurSaisie.motifNonVide(justification)) {
            return "ERREUR : La justification est obligatoire pour un déblocage.";
        }
        try {
            Lieu l = lieuDAO.findById(idLieu);
            if (l == null) {
                return "ERREUR : Lieu introuvable.";
            }
            if (!l.peutEtreDebloque()) {
                return "ERREUR : Le lieu ne peut pas être débloqué dans son état actuel.";
            }
            boolean ok = lieuDAO.debloquerLieu(idLieu, justification, operateur.getIdPersonne(), operateur.getNomComplet());
            if (ok) {
                return "SUCCÈS : Lieu débloqué.";
            }
            return "ERREUR : Échec lors du déblocage du lieu.";
        } catch (Exception e) {
            return "ERREUR : " + e.getMessage();
        }
    }

    public List<HistoriqueEvenement> consulterJournal(String typeFiltre, Date debut, Date fin) {
        try {
            if (debut != null && fin != null && !ValidateurSaisie.dateCoherente(debut, fin)) {
                return null;
            }
            if (typeFiltre != null && !typeFiltre.trim().isEmpty()) {
                return historiqueDAO.findByType(typeFiltre);
            }
            if (debut != null && fin != null) {
                return historiqueDAO.findByPeriode(debut, fin);
            }
            return historiqueDAO.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    // La génération de rapport est laissée simple : retourne l'objet HistoriqueEvenement sous forme de liste
    public Object genererRapport(Date debut, Date fin, Integer idPersonne, Integer idLieu) {
        if (!ValidateurSaisie.dateCoherente(debut, fin)) {
            return null;
        }
        // Pour simplicité, on retourne la liste des événements sur la période filtrée (le reporting détaillé peut être ajouté)
        try {
            return historiqueDAO.findByPeriode(debut, fin);
        } catch (Exception e) {
            return null;
        }
    }

}
