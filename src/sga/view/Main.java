package sga.view;

import sga.dao.BadgeDAOImpl;
import sga.dao.PersonneDAOImpl;
import sga.dao.LieuDAOImpl;
import sga.dao.HistoriqueEvenementDAOImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Petit main d'appoint pour simuler le projet :
 * - Ouvre une fenêtre Swing contenant `ProfilView`
 * - Affiche les noms des DAO instanciés (sans exécuter d'opérations BD)
 */
public class Main {

    public static void main(String[] args) {
        // Instanciation des DAO (ne déclenche pas d'accès BD tant qu'on n'appelle pas de méthodes nécessitant une connexion)
        BadgeDAOImpl badgeDao = new BadgeDAOImpl();
        PersonneDAOImpl personneDao = new PersonneDAOImpl();
        LieuDAOImpl lieuDao = new LieuDAOImpl();
        HistoriqueEvenementDAOImpl histDao = new HistoriqueEvenementDAOImpl();

        // Construction de l'interface Swing
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("SGA - Simulation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // Zone centrale : ProfilView
            ProfilView profilView = new ProfilView();
            profilView.setPreferredSize(new Dimension(600, 300));
            frame.add(profilView, BorderLayout.CENTER);

            // Panel bas : informations de simulation
            JPanel bottom = new JPanel(new BorderLayout());
            JTextArea info = new JTextArea();
            info.setEditable(false);
            info.setText("DAO instanciés:\n");
            info.append("- " + badgeDao.getClass().getSimpleName() + "\n");
            info.append("- " + personneDao.getClass().getSimpleName() + "\n");
            info.append("- " + lieuDao.getClass().getSimpleName() + "\n");
            info.append("- " + histDao.getClass().getSimpleName() + "\n");
            bottom.add(new JScrollPane(info), BorderLayout.CENTER);

            JButton simulateButton = new JButton(new AbstractAction("Simuler action") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Ne pas appeler de méthodes qui ouvrent une connexion DB. On simule seulement.
                    JOptionPane.showMessageDialog(frame,
                            "Simulation : on a instancié les DAO (pas d'accès BD effectué).\n"
                                    + "Consultez src pour écrire ou activer des tests).",
                            "Simulation", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            bottom.add(simulateButton, BorderLayout.SOUTH);

            frame.add(bottom, BorderLayout.SOUTH);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
