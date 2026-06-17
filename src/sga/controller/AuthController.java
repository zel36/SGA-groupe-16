package sga.controller;

import sga.dao.PersonneDAO;
import sga.dao.PersonneDAOImpl;
import sga.model.Personne;

/**
 * Contrôleur d'authentification minimal.
 */
public class AuthController {

    private static Personne operateurConnecte;
    private PersonneDAO personneDAO = new PersonneDAOImpl();

    /**
     * Tente de connecter un opérateur. Retourne true si authentification réussie.
     */
    public boolean connecter(String login, String motDePasse) {
        try {
            Personne p = personneDAO.authentifier(login, motDePasse);
            if (p != null) {
                operateurConnecte = p;
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void deconnecter() {
        operateurConnecte = null;
    }

    public static Personne getOperateurConnecte() {
        return operateurConnecte;
    }

}
