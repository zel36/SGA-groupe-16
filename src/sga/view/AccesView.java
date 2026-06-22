package sga.view;

import sga.controller.AccesController;
import sga.model.Acces;
import sga.model.Lieu;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Vue Gestion des Accès — Interface colorée et professionnelle.
 */
public class AccesView extends JPanel {

    // ── Palette de couleurs ───────────────────────────────────────────────────
    static final Color C_BG          = new Color(0xF0F4F8);   // fond général
    static final Color C_SIDEBAR     = new Color(0x1A2B4A);   // bleu marine foncé
    static final Color C_ACCENT      = new Color(0x2563EB);   // bleu primaire
    static final Color C_ACCENT_HVR  = new Color(0x1D4ED8);   // bleu hover
    static final Color C_SUCCESS     = new Color(0x16A34A);   // vert
    static final Color C_DANGER      = new Color(0xDC2626);   // rouge
    static final Color C_WARNING     = new Color(0xD97706);   // orange
    static final Color C_WHITE       = Color.WHITE;
    static final Color C_HEADER_TBL  = new Color(0x1E3A5F);   // en-tête tableau
    static final Color C_ROW_ALT     = new Color(0xEFF6FF);   // ligne alternée
    static final Color C_ROW_SEL     = new Color(0xBFDBFE);   // ligne sélectionnée
    static final Color C_TEXT_DARK   = new Color(0x1E293B);
    static final Color C_TEXT_LIGHT  = new Color(0x64748B);
    static final Color C_PANEL_TITLE = new Color(0x1E3A5F);
    static final Color C_FIELD_BG    = new Color(0xF8FAFC);
    static final Color C_FIELD_BORD  = new Color(0xCBD5E1);

    static final Font F_TITLE   = new Font("Segoe UI", Font.BOLD, 22);
    static final Font F_SECTION = new Font("Segoe UI", Font.BOLD, 13);
    static final Font F_LABEL   = new Font("Segoe UI", Font.PLAIN, 12);
    static final Font F_INPUT   = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font F_BTN     = new Font("Segoe UI", Font.BOLD, 12);
    static final Font F_TABLE_H = new Font("Segoe UI", Font.BOLD, 12);
    static final Font F_TABLE   = new Font("Segoe UI", Font.PLAIN, 12);

    // ── État ─────────────────────────────────────────────────────────────────
    private final AccesController controller = new AccesController();
    private Integer idAccesEnEdition = null;
    private List<Lieu> lieuxCache = new ArrayList<>();

    // ── Composants tableau ────────────────────────────────────────────────────
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel lblCount;

    // ── Composants filtres ────────────────────────────────────────────────────
    private JTextField     tfRecherche;
    private JComboBox<String>   cbFiltreType;
    private JComboBox<LieuItem> cbFiltreList;

    // ── Composants formulaire ─────────────────────────────────────────────────
    private JTextField        tfLibelle;
    private JTextArea         taDescription;
    private JComboBox<String>   cbType;
    private JComboBox<LieuItem> cbLieu;
    private JComboBox<String>   cbStatut;
    private RoundedButton btnSauvegarder;
    private RoundedButton btnNouvel;
    private RoundedButton btnDesactiver;
    private JLabel lblFormTitle;
    private JLabel lblBdStatus;

    // ─────────────────────────────────────────────────────────────────────────
    public AccesView() {
        setLayout(new BorderLayout(0, 0));
        setBackground(C_BG);

        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildMain(),      BorderLayout.CENTER);

