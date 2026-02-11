package Serveur;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Serveur {
	
	public static void main(String[] args) throws RemoteException, MalformedURLException{
		
	try {
		
	
		LocateRegistry.createRegistry(1099);
		
		AuthImpl service = new AuthImpl();
		
		Naming.rebind("rmi://localhost:1099/test", service);
	} catch (Exception e) {
		System.err.println("Erreur : " + e.getMessage());
		e.printStackTrace();
	}

}}