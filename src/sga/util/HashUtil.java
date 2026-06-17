
// File: src/sga/util/HashUtil.java
package sga.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilitaires de hachage.
 */
public final class HashUtil {

	private HashUtil() {
	}

	/**
	 * Calcule le hash SHA-256 de {@code texte} et le retourne en hexadécimal
	 * minuscule. Retourne null si {@code texte} est null.
	 */
	public static String sha256(String texte) {
		if (texte == null) {
			return null;
		}
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(texte.getBytes(StandardCharsets.UTF_8));
			return bytesToHexLower(digest);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 algorithm not available", e);
		}
	}

	/**
	 * Vérifie que sha256(texte) est égal à {@code hash}. Retourne false si l'un des
	 * paramètres est null.
	 */
	public static boolean verifier(String texte, String hash) {
		if (texte == null || hash == null) {
			return false;
		}
		String calcul = sha256(texte);
		return hash.equals(calcul);
	}

	private static String bytesToHexLower(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			int v = b & 0xFF;
			if (v < 0x10) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString();
	}
}
