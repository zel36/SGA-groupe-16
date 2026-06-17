package org.isgebf.sga.controller;

import org.isgebf.sga.dao.BadgeDAO;
import org.isgebf.sga.model.Badge;
import org.isgebf.sga.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 * Controller for Badge operations enforcing business rules such as one active badge per person.
 */
public class BadgeController {

    private final BadgeDAO dao = new BadgeDAO();

    /**
     * Issue a badge to a person. Ensures there is only one active badge by revoking existing active badges for that person.
     */
    public Badge issueBadge(Badge b) throws SQLException {
        if (b == null) throw new IllegalArgumentException("Badge cannot be null");
        if (b.getNumBadge() == null || b.getNumBadge().trim().isEmpty()) throw new IllegalArgumentException("numBadge is required");
        if (b.getIdPersonne() <= 0) throw new IllegalArgumentException("idPersonne is required");

        String revokeSql = "UPDATE BADGE SET STATUT = 'REVOQUE' WHERE ID_PERSONNE = ? AND STATUT = 'ACTIF'";

        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(revokeSql)) {
                ps.setInt(1, b.getIdPersonne());
                ps.executeUpdate();
            }

            // set default fields if absent
            if (b.getDateEmission() == null) b.setDateEmission(new Date());
            if (b.getStatut() == null) b.setStatut("ACTIF");

            // use DAO but passing the same connection is complex; instead just call DAO which opens its own connection for insertion
            // to keep atomicity across revocation and creation we insert here directly
            final String insert = "INSERT INTO BADGE (NUM_BADGE, DATE_EMISSION, DATE_EXPIRATION, STATUT, ID_PERSONNE) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps2 = conn.prepareStatement(insert)) {
                ps2.setString(1, b.getNumBadge());
                ps2.setDate(2, new java.sql.Date(b.getDateEmission().getTime()));
                if (b.getDateExpiration() != null) ps2.setDate(3, new java.sql.Date(b.getDateExpiration().getTime())); else ps2.setNull(3, java.sql.Types.DATE);
                ps2.setString(4, b.getStatut());
                ps2.setInt(5, b.getIdPersonne());
                ps2.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
        }
        return b;
    }

    public boolean revokeBadge(String numBadge) throws SQLException {
        if (numBadge == null || numBadge.trim().isEmpty()) throw new IllegalArgumentException("numBadge is required");
        return dao.deleteLogic(numBadge);
    }
}
