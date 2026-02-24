package commons.modele;

import java.io.Serializable;

public class AuthResponse implements Serializable{

	private String token;
	private String login;
	
	public AuthResponse(String token, String login) {
		this.token = token;
		this.login = login;
	}
	
	public String getToken() {
		return token;
	}
	
	public String getLogin() {
		return login;
	}
}
