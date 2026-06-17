package sga.dao;

import sga.model.Personne;
import sga.util.DBConnection;
import sga.util.HashUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation JDBC de PersonneDAO conforme à la spécification SGA.
 */
public class PersonneDAOImpl implements PersonneDAO {

    @Override
    public Personne findById(int id) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(
                    "SELECT id_personne, nom, prenom, statut, id_profil, login, mot_de_passe FROM PERSONNE WHERE id_personne = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Integer idProfil = rs.getObject("id_profil") != null ? rs.getInt("id_profil") : null;
                Personne p = new Personne(rs.getInt("id_personne"), rs.getString("nom"), rs.getString("prenom"),
                        rs.getString("statut"), idProfil, rs.getString("login"), rs.getString("mot_de_passe"));
                return p;
            }
            return null;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public List<Personne> findAll() throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(
                    "SELECT id_personne, nom, prenom, statut, id_profil, login, mot_de_passe FROM PERSONNE WHERE statut = 'ACTIF'");
            ResultSet rs = ps.executeQuery();
            List<Personne> list = new ArrayList<>();
            while (rs.next()) {
                Integer idProfil = rs.getObject("id_profil") != null ? rs.getInt("id_profil") : null;
                list.add(new Personne(rs.getInt("id_personne"), rs.getString("nom"), rs.getString("prenom"),
                        rs.getString("statut"), idProfil, rs.getString("login"), rs.getString("mot_de_passe")));
            }
            return list;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public List<Personne> findByNom(String motCle) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(
                    "SELECT id_personne, nom, prenom, statut, id_profil, login, mot_de_passe FROM PERSONNE WHERE (UPPER(nom) LIKE ? OR UPPER(prenom) LIKE ?) AND statut = 'ACTIF'");
            String pattern = "%" + (motCle == null ? "" : motCle.trim().toUpperCase()) + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ResultSet rs = ps.executeQuery();
            List<Personne> list = new ArrayList<Personne>();
            while (rs.next()) {
                Integer idProfil = rs.getObject("id_profil") != null ? rs.getInt("id_profil") : null;
                list.add(new Personne(rs.getInt("id_personne"), rs.getString("nom"), rs.getString("prenom"),
                        rs.getString("statut"), idProfil, rs.getString("login"), rs.getString("mot_de_passe")));
            }
            return list;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public void insert(Personne p) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO PERSONNE (id_personne, nom, prenom, statut, id_profil, login, mot_de_passe) VALUES (PERSONNE_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setString(3, p.getStatut());
            if (p.getIdProfil() != null) {
                ps.setInt(4, p.getIdProfil());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setString(5, p.getLogin());
            ps.setString(6, p.getMotDePasse());
            ps.executeUpdate();
            c.commit();
        } catch (SQLException ex) {
            if (c != null)
                c.rollback();
            throw ex;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public void update(Personne p) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(
                    "UPDATE PERSONNE SET nom = ?, prenom = ?, statut = ?, id_profil = ?, login = ?, mot_de_passe = ? WHERE id_personne = ?");
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setString(3, p.getStatut());
            if (p.getIdProfil() != null) {
                ps.setInt(4, p.getIdProfil());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setString(5, p.getLogin());
            ps.setString(6, p.getMotDePasse());
            ps.setInt(7, p.getIdPersonne());
            ps.executeUpdate();
            c.commit();
        } catch (SQLException ex) {
            if (c != null)
                c.rollback();
            throw ex;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public void desactiver(int idPersonne) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement("UPDATE PERSONNE SET statut = 'INACTIF' WHERE id_personne = ?");
            ps.setInt(1, idPersonne);
            ps.executeUpdate();
            c.commit();
        } catch (SQLException ex) {
            if (c != null)
                c.rollback();
            throw ex;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

    @Override
    public Personne authentifier(String login, String motDePasse) throws SQLException {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            String hashed = motDePasse == null ? null : HashUtil.sha256(motDePasse);
            PreparedStatement ps = c.prepareStatement(
                    "SELECT id_personne, nom, prenom, statut, id_profil, login, mot_de_passe FROM PERSONNE WHERE login = ? AND mot_de_passe = ? AND statut = 'ACTIF'");
            ps.setString(1, login);
            ps.setString(2, hashed);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Integer idProfil = rs.getObject("id_profil") != null ? rs.getInt("id_profil") : null;
                return new Personne(rs.getInt("id_personne"), rs.getString("nom"), rs.getString("prenom"),
                        rs.getString("statut"), idProfil, rs.getString("login"), rs.getString("mot_de_passe"));
            }
            return null;
        } finally {
            DBConnection.closeQuietly(c);
        }
    }

}
