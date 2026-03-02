package Serveur;

import Serveur.dao.DatabaseManager;
import Serveur.dao.IIncidentDao;
import Serveur.dao.IUserDao;
import Serveur.dao.JdbcIncidentDao;
import Serveur.dao.JdbcUserDao;
import Serveur.session.ISessionManager;
import Serveur.session.InMemorySessionManager;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Serveur {
	
	private static int port = 1099;
	private static String AuthService = "rmi://localhost:1099/AuthService";
	private static String IncidentService = "rmi://localhost:1099/IncidentService";
	
	public static void main(String[] args) throws RemoteException, MalformedURLException{
		
		try {
			// 1. Initialisation de la base de données SQLite
			DatabaseManager.initDatabase();

			// 2. Démarrage du registre RMI
			LocateRegistry.createRegistry(port);
			System.out.println("INIT >> Serveur écoute sur le port " + port);
			
			// 3. Utilisation des NOUVEAUX DAO JDBC
			IUserDao dao = new JdbcUserDao();
			ISessionManager sessionManager = new InMemorySessionManager(); // On garde la session en RAM
			IIncidentDao incidentDao = new JdbcIncidentDao();
			
			AuthImpl auth = new AuthImpl(dao, sessionManager);
			IncidentImpl inc = new IncidentImpl(incidentDao);
			
			Naming.rebind(AuthService, auth);
			System.out.println("INIT >> Service d'authentification démarré");
			
			Naming.rebind(IncidentService, inc);
			System.out.println("INIT >> Service d'incident démarré");

		} catch (Exception e) {
			System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
		}
	}
}