        chargerLieux();
        chargerAcces(null, null);
    }

    // ── Barre du haut ─────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(C_SIDEBAR);
        bar.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        // Titre + icône
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        JLabel icon = new JLabel("🔐");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        JLabel titre = new JLabel("Gestion des Accès");
        titre.setFont(F_TITLE);
        titre.setForeground(C_WHITE);
        left.add(icon);
        left.add(titre);
        bar.add(left, BorderLayout.WEST);

        // Statut BD
        lblBdStatus = new JLabel("● Connecté à Oracle XE");
        lblBdStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblBdStatus.setForeground(new Color(0x86EFAC));
        bar.add(lblBdStatus, BorderLayout.EAST);

        return bar;
    }

    // ── Corps principal ────────────────────────────────────────────────────────
    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout(12, 12));
        main.setBackground(C_BG);
        main.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        main.add(buildFiltresPanel(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildTablePanel(), buildFormPanel());
        split.setDividerLocation(780);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setBackground(C_BG);
        main.add(split, BorderLayout.CENTER);

        return main;
    }

    // ── Panel filtres ─────────────────────────────────────────────────────────
    private JPanel buildFiltresPanel() {
        JPanel card = createCard("🔍  Recherche & Filtres");
        JPanel inner = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        inner.setOpaque(false);

        inner.add(makeLabel("Libellé :"));
        tfRecherche = makeTextField(16);
        inner.add(tfRecherche);

        inner.add(makeLabel("Type :"));
        cbFiltreType = new JComboBox<>();
        styleCombo(cbFiltreType);
        cbFiltreType.addItem("-- Tous --");
        for (String t : controller.listerTypesAcces()) cbFiltreType.addItem(t);
        inner.add(cbFiltreType);

        inner.add(makeLabel("Lieu :"));
        cbFiltreList = new JComboBox<>();
        styleCombo(cbFiltreList);
        cbFiltreList.addItem(new LieuItem(null, "-- Tous --"));
        inner.add(cbFiltreList);

        RoundedButton btnSearch = new RoundedButton("  Rechercher", C_ACCENT, C_ACCENT_HVR, C_WHITE);
        btnSearch.addActionListener(e -> lancerRecherche());
        inner.add(btnSearch);

        RoundedButton btnAll = new RoundedButton("  Tout afficher", new Color(0x475569), new Color(0x334155), C_WHITE);
        btnAll.addActionListener(e -> {
            tfRecherche.setText("");
            cbFiltreType.setSelectedIndex(0);
            cbFiltreList.setSelectedIndex(0);
            chargerAcces(null, null);
        });
        inner.add(btnAll);

        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    // ── Panel tableau ─────────────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel card = createCard("📋  Liste des Accès");

        // En-tête compteur
        lblCount = new JLabel("0 accès");
        lblCount.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblCount.setForeground(C_TEXT_LIGHT);
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(lblCount, BorderLayout.EAST);
        card.add(topRow, BorderLayout.NORTH);

        // Tableau
        String[] cols = {"ID", "Libellé", "Type", "Lieu", "Statut", "Description"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(F_TABLE);
        table.setRowHeight(32);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(C_ROW_SEL);
        table.setSelectionForeground(C_TEXT_DARK);
        table.setGridColor(new Color(0xE2E8F0));

        // En-tête tableau
        JTableHeader header = table.getTableHeader();
        header.setFont(F_TABLE_H);
        header.setBackground(C_HEADER_TBL);
        header.setForeground(C_WHITE);
        header.setPreferredSize(new Dimension(0, 36));
        header.setBorder(BorderFactory.createEmptyBorder());

        // Largeurs colonnes
        int[] widths = {45, 175, 100, 145, 75, 190};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Rendu alterné + statut coloré
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (sel) {
                    setBackground(C_ROW_SEL);
                    setForeground(C_TEXT_DARK);
                } else {
                    setBackground(row % 2 == 0 ? C_WHITE : C_ROW_ALT);
                    setForeground(C_TEXT_DARK);
                }
                // Colonne Statut → badge coloré
                if (col == 4 && val != null) {
                    String s = val.toString();
                    setForeground(s.equals("ACTIF") ? C_SUCCESS : C_DANGER);
                    setFont(F_TABLE.deriveFont(Font.BOLD));
                } else {
                    setFont(F_TABLE);
                }
                return this;
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) chargerAccesDansFormulaire();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE2E8F0)));
        scroll.getViewport().setBackground(C_WHITE);
        card.add(scroll, BorderLayout.CENTER);

        // Bouton désactiver en bas
        JPanel bas = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 6));
        bas.setOpaque(false);
        btnDesactiver = new RoundedButton("  Désactiver l'accès", C_DANGER, new Color(0xB91C1C), C_WHITE);
        btnDesactiver.setEnabled(false);
        btnDesactiver.addActionListener(e -> desactiverSelectionne());
        bas.add(btnDesactiver);
        card.add(bas, BorderLayout.SOUTH);

        card.setPreferredSize(new Dimension(780, 0));
        return card;
    }

    // ── Panel formulaire ──────────────────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(C_WHITE);
        card.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE2E8F0)),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)));

        // Titre formulaire
        lblFormTitle = new JLabel("Nouvel Accès");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(C_PANEL_TITLE);
        lblFormTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        card.add(lblFormTitle, BorderLayout.NORTH);

        // Champs
        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
        fields.setOpaque(false);

        fields.add(makeFieldGroup("Libellé *", tfLibelle = makeTextField(20)));
        fields.add(Box.createVerticalStrut(10));

        cbType = new JComboBox<>();
        styleCombo(cbType);
        cbType.addItem("-- Sélectionner --");
        for (String t : controller.listerTypesAcces()) cbType.addItem(t);
        fields.add(makeFieldGroup("Type *", cbType));
        fields.add(Box.createVerticalStrut(10));

        cbLieu = new JComboBox<>();
        styleCombo(cbLieu);
        cbLieu.addItem(new LieuItem(null, "-- Sélectionner --"));
        // Les lieux seront chargés dans chargerLieux()
        fields.add(makeFieldGroup("Lieu *", cbLieu));
        fields.add(Box.createVerticalStrut(10));

        cbStatut = new JComboBox<>(new String[]{Acces.STATUT_ACTIF, Acces.STATUT_INACTIF});
        styleCombo(cbStatut);
        fields.add(makeFieldGroup("Statut", cbStatut));
        fields.add(Box.createVerticalStrut(10));

        JPanel descGroup = new JPanel(new BorderLayout());
        descGroup.setOpaque(false);
        JLabel lDesc = makeLabel("Description");
        lDesc.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        descGroup.add(lDesc, BorderLayout.NORTH);
        taDescription = new JTextArea(5, 20);
        taDescription.setFont(F_INPUT);
        taDescription.setLineWrap(true);
        taDescription.setWrapStyleWord(true);
        taDescription.setBackground(C_FIELD_BG);
        taDescription.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_FIELD_BORD),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        descGroup.add(new JScrollPane(taDescription), BorderLayout.CENTER);
        fields.add(descGroup);

        card.add(fields, BorderLayout.CENTER);

        // Boutons bas formulaire
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        btnNouvel = new RoundedButton("Nouveau", new Color(0x475569), new Color(0x334155), C_WHITE);
        btnNouvel.addActionListener(e -> viderFormulaire());
        btnSauvegarder = new RoundedButton("Enregistrer", C_SUCCESS, new Color(0x15803D), C_WHITE);
        btnSauvegarder.addActionListener(e -> sauvegarder());
        btnPanel.add(btnNouvel);
        btnPanel.add(btnSauvegarder);
        card.add(btnPanel, BorderLayout.SOUTH);

        return card;
    }

    // ── Chargement BD ─────────────────────────────────────────────────────────
    private void chargerLieux() {
        try {
            lieuxCache = controller.listerLieux();
            if (lieuxCache.isEmpty()) {
                // Données de démonstration si BD vide ou inaccessible
                lieuxCache = getDemoLieux();
                lblBdStatus.setText("● Mode démonstration (BD vide)");
                lblBdStatus.setForeground(new Color(0xFCD34D));
            }
        } catch (Exception ex) {
            lieuxCache = getDemoLieux();
            lblBdStatus.setText("⚠ Hors ligne — données démo");
            lblBdStatus.setForeground(new Color(0xFCA5A5));
        }
        // Peupler les deux combos Lieu
        for (Lieu l : lieuxCache) {
            LieuItem item = new LieuItem(l.getIdLieu(), l.getLibelleLieu());
            cbFiltreList.addItem(item);
            cbLieu.addItem(item);
        }
    }

    private void chargerAcces(Integer idLieu, String type) {
        try {
            List<Acces> liste = controller.listerAcces(idLieu, type);
            if (liste.isEmpty() && estModeDemoConnexion()) {
                liste = getDemoAcces();
            }
            remplirTableau(liste);
        } catch (Exception ex) {
            remplirTableau(getDemoAcces());
            lblBdStatus.setText("⚠ Hors ligne — données démo");
            lblBdStatus.setForeground(new Color(0xFCA5A5));
        }
    }

    private boolean estModeDemoConnexion() {
        return lblBdStatus.getText().contains("démo") || lblBdStatus.getText().contains("Hors");
    }

    // ── Actions ────────────────────────────────────────────────────────────────
    private void lancerRecherche() {
        try {
            String kw    = tfRecherche.getText().trim();
            String type  = selectedType(cbFiltreType);
            Integer lieu = selectedLieu(cbFiltreList);
            List<Acces> res = controller.rechercherAcces(kw.isBlank() ? null : kw, type, lieu);
            remplirTableau(res);
        } catch (Exception ex) {
            showError("Erreur de recherche :\n" + ex.getMessage());
        }
    }

    private void sauvegarder() {
        String libelle     = tfLibelle.getText().trim();
        String type        = selectedType(cbType);
        Integer idLieu     = selectedLieu(cbLieu);
        String description = taDescription.getText().trim();
        String statut      = (String) cbStatut.getSelectedItem();

        // Validation visuelle
        if (libelle.isEmpty()) { flashField(tfLibelle); showWarn("Le libellé est obligatoire."); return; }
        if (type == null)      { flashCombo(cbType);    showWarn("Veuillez sélectionner un type."); return; }
        if (idLieu == null)    { flashCombo(cbLieu);    showWarn("Veuillez sélectionner un lieu."); return; }

        try {
            if (idAccesEnEdition == null) {
                Acces a = controller.creerAcces(libelle, type, description, idLieu);
                showSuccess("✅  Accès créé avec succès  (ID " + a.getIdAcces() + ")");
            } else {
                controller.modifierAcces(idAccesEnEdition, libelle, type, description, statut, idLieu);
                showSuccess("✅  Accès modifié avec succès");
            }
            viderFormulaire();
            chargerAcces(null, null);
        } catch (IllegalArgumentException ex) {
            showWarn(ex.getMessage());
        } catch (SQLException ex) {
            showError("Erreur base de données :\n" + ex.getMessage());
        }
    }

    private void chargerAccesDansFormulaire() {
        int row = table.getSelectedRow();
        btnDesactiver.setEnabled(row >= 0);
        if (row < 0) return;
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            Acces a = controller.getAccesById(id);
            if (a == null) return;
            idAccesEnEdition = a.getIdAcces();
            lblFormTitle.setText("Modifier l'Accès #" + id);
            tfLibelle.setText(a.getLibelleAcces());
            taDescription.setText(a.getDescription() != null ? a.getDescription() : "");
            selectComboStr(cbType,   a.getTypeAcces());
            selectComboLieu(cbLieu,  a.getIdLieu());
            selectComboStr(cbStatut, a.getStatut());
            btnDesactiver.setEnabled(Acces.STATUT_ACTIF.equals(a.getStatut()));
        } catch (Exception ex) {
            showError("Impossible de charger l'accès : " + ex.getMessage());
        }
    }

    private void desactiverSelectionne() {
        if (idAccesEnEdition == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
            "Confirmer la désactivation de l'accès #" + idAccesEnEdition + " ?",
            "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            controller.desactiverAcces(idAccesEnEdition);
            showSuccess("✅  Accès désactivé.");
            viderFormulaire();
            chargerAcces(null, null);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void viderFormulaire() {
        idAccesEnEdition = null;
        lblFormTitle.setText("Nouvel Accès");
        tfLibelle.setText("");
        taDescription.setText("");
        cbType.setSelectedIndex(0);
        cbLieu.setSelectedIndex(0);
        cbStatut.setSelectedItem(Acces.STATUT_ACTIF);
        table.clearSelection();
        btnDesactiver.setEnabled(false);
        // Réinitialiser bordures
        tfLibelle.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_FIELD_BORD),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }

    // ── Utilitaires UI ────────────────────────────────────────────────────────
    private void remplirTableau(List<Acces> liste) {
        tableModel.setRowCount(0);
        for (Acces a : liste) {
            String lieu = a.getLibelleLieu() != null ? a.getLibelleLieu()
                        : (a.getIdLieu() != null ? "Lieu #" + a.getIdLieu() : "—");
            tableModel.addRow(new Object[]{
                a.getIdAcces(), a.getLibelleAcces(), a.getTypeAcces(),
                lieu, a.getStatut(), a.getDescription()
            });
        }
        lblCount.setText(liste.size() + " accès trouvé" + (liste.size() > 1 ? "s" : ""));
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(C_WHITE);
        card.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE2E8F0)),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        JLabel lbl = new JLabel(title);
        lbl.setFont(F_SECTION);
        lbl.setForeground(C_PANEL_TITLE);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        card.add(lbl, BorderLayout.NORTH);
        return card;
    }

    private JPanel makeFieldGroup(String labelText, JComponent field) {
        JPanel g = new JPanel(new BorderLayout());
        g.setOpaque(false);
        JLabel lbl = makeLabel(labelText);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        g.add(lbl, BorderLayout.NORTH);
        g.add(field, BorderLayout.CENTER);
        return g;
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_LABEL);
        l.setForeground(C_TEXT_DARK);
        return l;
    }

    private JTextField makeTextField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(F_INPUT);
        tf.setBackground(C_FIELD_BG);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_FIELD_BORD),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        return tf;
    }

    private <T> void styleCombo(JComboBox<T> cb) {
        cb.setFont(F_INPUT);
        cb.setBackground(C_FIELD_BG);
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cb.setPreferredSize(new Dimension(cb.getPreferredSize().width, 32));
    }

    private String selectedType(JComboBox<String> cb) {
        String s = (String) cb.getSelectedItem();
        return (s == null || s.startsWith("--")) ? null : s;
    }

    private Integer selectedLieu(JComboBox<LieuItem> cb) {
        LieuItem it = (LieuItem) cb.getSelectedItem();
        return (it == null) ? null : it.id;
    }

    private void selectComboStr(JComboBox<String> cb, String val) {
        if (val == null) return;
        for (int i = 0; i < cb.getItemCount(); i++)
            if (val.equals(cb.getItemAt(i))) { cb.setSelectedIndex(i); return; }
    }

    private void selectComboLieu(JComboBox<LieuItem> cb, Integer id) {
        if (id == null) return;
        for (int i = 0; i < cb.getItemCount(); i++)
            if (id.equals(cb.getItemAt(i).id)) { cb.setSelectedIndex(i); return; }
    }

    private void flashField(JTextField tf) {
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_DANGER, 2),
            BorderFactory.createEmptyBorder(5, 7, 5, 7)));
    }

    private void flashCombo(JComboBox<?> cb) {
        cb.setBorder(BorderFactory.createLineBorder(C_DANGER, 2));
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Attention", JOptionPane.WARNING_MESSAGE);
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Données de démonstration (fallback sans BD) ────────────────────────────
    private List<Lieu> getDemoLieux() {
        List<Lieu> list = new ArrayList<>();
        list.add(new Lieu(1, "Salle de cours A01",    "Bâtiment A, RDC",         50, 700, 2000, "OUVERT"));
        list.add(new Lieu(2, "Laboratoire Réseaux",   "Bâtiment C, 1er étage",   20, 800, 1800, "OUVERT"));
        list.add(new Lieu(3, "Salle des professeurs", "Bâtiment A, 1er étage",   15, 700, 2200, "BLOQUE"));
        list.add(new Lieu(4, "Parking principal",     "Zone extérieure Nord",    30, 600, 2200, "FERME"));
        list.add(new Lieu(5, "Salle Informatique B02","Bâtiment B, 2ème étage",  30, 700, 2000, "OUVERT"));
        list.add(new Lieu(6, "Administration",        "Bâtiment A, RDC",         10, 800, 1700, "OUVERT"));
        return list;
    }

    private List<Acces> getDemoAcces() {
        List<Acces> list = new ArrayList<>();
        Acces a1 = new Acces(1,"Porte principale A01","PORTE","Entrée salle A01","ACTIF",1);
        a1.setLibelleLieu("Salle de cours A01");
        Acces a2 = new Acces(2,"Porte Labo Réseaux","PORTE","Entrée labo réseaux","ACTIF",2);
        a2.setLibelleLieu("Laboratoire Réseaux");
        Acces a3 = new Acces(3,"Porte salle profs","PORTE","Entrée salle prof","ACTIF",3);
        a3.setLibelleLieu("Salle des professeurs");
        Acces a4 = new Acces(4,"Barrière parking","BARRIERE","Entrée parking","ACTIF",4);
        a4.setLibelleLieu("Parking principal");
        Acces a5 = new Acces(5,"Porte Info B02","PORTE","Entrée Info B02","ACTIF",5);
        a5.setLibelleLieu("Salle Informatique B02");
        list.addAll(Arrays.asList(a1, a2, a3, a4, a5));
        return list;
    }

    // ── LieuItem ──────────────────────────────────────────────────────────────
    static class LieuItem {
        final Integer id;
        final String  libelle;
        LieuItem(Integer id, String libelle) { this.id = id; this.libelle = libelle; }
        @Override public String toString() { return libelle; }
    }

    // ── Bouton arrondi personnalisé ────────────────────────────────────────────
    static class RoundedButton extends JButton {
        private Color bg, bgHover, fg;
        private boolean hover = false;

        RoundedButton(String text, Color bg, Color bgHover, Color fg) {
            super(text);
            this.bg = bg; this.bgHover = bgHover; this.fg = fg;
            setFont(F_BTN);
            setForeground(fg);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(getPreferredSize().width + 20, 34));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                @Override public void mouseExited (MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isEnabled() ? (hover ? bgHover : bg) : new Color(0xCBD5E1));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
