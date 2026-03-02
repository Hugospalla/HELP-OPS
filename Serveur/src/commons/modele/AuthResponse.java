package commons.modele;

import java.io.Serializable;

public class AuthResponse implements Serializable{

	private String token;
	private String login;
	private Role role;
	
	public AuthResponse(String token, String login, Role role) {
		this.token = token;
		this.login = login;
		this.role = role;
	}
	
	public String getToken() {
		return token;
	}
	
	public String getLogin() {
		return login;
	}
	
	public Role getRole() {
		return role;
	}
}
