package sga.controller;

import sga.dao.AccesDAO;
import sga.dao.AccesDAOImpl;
import sga.dao.LieuDAO;
import sga.dao.LieuDAOImpl;
import sga.model.Acces;
import sga.model.Lieu;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Contrôleur pour la Gestion des Accès.
 *
 * Fonctions offertes :
 *  1. listerTypesAcces()         — liste tous les types d'accès disponibles
 *  2. creerAcces(...)            — créer un accès (type, lieu, libellé, description)
 *  3. modifierAcces(...)         — modifier les caractéristiques d'un accès existant
 *  4. listerAcces(idLieu, type)  — lister avec filtrage par lieu et/ou type
 *  5. rechercherAcces(...)       — rechercher par libellé, type ou lieu
 *  6. desactiverAcces(id)        — désactiver logiquement un accès
 *  7. getAccesById(id)           — retrouver un accès par son ID
 *  8. listerLieux()              — liste les lieux disponibles (pour les formulaires)
 */
public class AccesController {

    private final AccesDAO accesDAO;
    private final LieuDAO  lieuDAO;

    public AccesController() {
        this.accesDAO = new AccesDAOImpl();
        this.lieuDAO  = new LieuDAOImpl();
    }

    // ─── 1. Types d'accès ────────────────────────────────────────────────────

    /**
     * Retourne la liste des types d'accès valides définis dans la contrainte BD.
     * Ex : PORTE, BARRIERE, PORTILLON, PARKING, ASCENSEUR
     */
    public List<String> listerTypesAcces() {
        return Arrays.asList(Acces.TYPES_VALIDES);
    }

    // ─── 2. Créer un accès ───────────────────────────────────────────────────

    /**
     * Crée un nouvel accès.
     *
     * @param libelleAcces libellé de l'accès (obligatoire)
     * @param typeAcces    type parmi PORTE, BARRIERE, PORTILLON, PARKING, ASCENSEUR
     * @param description  description optionnelle
     * @param idLieu       identifiant du lieu associé (obligatoire — 1 accès = 1 lieu)
     * @return l'accès créé avec son id généré
     * @throws IllegalArgumentException si les données sont invalides
     * @throws SQLException             en cas d'erreur BD
     */
    public Acces creerAcces(String libelleAcces, String typeAcces,
                            String description, Integer idLieu)
            throws IllegalArgumentException, SQLException {

        validerDonnees(libelleAcces, typeAcces, idLieu);

        // Vérifier que le lieu existe
        Lieu lieu = lieuDAO.findById(idLieu);
        if (lieu == null) {
            throw new IllegalArgumentException("Lieu introuvable (id=" + idLieu + ").");
        }

        Acces acces = new Acces();
        acces.setLibelleAcces(libelleAcces.trim());
        acces.setTypeAcces(typeAcces.toUpperCase().trim());
        acces.setDescription(description != null ? description.trim() : null);
        acces.setStatut(Acces.STATUT_ACTIF);
        acces.setIdLieu(idLieu);

        accesDAO.insert(acces);
        return acces;
    }

    // ─── 3. Modifier un accès ────────────────────────────────────────────────

    /**
     * Modifie les caractéristiques d'un accès existant.
     *
     * @param idAcces      identifiant de l'accès à modifier
     * @param libelleAcces nouveau libellé
     * @param typeAcces    nouveau type
     * @param description  nouvelle description (null autorisé)
     * @param statut       nouveau statut (ACTIF ou INACTIF)
     * @param idLieu       nouveau lieu (la contrainte 1 lieu par accès est respectée)
     * @return l'accès mis à jour
     */
    public Acces modifierAcces(int idAcces, String libelleAcces, String typeAcces,
                               String description, String statut, Integer idLieu)
            throws IllegalArgumentException, SQLException {

        Acces existant = accesDAO.findById(idAcces);
        if (existant == null) {
            throw new IllegalArgumentException("Accès introuvable (id=" + idAcces + ").");
        }

        validerDonnees(libelleAcces, typeAcces, idLieu);

        if (statut != null && !statut.equals(Acces.STATUT_ACTIF) && !statut.equals(Acces.STATUT_INACTIF)) {
            throw new IllegalArgumentException("Statut invalide : " + statut
                    + ". Valeurs acceptées : ACTIF, INACTIF.");
        }

        // Vérifier que le nouveau lieu existe
        Lieu lieu = lieuDAO.findById(idLieu);
        if (lieu == null) {
            throw new IllegalArgumentException("Lieu introuvable (id=" + idLieu + ").");
        }

        existant.setLibelleAcces(libelleAcces.trim());
        existant.setTypeAcces(typeAcces.toUpperCase().trim());
        existant.setDescription(description != null ? description.trim() : null);
        existant.setStatut(statut != null ? statut : Acces.STATUT_ACTIF);
        existant.setIdLieu(idLieu);

        accesDAO.update(existant);
        return existant;
    }

