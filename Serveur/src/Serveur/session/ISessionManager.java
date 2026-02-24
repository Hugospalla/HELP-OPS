package Serveur.session;

import commons.modele.User;

public interface ISessionManager {

	void createSession(String token, User user);
	boolean isSessionValid(String token);
	User getUserByToken(String token);
}
