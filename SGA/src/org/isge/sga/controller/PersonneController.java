package org.isge.sga.controller;

import org.isge.sga.model.entity.Profil;
import org.isge.sga.view.PersonneView;
import sga.dao.PersonneDAO;
import sga.model.Personne;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;

/**
 * Exemple de controller liant une {@link PersonneView} à un {@link PersonneDAO}.
 *
 * Le contrôleur se charge de :
 * - charger les profils dans le JComboBox (via setProfils)
 * - remplir la JTable avec les personnes retournées par le DAO
 * - gérer les actions Ajouter / Modifier / Désactiver
 *
 * Note : la suppression logique (statut = INACTIF) est effectuée en appelant
 * personneDAO.delete(id) : adaptez l'implémentation du DAO si vous préférez
 * une mise à jour explicite d'un champ "statut".
 */
public class PersonneController {

    private final PersonneView view;
    private final PersonneDAO personneDAO;

    public PersonneController(PersonneView view, PersonneDAO personneDAO) {
        this.view = view;
        this.personneDAO = personneDAO;
        initListeners();
    }

    /** Constructeur pratique qui charge aussi une liste de profils dans la vue. */
    public PersonneController(PersonneView view, PersonneDAO personneDAO, List<Profil> profils) {
        this(view, personneDAO);
        loadProfils(profils);
    }

    public void loadProfils(List<Profil> profils) {
        view.setProfils(profils);
    }

    /** Charge les personnes depuis le DAO et remplit la JTable. */
    public void loadPersonnes() {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);
        try {
            List<Personne> personnes = personneDAO.findAll();
            if (personnes == null) return;
            for (Personne p : personnes) {
                // Le modèle Personne fourni dans le projet de base ne contient pas
                // d'information sur le profil ni le statut. Remplissez ces colonnes
                // si vos DAO/entités les exposent (par ex. p.getProfil(), p.isActif()).
                String profilStr = "";
                String actifStr = "";
                model.addRow(new Object[]{p.getId(), p.getNom(), p.getPrenom(), profilStr, actifStr});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Erreur lors du chargement des personnes : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initListeners() {
        view.getBtnAjouter().addActionListener(e -> onAjouter());
        view.getBtnModifier().addActionListener(e -> onModifier());
        view.getBtnDesactiver().addActionListener(e -> onDesactiver());
    }

    private void onAjouter() {
        String nom = view.getTxtNom().getText();
        String prenom = view.getTxtPrenom().getText();
        // Le Profil sélectionné peut être utilisé si votre DAO/entité le supporte
        Profil selectedProfil = (Profil) view.getComboProfil().getSelectedItem();

        Personne p = new Personne();
        p.setNom(nom);
        p.setPrenom(prenom);

        try {
            personneDAO.insert(p);
            loadPersonnes();
            clearForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Erreur à l'ajout : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onModifier() {
        int id = view.getSelectedPersonneId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(view, "Aucune personne sélectionnée.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String nom = view.getTxtNom().getText();
        String prenom = view.getTxtPrenom().getText();

        Personne p = new Personne(id, nom, prenom);
        try {
            personneDAO.update(p);
            loadPersonnes();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Erreur à la modification : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDesactiver() {
        int id = view.getSelectedPersonneId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(view, "Aucune personne sélectionnée.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(view, "Confirmer la désactivation de la personne ?", "Confirmer", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            // La méthode delete du DAO doit effectuer la suppression logique
            // (mise à jour du statut) si c'est ce que vous souhaitez.
            personneDAO.delete(id);
            loadPersonnes();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Erreur lors de la désactivation : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        view.getTxtNom().setText("");
        view.getTxtPrenom().setText("");
        view.getComboProfil().setSelectedIndex(-1);
    }
}
