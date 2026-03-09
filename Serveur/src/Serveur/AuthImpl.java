package Serveur;

import Serveur.dao.IUserDao;
import Serveur.session.ISessionManager;
import Serveur.utils.PasswordUtil;
import commons.interfaces.IAuthService;
import commons.modele.AuthResponse;
import commons.modele.Role;
import commons.modele.User;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

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
		
		String hashedInput = PasswordUtil.hash(password);
		
		if (user != null && user.getPassword().equals(hashedInput)) {
			String token = UUID.randomUUID().toString();
			sessionManager.createSession(token, user);
			
			System.out.println("AUTH >> Connexion réussi pour " + login + " Role: " + user.getRole());
			return new AuthResponse(token, user.getLogin(), user.getRole());
		}
		
		System.out.println("AUTH >> Echec connexion pour " + login);
		throw new RemoteException("Identifiant ou mot de passe incorrect");
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
	
	public Role getRoleByToken(String token) throws RemoteException{
		User u = sessionManager.getUserByToken(token);
		if (u != null) {
			return u.getRole();
		}
		return null;
	}

}

