package Serveur.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class PasswordUtil {

	public static String hash(String password) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
			
			StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
			for(int i = 0; i < encodedhash.length; i++) {
				String hex = Integer.toHexString(0xff & encodedhash[i]);
				if(hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception e) {
			throw new RuntimeException("Erreur critique lors du hachage du mot de passe", e);
		}
	}
}