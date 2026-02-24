package commons.interfaces;

import java.rmi.RemoteException;

import commons.modele.AuthResponse;



public interface IAuthService extends java.rmi.Remote{

	public AuthResponse seConnecter(String login, String password) throws RemoteException;
		
	public boolean isTokenValid(String token) throws RemoteException;
	
	public String getLoginByToken(String token) throws RemoteException;
	
}
