package Serveur;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Serveur {
	
	private static int port = 1099;
	private static String AuthService = "rmi://localhost:1099/AuthService";
	
	public static void main(String[] args) throws RemoteException, MalformedURLException{
		
	try {
		
	
		LocateRegistry.createRegistry(port);
		System.out.println("INIT >> Serveur écoute sur le port " + port);
		
		AuthImpl auth = new AuthImpl();
		
		Naming.rebind(AuthService, auth);
		System.out.println("INIT >> Service d'authentification démarré");
		
	} catch (Exception e) {
		System.err.println("Erreur : " + e.getMessage());
		e.printStackTrace();
	}

}}