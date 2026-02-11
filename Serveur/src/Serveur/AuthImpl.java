package Serveur;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import interfaceRMI.Auth;

public class AuthImpl extends UnicastRemoteObject implements Auth{

	private HashMap<String, String> userBD = new HashMap<>();
	
	public AuthImpl() throws RemoteException {
		super();
		userBD.put("Hugo", "test");
	}
	
	@Override
	public boolean authentification(String login, String password) throws RemoteException {
		
		
		if (userBD.containsKey(login)) {
			String vraimdp = userBD.get(login);
			if (vraimdp.equals(password)) {
				System.out.println("CONNEXION REUSSIE");
				return true;
			}
		}
			System.out.println("CONNEXION ECHOUEE");
			return false;
		}
	}
	