    // ─── 4. Lister avec filtrage ──────────────────────────────────────────────

    /**
     * Liste tous les accès avec filtres optionnels par lieu et/ou par type.
     *
     * @param idLieu    identifiant du lieu (null = tous les lieux)
     * @param typeAcces type d'accès (null ou vide = tous les types)
     */
    public List<Acces> listerAcces(Integer idLieu, String typeAcces) throws SQLException {
        String type = (typeAcces != null && !typeAcces.isBlank()) ? typeAcces.toUpperCase().trim() : null;
        return accesDAO.findByFilter(idLieu, type);
    }

    /** Liste tous les accès sans filtre. */
    public List<Acces> listerTousAcces() throws SQLException {
        return accesDAO.findAll();
    }

    // ─── 5. Rechercher ───────────────────────────────────────────────────────

    /**
     * Recherche des accès par libellé (partiel), type exact, ou lieu.
     * Tous les paramètres sont optionnels. La recherche par libellé est
     * insensible à la casse et utilise un LIKE.
     *
     * @param libelleKeyword mot-clé dans le libellé (null = ignoré)
     * @param typeAcces      type exact (null = ignoré)
     * @param idLieu         identifiant du lieu (null = ignoré)
     */
    public List<Acces> rechercherAcces(String libelleKeyword, String typeAcces, Integer idLieu)
            throws SQLException {
        String type = (typeAcces != null && !typeAcces.isBlank()) ? typeAcces.toUpperCase().trim() : null;
        return accesDAO.search(libelleKeyword, type, idLieu);
    }

    // ─── 6. Désactiver ────────────────────────────────────────────────────────

    /**
     * Désactive logiquement un accès (passe son statut à INACTIF).
     * La suppression physique est interdite pour maintenir l'intégrité référentielle.
     */
    public void desactiverAcces(int idAcces) throws IllegalArgumentException, SQLException {
        Acces a = accesDAO.findById(idAcces);
        if (a == null) {
            throw new IllegalArgumentException("Accès introuvable (id=" + idAcces + ").");
        }
        if (Acces.STATUT_INACTIF.equals(a.getStatut())) {
            throw new IllegalArgumentException("L'accès est déjà inactif.");
        }
        accesDAO.desactiver(idAcces);
    }

    // ─── 7. Retrouver par ID ─────────────────────────────────────────────────

    public Acces getAccesById(int idAcces) throws SQLException {
        return accesDAO.findById(idAcces);
    }

    // ─── 8. Lieux disponibles ─────────────────────────────────────────────────

    /** Retourne tous les lieux disponibles (pour alimenter les listes déroulantes). */
    public List<Lieu> listerLieux() throws SQLException {
        return lieuDAO.findAll();
    }

    // ─── Validation ──────────────────────────────────────────────────────────

    private void validerDonnees(String libelleAcces, String typeAcces, Integer idLieu)
            throws IllegalArgumentException {
        if (libelleAcces == null || libelleAcces.isBlank()) {
            throw new IllegalArgumentException("Le libellé de l'accès est obligatoire.");
        }
        if (typeAcces == null || typeAcces.isBlank()) {
            throw new IllegalArgumentException("Le type d'accès est obligatoire.");
        }
        boolean typeValide = false;
        for (String t : Acces.TYPES_VALIDES) {
            if (t.equalsIgnoreCase(typeAcces.trim())) { typeValide = true; break; }
        }
        if (!typeValide) {
            throw new IllegalArgumentException("Type d'accès invalide : " + typeAcces
                    + ". Valeurs acceptées : " + Arrays.toString(Acces.TYPES_VALIDES));
        }
        if (idLieu == null) {
            throw new IllegalArgumentException("Le lieu est obligatoire (un accès appartient à un seul lieu).");
        }
    }
}
