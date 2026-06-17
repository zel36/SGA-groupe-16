package org.isgebf.sga.view;

import org.isgebf.sga.controller.PersonneController;
import org.isgebf.sga.model.Personne;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Panel providing UI to list, search, add and update Personne records.
 */
public class PersonnePanel extends JPanel {

    private final PersonneController controller = new PersonneController();
    private final JTable table = new JTable();
    private final DefaultTableModel tableModel;
    private final JTextField txtSearch = new JTextField(20);
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public PersonnePanel() {
        setLayout(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"ID","Nom","Prenom","Fonction","Login","Profil","Statut"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table.setModel(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Recherche:"));
        top.add(txtSearch);
        JButton btnSearch = new JButton("Rechercher");
        btnSearch.addActionListener(e -> doSearch());
        top.add(btnSearch);
        JButton btnRefresh = new JButton("Rafraîchir");
        btnRefresh.addActionListener(e -> loadAll());
        top.add(btnRefresh);
        add(top, BorderLayout.NORTH);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("Ajouter");
        btnAdd.addActionListener(e -> doAdd());
        JButton btnEdit = new JButton("Modifier");
        btnEdit.addActionListener(e -> doEdit());
        JButton btnDelete = new JButton("Supprimer (logique)");
        btnDelete.addActionListener(e -> doDelete());
        bottom.add(btnAdd); bottom.add(btnEdit); bottom.add(btnDelete);
        add(bottom, BorderLayout.SOUTH);

        loadAll();
    }

    private void loadAll() {
        try {
            List<Personne> list = controller.getAllActive();
            refreshTable(list);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doSearch() {
        String term = txtSearch.getText();
        try {
            List<Personne> list = controller.search(term);
            refreshTable(list);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de recherche: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable(java.util.List<Personne> list) {
        tableModel.setRowCount(0);
        for (Personne p : list) {
            tableModel.addRow(new Object[]{p.getIdPersonne(), p.getNom(), p.getPrenom(), p.getFonction(), p.getLogin(), p.getIdProfil(), p.getStatut()});
        }
    }

    private void doAdd() {
        PersonneForm form = new PersonneForm(null);
        int res = JOptionPane.showConfirmDialog(this, form, "Ajouter Personne", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            Personne p = form.getPersonne();
            try {
                controller.create(p);
                loadAll();
                JOptionPane.showMessageDialog(this, "Personne créée avec succès.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void doEdit() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une personne"); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            Personne p = controller.findById(id);
            PersonneForm form = new PersonneForm(p);
            int res = JOptionPane.showConfirmDialog(this, form, "Modifier Personne", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res == JOptionPane.OK_OPTION) {
                Personne updated = form.getPersonne();
                controller.update(updated);
                loadAll();
                JOptionPane.showMessageDialog(this, "Personne mise à jour.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDelete() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une personne"); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        int c = JOptionPane.showConfirmDialog(this, "Confirmer la suppression logique de la personne id=" + id + "?", "Confirmer", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            try {
                controller.deleteLogic(id);
                loadAll();
                JOptionPane.showMessageDialog(this, "Personne marquée INACTIF.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // inner form for person creation/editing
    private class PersonneForm extends JPanel {
        private JTextField txtNom = new JTextField(20);
        private JTextField txtPrenom = new JTextField(20);
        private JTextField txtFonction = new JTextField(15);
        private JTextField txtLogin = new JTextField(15);
        private JPasswordField txtPass = new JPasswordField(15);
        private JTextField txtProfil = new JTextField(5);

        private Personne personne;

        PersonneForm(Personne p) {
            this.personne = p == null ? new Personne() : p;
            setLayout(new GridLayout(0,2,5,5));
            add(new JLabel("Nom:")); add(txtNom);
            add(new JLabel("Prenom:")); add(txtPrenom);
            add(new JLabel("Fonction:")); add(txtFonction);
            add(new JLabel("Login:")); add(txtLogin);
            add(new JLabel("Mot de passe:")); add(txtPass);
            add(new JLabel("ID Profil:")); add(txtProfil);
            if (p != null) {
                txtNom.setText(p.getNom()); txtPrenom.setText(p.getPrenom()); txtFonction.setText(p.getFonction()); txtLogin.setText(p.getLogin()); txtProfil.setText(p.getIdProfil()==null?"":String.valueOf(p.getIdProfil()));
            }
        }

        Personne getPersonne() {
            personne.setNom(txtNom.getText());
            personne.setPrenom(txtPrenom.getText());
            personne.setFonction(txtFonction.getText());
            personne.setLogin(txtLogin.getText());
            String pwd = new String(txtPass.getPassword());
            if (pwd != null && !pwd.trim().isEmpty()) personne.setMotDePasse(pwd);
            try { personne.setIdProfil(txtProfil.getText().trim().isEmpty()?null:Integer.parseInt(txtProfil.getText().trim())); } catch (NumberFormatException ignored) {}
            return personne;
        }
    }
}
