package org.isge.sga.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import org.isge.sga.model.entity.Profil;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Vue Swing pour gérer les personnes.
 * Conçue pour être compatible avec Eclipse WindowBuilder.
 */
public class PersonneView extends JFrame {

    private JPanel contentPane;
    private JTable tablePersonnes;
    private DefaultTableModel tableModel;
    private JTextField txtNom;
    private JTextField txtPrenom;
    private JComboBox<Profil> comboProfil;
    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnDesactiver;

    public PersonneView() {
        setTitle("Gestion des personnes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));

        contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        // Table panel
        tableModel = new DefaultTableModel(new Object[][] {}, new String[] {"ID", "Nom", "Prénom", "Profil", "Actif"}) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table non éditable directement
            }
        };

        tablePersonnes = new JTable(tableModel);
        tablePersonnes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePersonnes.setAutoCreateRowSorter(true);

        // Masquer la colonne ID pour l'utilisateur mais la garder dans le TableModel
        if (tablePersonnes.getColumnModel().getColumnCount() > 0) {
            try {
                tablePersonnes.getColumnModel().getColumn(0).setMinWidth(0);
                tablePersonnes.getColumnModel().getColumn(0).setMaxWidth(0);
                tablePersonnes.getColumnModel().getColumn(0).setPreferredWidth(0);
                tablePersonnes.getColumnModel().getColumn(0).setResizable(false);
            } catch (Exception ex) {
                // Si pour une raison quelconque la colonne n'existe pas, on ignore
            }
        }

        JScrollPane scrollPane = new JScrollPane(tablePersonnes);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Form panel (north)
        JPanel panelForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        panelForm.add(new JLabel("Nom:"));
        txtNom = new JTextField();
        txtNom.setColumns(15);
        panelForm.add(txtNom);

        panelForm.add(new JLabel("Prénom:"));
        txtPrenom = new JTextField();
        txtPrenom.setColumns(15);
        panelForm.add(txtPrenom);

        panelForm.add(new JLabel("Profil:"));
        comboProfil = new JComboBox<>();
        comboProfil.setPreferredSize(new Dimension(150, 24));
        // Jeu d'exemple temporaire; idéalement le contrôleur appellera setProfils(...)
        comboProfil.addItem(new Profil(1L, "ADMIN"));
        comboProfil.addItem(new Profil(2L, "AGENT"));
        comboProfil.addItem(new Profil(3L, "VISITEUR"));
        panelForm.add(comboProfil);

        contentPane.add(panelForm, BorderLayout.NORTH);

        // Buttons panel (south)
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnAjouter = new JButton("Ajouter");
        btnModifier = new JButton("Modifier");
        btnDesactiver = new JButton("Désactiver");

        panelButtons.add(btnAjouter);
        panelButtons.add(btnModifier);
        panelButtons.add(btnDesactiver);

        contentPane.add(panelButtons, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        // Listener de sélection : remplir les champs quand une ligne est sélectionnée
        tablePersonnes.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                int viewRow = tablePersonnes.getSelectedRow();
                if (viewRow >= 0) {
                    int modelRow = tablePersonnes.convertRowIndexToModel(viewRow);
                    Object nom = tableModel.getValueAt(modelRow, 1);
                    Object prenom = tableModel.getValueAt(modelRow, 2);
                    Object profil = tableModel.getValueAt(modelRow, 3);

                    txtNom.setText(nom != null ? nom.toString() : "");
                    txtPrenom.setText(prenom != null ? prenom.toString() : "");

                    // Sélectionner l'item du combo correspondant au profil (compare toString)
                    if (profil != null) {
                        String profStr = profil.toString();
                        boolean found = false;
                        for (int i = 0; i < comboProfil.getItemCount(); i++) {
                            Object item = comboProfil.getItemAt(i);
                            if (item != null && profStr.equals(item.toString())) {
                                comboProfil.setSelectedIndex(i);
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            // Si le profil sélectionné n'est pas dans la combo, on ajoute un Profil temporaire
                            Profil p = new Profil(null, profStr);
                            comboProfil.addItem(p);
                            comboProfil.setSelectedItem(p);
                        }
                    } else {
                        comboProfil.setSelectedIndex(-1);
                    }
                }
            }
        });
    }

    /**
     * Remplit le JComboBox des profils depuis une liste dynamique.
     * Accepte n'importe quel type d'objet (par ex. vos objets Profil) et utilise toString() pour l'affichage.
     * Le contrôleur pourra appeler cette méthode avec une List<Profil>.
     */
    public void setProfils(List<Profil> profils) {
        comboProfil.removeAllItems();
        if (profils == null) return;
        for (Profil p : profils) {
            comboProfil.addItem(p);
        }
    }

    // Getters pour utilisation par le contrôleur
    public JTable getTablePersonnes() {
        return tablePersonnes;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getTxtNom() {
        return txtNom;
    }

    public JTextField getTxtPrenom() {
        return txtPrenom;
    }

    public JComboBox<Profil> getComboProfil() {
        return comboProfil;
    }

    public JButton getBtnAjouter() {
        return btnAjouter;
    }

    public JButton getBtnModifier() {
        return btnModifier;
    }

    public JButton getBtnDesactiver() {
        return btnDesactiver;
    }

}
