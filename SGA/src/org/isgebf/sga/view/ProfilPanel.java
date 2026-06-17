package org.isgebf.sga.view;

import org.isgebf.sga.controller.DroitAccesController;
import org.isgebf.sga.controller.ProfilController;
import org.isgebf.sga.dao.LieuDAO;
import org.isgebf.sga.dao.ProfilDAO;
import org.isgebf.sga.model.DroitAcces;
import org.isgebf.sga.model.Lieu;
import org.isgebf.sga.model.Profil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel to manage profiles and assign fine-grained access rights
 */
public class ProfilPanel extends JPanel {

    private final ProfilController profilController = new ProfilController();
    private final ProfilDAO profilDAO = new ProfilDAO();
    private final LieuDAO lieuDAO = new LieuDAO();
    private final DroitAccesController droitController = new DroitAccesController();

    private final JTable table = new JTable();
    private final DefaultTableModel tm;

    public ProfilPanel() {
        setLayout(new BorderLayout());
        tm = new DefaultTableModel(new Object[]{"ID","Libelle","Description","DateCreation","Statut"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table.setModel(tm);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Rafraîchir"); btnRefresh.addActionListener(e -> loadAll());
        JButton btnAdd = new JButton("Ajouter Profil"); btnAdd.addActionListener(e -> doAddProfil());
        JButton btnAssign = new JButton("Assigner Droit"); btnAssign.addActionListener(e -> doAssignDroit());
        top.add(btnRefresh); top.add(btnAdd); top.add(btnAssign);
        add(top, BorderLayout.NORTH);

        loadAll();
    }

    private void loadAll() {
        try {
            List<Profil> list = profilDAO.getAllActive();
            tm.setRowCount(0);
            for (Profil p : list) {
                tm.addRow(new Object[]{p.getIdProfil(), p.getLibelleProfil(), p.getDescription(), p.getDateCreation(), p.getStatut()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doAddProfil() {
        JPanel form = new JPanel(new GridLayout(0,2,5,5));
        JTextField txtLib = new JTextField(20);
        JTextField txtDesc = new JTextField(40);
        form.add(new JLabel("Libelle:")); form.add(txtLib);
        form.add(new JLabel("Description:")); form.add(txtDesc);
        int res = JOptionPane.showConfirmDialog(this, form, "Ajouter Profil", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                Profil p = new Profil();
                p.setLibelleProfil(txtLib.getText().trim());
                p.setDescription(txtDesc.getText().trim());
                p.setDateCreation(new java.util.Date());
                p.setStatut("ACTIF");
                profilController.createProfil(p);
                loadAll();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void doAssignDroit() {
        try {
            List<Profil> profils = profilDAO.getAllActive();
            List<Lieu> lieux = lieuDAO.getAllWithRealTimeStatus();
            if (profils.isEmpty() || lieux.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Il faut au moins un profil et un lieu configurés.");
                return;
            }
            JPanel form = new JPanel(new GridLayout(0,2,5,5));
            JComboBox<Profil> cbProfil = new JComboBox<>(profils.toArray(new Profil[0]));
            JComboBox<Lieu> cbLieu = new JComboBox<>(lieux.toArray(new Lieu[0]));
            JComboBox<String> cbDay = new JComboBox<>(new String[]{"1","2","3","4","5","6","7"});
            JTextField txtStart = new JTextField("08:00");
            JTextField txtEnd = new JTextField("17:00");
            form.add(new JLabel("Profil:")); form.add(cbProfil);
            form.add(new JLabel("Lieu:")); form.add(cbLieu);
            form.add(new JLabel("Jour (1..7):")); form.add(cbDay);
            form.add(new JLabel("Heure debut (HH:mm):")); form.add(txtStart);
            form.add(new JLabel("Heure fin (HH:mm):")); form.add(txtEnd);
            int res = JOptionPane.showConfirmDialog(this, form, "Assigner droit", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res == JOptionPane.OK_OPTION) {
                Profil selProfil = (Profil) cbProfil.getSelectedItem();
                Lieu selLieu = (Lieu) cbLieu.getSelectedItem();
                int day = Integer.parseInt((String) cbDay.getSelectedItem());
                DroitAcces d = new DroitAcces();
                d.setIdProfil(selProfil.getIdProfil());
                d.setIdLieu(selLieu.getIdLieu());
                d.setJourSemaine(day);
                d.setHeureDebut(txtStart.getText().trim());
                d.setHeureFin(txtEnd.getText().trim());
                droitController.addDroit(d);
                JOptionPane.showMessageDialog(this, "Droit assigné.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
