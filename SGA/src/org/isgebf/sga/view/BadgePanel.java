package org.isgebf.sga.view;

import org.isgebf.sga.controller.BadgeController;
import org.isgebf.sga.dao.BadgeDAO;
import org.isgebf.sga.model.Badge;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Panel to manage badges: issue and revoke
 */
public class BadgePanel extends JPanel {

    private final BadgeController controller = new BadgeController();
    private final BadgeDAO badgeDAO = new BadgeDAO();
    private final JTable table = new JTable();
    private final DefaultTableModel tableModel;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public BadgePanel() {
        setLayout(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"NumBadge","IdPersonne","Emission","Expiration","Statut"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table.setModel(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Rafraîchir");
        btnRefresh.addActionListener(e -> loadActive());
        top.add(btnRefresh);
        add(top, BorderLayout.NORTH);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnIssue = new JButton("Emettre Badge");
        btnIssue.addActionListener(e -> doIssue());
        JButton btnRevoke = new JButton("Révoquer Badge");
        btnRevoke.addActionListener(e -> doRevoke());
        bottom.add(btnIssue); bottom.add(btnRevoke);
        add(bottom, BorderLayout.SOUTH);

        loadActive();
    }

    private void loadActive() {
        try {
            List<Badge> list = badgeDAO.findActiveBadges();
            tableModel.setRowCount(0);
            for (Badge b : list) {
                tableModel.addRow(new Object[]{b.getNumBadge(), b.getIdPersonne(), b.getDateEmission()==null?"":sdf.format(b.getDateEmission()), b.getDateExpiration()==null?"":sdf.format(b.getDateExpiration()), b.getStatut()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doIssue() {
        JPanel form = new JPanel(new GridLayout(0,2,5,5));
        JTextField txtNum = new JTextField(20);
        JTextField txtIdPersonne = new JTextField(6);
        JTextField txtExpiration = new JTextField(10); // yyyy-MM-dd
        form.add(new JLabel("Numéro Badge:")); form.add(txtNum);
        form.add(new JLabel("ID Personne:")); form.add(txtIdPersonne);
        form.add(new JLabel("Date expiration (yyyy-MM-dd) (optionnel):")); form.add(txtExpiration);
        int res = JOptionPane.showConfirmDialog(this, form, "Emettre Badge", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                Badge b = new Badge();
                b.setNumBadge(txtNum.getText().trim());
                b.setIdPersonne(Integer.parseInt(txtIdPersonne.getText().trim()));
                String exp = txtExpiration.getText().trim();
                if (!exp.isEmpty()) b.setDateExpiration(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(exp));
                b.setDateEmission(new Date());
                b.setStatut("ACTIF");
                controller.issueBadge(b);
                loadActive();
                JOptionPane.showMessageDialog(this, "Badge émis.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void doRevoke() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un badge"); return; }
        String num = (String) tableModel.getValueAt(row, 0);
        int c = JOptionPane.showConfirmDialog(this, "Révoquer le badge " + num + " ?", "Confirmer", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            try {
                controller.revokeBadge(num);
                loadActive();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
