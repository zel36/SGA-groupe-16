package org.isgebf.sga.controller;

import org.isgebf.sga.dao.ProfilDAO;
import org.isgebf.sga.model.HistoriqueProfil;
import org.isgebf.sga.model.Profil;
import org.isgebf.sga.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Controller for Profil management. Includes operations to create/update and to track profile changes for persons.
 */
public class ProfilController {

    private final ProfilDAO dao = new ProfilDAO();

    public Profil createProfil(Profil p) throws SQLException {
        if (p == null) throw new IllegalArgumentException("Profil cannot be null");
        if (p.getLibelleProfil() == null || p.getLibelleProfil().trim().isEmpty()) throw new IllegalArgumentException("libelleProfil is required");
        if (p.getStatut() == null) p.setStatut("ACTIF");
        if (p.getDateCreation() == null) p.setDateCreation(new Date());
        return dao.create(p);
    }

    public boolean updateProfil(Profil p) throws SQLException {
        if (p == null || p.getIdProfil() <= 0) throw new IllegalArgumentException("Valid profil is required");
        return dao.update(p);
    }

    public boolean deactivateProfil(int idProfil) throws SQLException {
        return dao.deleteLogic(idProfil);
    }

    public Profil findById(int id) throws SQLException {
        return dao.findById(id);
    }

    public List<Profil> getAllActive() throws SQLException {
        return dao.getAllActive();
    }

    /**
     * Change profil for a person and record in historique.
     */
    public boolean changeProfilForPersonne(int idPersonne, Integer idNouveauProfil, String motif, String effectuePar) throws SQLException {
        String selectOld = "SELECT ID_PROFIL FROM PERSONNE WHERE ID_PERSONNE = ?";
        String updatePersonne = "UPDATE PERSONNE SET ID_PROFIL = ? WHERE ID_PERSONNE = ?";
        String insertHist = "INSERT INTO HISTORIQUE_PROFIL (ID_PERSONNE, ID_ANCIEN_PROFIL, ID_NOUVEAU_PROFIL, DATE_CHANGEMENT, MOTIF, EFFECTUE_PAR) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            Integer ancien = null;
            try (PreparedStatement ps = conn.prepareStatement(selectOld)) {
                ps.setInt(1, idPersonne);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int v = rs.getInt(1);
                        if (!rs.wasNull()) ancien = v;
                    }
                }
            }

            try (PreparedStatement ps2 = conn.prepareStatement(updatePersonne)) {
                if (idNouveauProfil != null) ps2.setInt(1, idNouveauProfil); else ps2.setNull(1, java.sql.Types.INTEGER);
                ps2.setInt(2, idPersonne);
                ps2.executeUpdate();
            }

            try (PreparedStatement ps3 = conn.prepareStatement(insertHist)) {
                ps3.setInt(1, idPersonne);
                if (ancien != null) ps3.setInt(2, ancien); else ps3.setNull(2, java.sql.Types.INTEGER);
                if (idNouveauProfil != null) ps3.setInt(3, idNouveauProfil); else ps3.setNull(3, java.sql.Types.INTEGER);
                ps3.setDate(4, new java.sql.Date(new Date().getTime()));
                ps3.setString(5, motif);
                ps3.setString(6, effectuePar);
                ps3.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException ex) {
            throw ex;
        }
    }
}
