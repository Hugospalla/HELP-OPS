package Serveur.session;


import java.util.HashMap;

import commons.modele.User;

public class InMemorySessionManager implements ISessionManager{
	
	
	
	private HashMap<String, User> activeSessions = new HashMap<>();
	
	@Override
	public void createSession(String token, User user) {
		activeSessions.put(token,  user);
	}
	
	@Override
	public boolean isSessionValid(String token) {
		return activeSessions.containsKey(token);
	}
	
	@Override 
	public User getUserByToken(String token) {
		return activeSessions.get(token);
	}
}
