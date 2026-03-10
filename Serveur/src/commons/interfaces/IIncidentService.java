package commons.interfaces;

import java.rmi.RemoteException;
import java.util.List;

import commons.modele.Categorie;
import commons.modele.Incident;


public interface IIncidentService extends java.rmi.Remote{
	
	public Incident creerIncident(String token, Categorie categorie, String titre, String desc) throws RemoteException;

	public List<Incident> getMesIncidents(String token) throws RemoteException;
	
	public List<Incident> getMesIncidentsAssigned(String token) throws RemoteException;
	
	public List<Incident> getIncidentsOpen(String token) throws RemoteException;
	
	
	public void prendreEnChargeTicket(String token, String idTicket) throws RemoteException;
	
	public void cloturerTicket(String token, String idTicket) throws RemoteException;
}

