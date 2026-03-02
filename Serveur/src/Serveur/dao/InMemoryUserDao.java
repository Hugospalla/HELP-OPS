package Serveur.dao;

import java.util.HashMap;

import commons.modele.Role;
import commons.modele.User;

public class InMemoryUserDao implements IUserDao {

	private HashMap<String, User> userBD = new HashMap<>();
	
	public InMemoryUserDao() {
		userBD.put("hugos", new User("hugos", "test", Role.UTILISATEUR));
        userBD.put("julien", new User("julien", "test", Role.UTILISATEUR));
        userBD.put("hugol", new User("hugol", "test", Role.UTILISATEUR));
        userBD.put("fabien", new User("fabien", "test", Role.UTILISATEUR));
        userBD.put("admin", new User("admin", "test", Role.AGENT));
        userBD.put("admin2", new User("admin2", "test", Role.AGENT));
	}
	
	@Override
	public User getUserByLogin(String login) {
		return userBD.get(login);
	}
}
