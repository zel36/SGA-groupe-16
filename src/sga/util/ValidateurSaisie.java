
// File: src/sga/util/ValidateurSaisie.java
package sga.util;

import java.sql.Date;
import java.util.regex.Pattern;

/**
 * Validations simples pour la saisie.
 */
public final class ValidateurSaisie {

	private static final Pattern BADGE_PATTERN = Pattern.compile("^LEO-\\d{4}-\\d{4}$");

	private ValidateurSaisie() {
		// utilitaire non instanciable
	}

	/**
	 * Retourne false si motif est null ou vide après trim().
	 */
	public static boolean motifNonVide(String motif) {
		return motif != null && !motif.trim().isEmpty();
	}

	/**
	 * Retourne false si debut ou fin est null, ou si fin est avant debut. Retourne
	 * true si fin est égal ou après debut.
	 */
	public static boolean dateCoherente(Date debut, Date fin) {
		if (debut == null || fin == null) {
			return false;
		}
		return !fin.before(debut);
	}

	/**
	 * Vérifie le format LEO-NNNN-NNNN (ex: LEO-1234-5678). Trim préalable.
	 */
	public static boolean numBadgeValide(String num) {
		if (num == null) {
			return false;
		}
		return BADGE_PATTERN.matcher(num.trim()).matches();
	}

	/**
	 * Retourne false si valeur est null ou vide après trim().
	 */
	public static boolean nonVide(String valeur) {
		return valeur != null && !valeur.trim().isEmpty();
	}
}
