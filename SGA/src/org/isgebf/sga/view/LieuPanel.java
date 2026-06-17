package org.isgebf.sga.view;

import org.isgebf.sga.dao.LieuDAO;
import org.isgebf.sga.model.Lieu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel to manage Lieux: configure opening hours and view realtime status
 */
public class LieuPanel extends JPanel {

    private final LieuDAO dao = new LieuDAO();
    private final JTable table = new JTable();
    private final DefaultTableModel tm;

    public LieuPanel() {
        setLayout(new BorderLayout());
        tm = new DefaultTableModel(new Object[]{"ID","Libelle","Emplacement","Capacite","Ouverture","Fermeture","Statut"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table.setModel(tm);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Rafraîchir"); btnRefresh.addActionListener(e -> loadAll());
        JButton btnAdd = new JButton("Ajouter"); btnAdd.addActionListener(e -> doAdd());
        JButton btnEdit = new JButton("Modifier"); btnEdit.addActionListener(e -> doEdit());
        top.add(btnRefresh); top.add(btnAdd); top.add(btnEdit);
        add(top, BorderLayout.NORTH);

        loadAll();
    }

    private void loadAll() {
        try {
            List<Lieu> list = dao.getAllWithRealTimeStatus();
            tm.setRowCount(0);
            for (Lieu l : list) {
                tm.addRow(new Object[]{l.getIdLieu(), l.getLibelleLieu(), l.getEmplacement(), l.getCapaciteMax(), l.getHeureOuverture(), l.getHeureFermeture(), l.getStatut()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doAdd() {
        LieuForm form = new LieuForm(null);
        int res = JOptionPane.showConfirmDialog(this, form, "Ajouter Lieu", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                dao.create(form.getLieu());
                loadAll();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void doEdit() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un lieu"); return; }
        int id = (int) tm.getValueAt(row, 0);
        try {
            Lieu l = dao.findById(id);
            LieuForm form = new LieuForm(l);
            int res = JOptionPane.showConfirmDialog(this, form, "Modifier Lieu", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res == JOptionPane.OK_OPTION) {
                dao.update(form.getLieu());
                loadAll();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class LieuForm extends JPanel {
        private JTextField txtLib = new JTextField(20);
        private JTextField txtEmp = new JTextField(20);
        private JTextField txtCap = new JTextField(6);
        private JTextField txtOuv = new JTextField(6);
        private JTextField txtFer = new JTextField(6);
        private JTextField txtStat = new JTextField(8);
        private Lieu lieu;

        LieuForm(Lieu l) {
            this.lieu = l == null ? new Lieu() : l;
            setLayout(new GridLayout(0,2,5,5));
            add(new JLabel("Libelle:")); add(txtLib);
            add(new JLabel("Emplacement:")); add(txtEmp);
            add(new JLabel("Capacite max:")); add(txtCap);
            add(new JLabel("Heure ouverture (HH:mm):")); add(txtOuv);
            add(new JLabel("Heure fermeture (HH:mm):")); add(txtFer);
            add(new JLabel("Statut:")); add(txtStat);
            if (l != null) {
                txtLib.setText(l.getLibelleLieu()); txtEmp.setText(l.getEmplacement()); txtCap.setText(String.valueOf(l.getCapaciteMax())); txtOuv.setText(l.getHeureOuverture()); txtFer.setText(l.getHeureFermeture()); txtStat.setText(l.getStatut());
            }
        }

        Lieu getLieu() {
            lieu.setLibelleLieu(txtLib.getText().trim());
            lieu.setEmplacement(txtEmp.getText().trim());
            try { lieu.setCapaciteMax(Integer.parseInt(txtCap.getText().trim())); } catch (NumberFormatException ignored) {}
            lieu.setHeureOuverture(txtOuv.getText().trim());
            lieu.setHeureFermeture(txtFer.getText().trim());
            lieu.setStatut(txtStat.getText().trim());
            return lieu;
        }
    }
}
