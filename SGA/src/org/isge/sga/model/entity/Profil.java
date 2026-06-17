package org.isge.sga.model.entity;

/**
 * Classe simple représentant un Profil. Contient un id technique et un libellé affichable.
 * La méthode toString() est redéfinie pour retourner le libellé, ce qui permet
 * au JComboBox de montrer uniquement le texte lisible à l'utilisateur tout en
 * conservant l'objet Profil comme valeur sélectionnée.
 */
public class Profil {

    private Long id;
    private String libelleProfil;

    public Profil() {
    }

    public Profil(Long id, String libelleProfil) {
        this.id = id;
        this.libelleProfil = libelleProfil;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelleProfil() {
        return libelleProfil;
    }

    public void setLibelleProfil(String libelleProfil) {
        this.libelleProfil = libelleProfil;
    }

    @Override
    public String toString() {
        return libelleProfil == null ? "" : libelleProfil;
    }
}
