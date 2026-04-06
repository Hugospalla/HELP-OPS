package Serveur;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import Serveur.dao.DatabaseManager;
import Serveur.dao.JdbcUserDao;
import Serveur.session.InMemorySessionManager;

public class ServeurAuth {

	public static void main(String[] args) {
		try {
			DatabaseManager.initDatabase();
			LocateRegistry.createRegistry(1099);
		
			JdbcUserDao userDao = new JdbcUserDao();
			InMemorySessionManager sessionManager = new InMemorySessionManager();
			AuthImpl authService = new AuthImpl(userDao, sessionManager);
		
			Naming.rebind("rmi://localhost:1099/AuthService", authService);
			System.out.println("AUTH >> Serveur d'authentification prêt ! Port : 1099");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
