package Serveur;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import Serveur.dao.IIncidentDao;
import Serveur.dao.IUserDao;
import Serveur.dao.InMemoryIncidentDao;
import Serveur.dao.InMemoryUserDao;
import Serveur.session.ISessionManager;
import Serveur.session.InMemorySessionManager;

public class Serveur {
	
	private static int port = 1099;
	private static String AuthService = "rmi://localhost:1099/AuthService";
	private static String IncidentService = "rmi://localhost:1099/IncidentService";
	
	public static void main(String[] args) throws RemoteException, MalformedURLException{
		
		try {
			LocateRegistry.createRegistry(port);
			System.out.println("INIT >> Serveur écoute sur le port " + port);
			
			IUserDao dao = new InMemoryUserDao();
			ISessionManager sessionManager = new InMemorySessionManager();
			IIncidentDao incidentDao = new InMemoryIncidentDao();
			
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