package sga.view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Point d'entrée principal — SGA avec interface colorée.
 */
public class Main {

    // Palette identique à AccesView
    static final Color C_SIDEBAR  = new Color(0x1A2B4A);
    static final Color C_BG       = new Color(0xF0F4F8);
    static final Color C_WHITE    = Color.WHITE;
    static final Color C_ACCENT   = new Color(0x2563EB);

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("SGA — Système de Gestion d'Accès");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 760);
            frame.setMinimumSize(new Dimension(1000, 600));
            frame.setLocationRelativeTo(null);

            // Icône de fenêtre
            try {
                frame.setIconImage(new ImageIcon(
                    Main.class.getResource("/sga/view/icon.png")).getImage());
            } catch (Exception ignored) {}

            // ── Onglets stylisés ─────────────────────────────────────────────
            JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
            tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
            tabs.setBackground(C_BG);

            // Onglet Gestion des Accès
            tabs.addTab("🔐  Gestion des Accès", new AccesView());

            // Onglet Profil
            tabs.addTab("👤  Profil", new ProfilView());

            // Onglet À propos
            tabs.addTab("ℹ  À propos", buildAboutPanel());

            frame.getContentPane().setBackground(C_BG);
            frame.getContentPane().add(tabs);
            frame.setVisible(true);
        });
    }

    private static JPanel buildAboutPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(C_BG);

        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(C_WHITE);
        card.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE2E8F0)),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)));
        card.setPreferredSize(new Dimension(540, 360));

        // Logo / titre
        JLabel titre = new JLabel("SGA — Système de Gestion d'Accès");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titre.setForeground(new Color(0x1A2B4A));
        titre.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(titre, BorderLayout.NORTH);

        // Infos
        String info =
            "<html><body style='font-family:Segoe UI;font-size:12px;color:#334155;line-height:1.8'>" +
            "<b>Base de données :</b> Oracle XE — jdbc:oracle:thin:@localhost:1521:XE<br>" +
            "<b>Utilisateur :</b> system &nbsp;|&nbsp; <b>Mot de passe :</b> sga<br><br>" +
            "<b>Modules disponibles :</b><br>" +
            "&nbsp;&nbsp;• Gestion des Accès : créer, modifier, lister, rechercher, désactiver<br>" +
            "&nbsp;&nbsp;• Types d'accès : PORTE, BARRIÈRE, PORTILLON, PARKING, ASCENSEUR<br>" +
            "&nbsp;&nbsp;• Contrainte : 1 accès = 1 lieu (FK id_lieu → LIEU)<br><br>" +
            "<b>Configuration connexion :</b><br>" +
            "&nbsp;&nbsp;<code>src/sga/util/DBConnection.java</code><br>" +
            "&nbsp;&nbsp;ou via propriétés système : -Ddb.url=... -Ddb.user=... -Ddb.password=...<br><br>" +
            "<i style='color:#64748B'>En mode hors-ligne, des données de démonstration sont affichées.</i>" +
            "</body></html>";
        JLabel lblInfo = new JLabel(info);
        card.add(lblInfo, BorderLayout.CENTER);

        // Version
        JLabel ver = new JLabel("ISGE-BF • Lot 3 • 2025-2026", SwingConstants.CENTER);
        ver.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        ver.setForeground(new Color(0x94A3B8));
        card.add(ver, BorderLayout.SOUTH);

        p.add(card, new GridBagConstraints());
        return p;
    }
}
