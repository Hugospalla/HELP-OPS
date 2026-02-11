package interfaceRMI;

import java.rmi.RemoteException;

import Serveur.User;



public interface Auth extends java.rmi.Remote{

	public boolean authentification(String login, String password) throws RemoteException;
	
	
}
