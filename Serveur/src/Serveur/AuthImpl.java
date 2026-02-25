package Serveur;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

import Serveur.dao.IUserDao;
import Serveur.session.ISessionManager;
import commons.interfaces.IAuthService;
import commons.modele.AuthResponse;
import commons.modele.User;

public class AuthImpl extends UnicastRemoteObject implements IAuthService{

	private IUserDao userDao;
	private ISessionManager sessionManager;
	
	public AuthImpl(IUserDao userDao, ISessionManager sessionManager) throws RemoteException {
		super();
		this.userDao = userDao;
		this.sessionManager = sessionManager;
	}
	
	@Override
	public AuthResponse seConnecter(String login, String password) throws RemoteException {
		User user = userDao.getUserByLogin(login);
		
		if (user != null && user.getPassword().equals(password)) {
			String token = UUID.randomUUID().toString();
			sessionManager.createSession(token, user);
			
			System.out.println("AUTH >> Connexion réussi pour " + login);
			return new AuthResponse(token, user.getLogin());
		}
		
		System.out.println("AUTH >> Echec connexion pour " + login);
		throw new RemoteException("Identifiant ou mote de passe incorrect");
	}
	
	@Override
	public boolean isTokenValid(String token) throws RemoteException {
		return sessionManager.isSessionValid(token);
	}
	
	@Override 
	public String getLoginByToken(String token) throws RemoteException{
		User u = sessionManager.getUserByToken(token);
		return (u !=null) ? u.getLogin(): null;
	}
}
	

