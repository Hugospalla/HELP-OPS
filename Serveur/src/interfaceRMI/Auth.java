package interfaceRMI;

import java.rmi.RemoteException;

import Serveur.User;



public interface Auth extends java.rmi.Remote{

	public String authentification(String login, String password) throws RemoteException;
		
	public boolean vToken(String token) throws RemoteException;
	
	public String getLoginByToken(String token) throws RemoteException;
	
}
