package org.isgebf.sga.view;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window containing navigation menu and main content area.
 */
public class MainFrame extends JFrame {

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    public MainFrame() {
        setTitle("SGA - Système de Gestion d'Accès - ISGE-BF");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        // create panels
        PersonnePanel personnePanel = new PersonnePanel();
        BadgePanel badgePanel = new BadgePanel();
        LieuPanel lieuPanel = new LieuPanel();
        ProfilPanel profilPanel = new ProfilPanel();

        mainPanel.add(personnePanel, "PERSONNE");
        mainPanel.add(badgePanel, "BADGE");
        mainPanel.add(lieuPanel, "LIEU");
        mainPanel.add(profilPanel, "PROFIL");

        setJMenuBar(buildMenuBar());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Navigation");
        JMenuItem mPersonne = new JMenuItem("Personnes");
        mPersonne.addActionListener(e -> cardLayout.show(mainPanel, "PERSONNE"));
        JMenuItem mBadge = new JMenuItem("Badges");
        mBadge.addActionListener(e -> cardLayout.show(mainPanel, "BADGE"));
        JMenuItem mLieu = new JMenuItem("Lieux");
        mLieu.addActionListener(e -> cardLayout.show(mainPanel, "LIEU"));
        JMenuItem mProfil = new JMenuItem("Profils & Droits");
        mProfil.addActionListener(e -> cardLayout.show(mainPanel, "PROFIL"));
        menu.add(mPersonne);
        menu.add(mBadge);
        menu.add(mLieu);
        menu.add(mProfil);
        mb.add(menu);
        return mb;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame f = new MainFrame();
            f.setVisible(true);
        });
    }
}
