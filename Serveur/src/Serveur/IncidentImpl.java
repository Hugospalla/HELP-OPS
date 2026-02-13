package Serveur;


import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import interfaceRMI.Auth;
import interfaceRMI.Incidents;

public class IncidentImpl extends UnicastRemoteObject implements Incidents{

	//BD Incident
	private HashMap<String, Incident> incidentbd = new HashMap<>();
	
	
	public IncidentImpl() throws RemoteException, MalformedURLException, NotBoundException, InterruptedException {
		super();
	}
	
	private Auth getAuth() {
		try { 
			return (Auth) Naming.lookup("rmi://localhost:1099/AuthService");
		} catch (Exception e ) {
			System.err.println("INC >> Impossible de se connecter au service d'authentification");
			return null;
		}
	}
	
	@Override
	public String creationInc(String token, String categorie, String titre, String desc, String etat, String auteur) throws RemoteException{
		
		Auth auth = getAuth();
		
		if (auth == null) {
			throw new RemoteException("Service d'authentification indisponible");
		}
		
		if(auth.vToken(token) == true) {
			
			String ticketId = UUID.randomUUID().toString();
			
			String ticketAuteur = auth.getLoginByToken(token);
			String ticketEtat = "OPEN";
			
			Incident ticket = new Incident(categorie, titre, desc, ticketEtat, ticketAuteur);
			
			incidentbd.put(ticketId, ticket);
			System.out.println("INC >> Creation d'un ticket pour : " + ticketAuteur);
			System.out.println("> ID ticket : " + ticketId);
			System.out.println("> Titre : " + titre);
			System.out.println("> Catégorie : " + categorie);
			System.out.println("> Description : " + categorie);
			System.out.println("> Auteur : " + ticketAuteur);
			System.out.println("> Auteur token : " + token);
			System.out.println("> Etat : " + ticketEtat);
			
			
			
			return ticketId;
		}else {
			System.out.println("INC >> Erreur lors de la création du ticket");
			return null;
		}
	}
	
	
	@Override
	public List<Incident> mesIncidents(String token) throws RemoteException {
		
		Auth auth = getAuth();
		
		if (auth == null) {
			throw new RemoteException("Service d'authentification indisponible");
		}
		
		
		
		if(auth.vToken(token) == true) {
			
			String demandeur = auth.getLoginByToken(token);
			
			List<Incident> res = new ArrayList<>();
			
			for (Incident ticket : incidentbd.values()) {
				if (ticket.getAuteur().equals(demandeur)) {
					res.add(ticket);
				}
			}
			
			if (res.isEmpty()) {
				System.out.println("INC >> Aucun ticket trouvé pour " + demandeur);
				return null;
			}
			
			
			return res;
		}else {
			System.out.println("INC >> Erreur lors de la création du ticket");
			return null;
		}
		
		
	}
}
	

