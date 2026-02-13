package interfaceRMI;

import java.rmi.RemoteException;
import java.util.List;

import Serveur.Incident;


public interface Incidents extends java.rmi.Remote{
	
	public String creationInc(String token, String categorie, String titre, String desc, String etat, String auteur) throws RemoteException;

	public List<Incident> mesIncidents(String token) throws RemoteException;
}

