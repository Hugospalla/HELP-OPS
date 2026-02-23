package Serveur.dao;

import java.util.HashMap;

import commons.modele.User;

public class InMemoryUserDao implements IUserDao {

	private HashMap<String, User> userBD = new HashMap<>();
	
	public InMemoryUserDao() {
		userBD.put("hugos", new User("hugos", "test"));
        userBD.put("julien", new User("julien", "test"));
        userBD.put("hugol", new User("hugol", "test"));
        userBD.put("fabien", new User("fabien", "test"));
	}
	
	@Override
	public User getUserByLogin(String login) {
		return userBD.get(login);
	}
}
