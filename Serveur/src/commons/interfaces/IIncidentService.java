package commons.interfaces;

import commons.modele.Categorie;
import commons.modele.Incident;
import java.rmi.RemoteException;
import java.util.List;


public interface IIncidentService extends java.rmi.Remote{
	
	public Incident creerIncident(String token, Categorie categorie, String titre, String desc) throws RemoteException;

	public List<Incident> getMesIncidents(String token) throws RemoteException;
	
	public List<Incident> getMesIncidentsAssigned(String token) throws RemoteException;
	
	public List<Incident> getIncidentsOpen(String token) throws RemoteException;
	
	
	public void prendreEnChargeTicket(String token, String idTicket) throws RemoteException;
	
	public void cloturerTicket(String token, String idTicket, String messageResolution) throws RemoteException;
	
	public void ajouterMessageSuivi(String token, String idTicket, String message) throws RemoteException;
	
	public commons.modele.Statistiques obtenirStatistiques(String token) throws RemoteException;

	public void reassignerTicket(String token, String idTicket, String nouvelAgent) throws RemoteException;
	

}

