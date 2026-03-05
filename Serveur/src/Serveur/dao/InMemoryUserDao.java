package Serveur.dao;

import java.util.HashMap;

import Serveur.utils.PasswordUtil;
import commons.modele.Role;
import commons.modele.User;

public class InMemoryUserDao implements IUserDao {

	private HashMap<String, User> userBD = new HashMap<>();
	
	public InMemoryUserDao() {
		
		String mdpHache = PasswordUtil.hash("test");
		
		userBD.put("hugos", new User("hugos", mdpHache, Role.UTILISATEUR));
        userBD.put("julien", new User("julien", mdpHache, Role.UTILISATEUR));
        userBD.put("hugol", new User("hugol", mdpHache, Role.UTILISATEUR));
        userBD.put("fabien", new User("fabien", mdpHache, Role.UTILISATEUR));
        userBD.put("admin", new User("admin", mdpHache, Role.AGENT));
        userBD.put("admin2", new User("admin2", mdpHache, Role.AGENT));
	}
	
	@Override
	public User getUserByLogin(String login) {
		return userBD.get(login);
	}
}
