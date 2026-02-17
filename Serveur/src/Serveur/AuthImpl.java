package Serveur;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.UUID;

import interfaceRMI.Auth;

public class AuthImpl extends UnicastRemoteObject implements Auth{

	//BD user 
	private HashMap<String, User> userBD = new HashMap<>();
	
	// BD session active
	private HashMap<String, User> activeSessions = new HashMap<>();
	
	//constructeur
	public AuthImpl() throws RemoteException {
		super();
		userBD.put("hugos", new User("hugoS", "test"));
        userBD.put("julien", new User("julien", "test"));
        userBD.put("hugol", new User("hugoL", "test"));
        userBD.put("fabien", new User("fabien", "test"));
	}
	
	@Override
	public String authentification(String login, String password) throws RemoteException {
		
		User user = userBD.get(login);
		
		if (user != null && user.getPassword().equals(password)) {
			// génère le token
			String token = UUID.randomUUID().toString();
			System.out.println("AUTH >> Token généré pour l'utilisateur " + login);
			
			//Insertion du token dans la bd pour l'user
			activeSessions.put(token, user);
			
			System.out.println("AUTH >> Connexion réussie pour " + login);
			return token;
			}
		
		
		System.out.println("AUTH >> Echec connexion pour " + login);
		return null;
		
	}
	
	@Override
	public boolean vToken(String token) throws RemoteException {
		
		return activeSessions.containsKey(token);
		
	}
	
	@Override
	public String getLoginByToken(String token) throws RemoteException {
		 User utilisateurTrouve = activeSessions.get(token);
		    
		    if (utilisateurTrouve == null) {
		        return null; 
		    }
		  
		    return utilisateurTrouve.getLogin(); 
		}
}
	

