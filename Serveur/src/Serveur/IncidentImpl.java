package Serveur;

import java.util.ArrayList;
import java.util.List;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.UUID;

import Serveur.dao.IIncidentDao;
import commons.interfaces.IAuthService;
import commons.interfaces.IIncidentService;
import commons.modele.Categorie;
import commons.modele.Etat;
import commons.modele.Incident;
import commons.modele.Role;

public class IncidentImpl extends UnicastRemoteObject implements IIncidentService{

	private IIncidentDao incidentDao;
	
	public IncidentImpl(IIncidentDao incidentDao) throws RemoteException {
		super();
		this.incidentDao = incidentDao;
	}
	
	private IAuthService getAuth() {
		try {
			return(IAuthService) Naming.lookup("rmi://localhost:1099/AuthService");
		} catch (Exception e) {
			System.err.println("INC >> Impossible de se connecter au service d'authentification");
			return null;
		}
	}
	
	@Override
	public Incident creerIncident(String token, Categorie categorie, String titre, String desc) throws RemoteException {
		
		IAuthService auth = getAuth();
		if (auth == null) throw new RemoteException("Service d'authentification indisponible");
		
		if(auth.isTokenValid(token)) {
			
			Role roleDemandeur = auth.getRoleByToken(token);
			
			if(roleDemandeur != Role.UTILISATEUR) {
				throw new RemoteException("Accès refusé : Seul un agent a le droit d'afficher tous les tickets");
			}
			
			String ticketAuteur = auth.getLoginByToken(token);
			String ticketId = UUID.randomUUID().toString();
			
			Incident ticket = new Incident(ticketId, categorie, titre, desc, ticketAuteur);
			
			incidentDao.save(ticket);
			
			System.out.println("INC >> Creation d'un ticket [" + ticketId.substring(0,8) + "] pour : " + ticketAuteur);
			return ticket;
		} else {
			System.out.println("INC >> Session invalide, impossible de créer le ticket.");
			throw new RemoteException("Votre session est invalide ou a expiré. Veuillez vous reconnecter");
		}
	}
	
	@Override
	public List<Incident> getMesIncidents(String token) throws RemoteException {
		
		IAuthService auth = getAuth();
		if (auth == null) throw new RemoteException("Service d'authentification indisponible");
		
		if (auth.isTokenValid(token)) {
			Role roleDemandeur = auth.getRoleByToken(token);
			
			if(roleDemandeur != Role.UTILISATEUR) {
				throw new RemoteException("Accès refusé : Seul un agent a le droit d'afficher tous les tickets");
			}
			
			String demandeur = auth.getLoginByToken(token);
			
			List<Incident> res = incidentDao.getIncidentsByAuteur(demandeur);
			
			if (res.isEmpty()) {
				System.out.println("INC >> Aucun ticket trouvé pour " + demandeur);
				return null;
			}
			return res;
		} else {
			System.out.println("INC >> Session invalide lors de la consultation.");
			throw new RemoteException("Votre session est invalide ou a expiré. Veuillez vous reconnecter");
		}
	}
	
	@Override
	public List<Incident> getMesIncidentsAssigned(String token) throws RemoteException{
		
		IAuthService auth = getAuth();
		if (auth == null) throw new RemoteException("Service d'authentification indisponible");
		
		if (auth.isTokenValid(token)) {
			
			Role roleDemandeur = auth.getRoleByToken(token);
			
			if (roleDemandeur != Role.AGENT) {
				throw new RemoteException("Accès refusé : Seul un agent a le droit d'afficher tous les tickets");
			}
			
			String nomAgent = auth.getLoginByToken(token);
			
			List<Incident> tous = incidentDao.getAllIncidents();
			List<Incident> mesAssigned = new ArrayList<>();
			
			for (Incident inc : tous) {
				if (inc.getEtat() == Etat.ASSIGNED && nomAgent.equals(inc.getAgentId())) {
					mesAssigned.add(inc);
				}
			}
			return mesAssigned;
			
		} else {
			throw new RemoteException("Session invalide ou expiré");
		}
	}
	
	@Override
	public List<Incident> getIncidentsOpen(String token) throws RemoteException{
		
		IAuthService auth = getAuth();
		if (auth == null) throw new RemoteException("Service d'authentification indisponible");
		
		if (auth.isTokenValid(token)) {
			
			Role roleDemandeur = auth.getRoleByToken(token);
			
			if (roleDemandeur != Role.AGENT) {
				throw new RemoteException("Accès refusé : Seul un agent a le droit d'afficher tous les tickets");
			}
			
			List<Incident> tous = incidentDao.getAllIncidents();
			List<Incident> ouverts = new ArrayList<>();
			
			for (Incident inc: tous) {
				if (inc.getEtat() == Etat.OPEN) {
					ouverts.add(inc);
				}
			} 
			
			return ouverts;
			
		} else {
			throw new RemoteException("Session invalide ou expiré");
		}
	}
	

	
	@Override
	public synchronized void prendreEnChargeTicket(String token, String idTicket) throws RemoteException{
		
		IAuthService auth = getAuth();
		if (auth == null) throw new RemoteException("Service d'authentification indisponible");
		
		if (auth.isTokenValid(token)) {
			Role roleDemandeur = auth.getRoleByToken(token);
			
			if (roleDemandeur != Role.AGENT) {
				throw new RemoteException("Accès refusé : Seul un agent a le droit d'afficher tous les tickets");
			}
			
			Incident ticket = incidentDao.getIncidentsById(idTicket);
			if (ticket == null) {
				throw new RemoteException("Erreur: le ticket '" + idTicket + "' n'existe pas");
			}
			
			if (ticket.getEtat() != Etat.OPEN) {
				throw new RemoteException("Ce ticket ne peut pas être pris");
			}
			
			ticket.setAgentId(auth.getLoginByToken(token));
			ticket.setDateAssignation(LocalDateTime.now());
			ticket.setEtat(Etat.ASSIGNED);
			
			
			incidentDao.save(ticket);
			System.out.println("INC >> Ticket : " + idTicket + "assigné à " + ticket.getAgentId());
		} else {
			throw new RemoteException("Session invalide ou expirée");
		}
		
	}
	
	@Override
	public void cloturerTicket(String token, String idTicket) throws RemoteException {
		
		IAuthService auth = getAuth();
		if (auth == null) throw new RemoteException("Service d'authentification indisponible");
		
		if (auth.isTokenValid(token)) {
			
			Role roleDemandeur = auth.getRoleByToken(token);
			String nomDemandeur = auth.getLoginByToken(token);
			
			
			if (roleDemandeur != Role.AGENT) {
				throw new RemoteException("Accès refusé : Seul un agent a le droit d'afficher tous les tickets");
			}
			
			Incident ticket = incidentDao.getIncidentsById(idTicket);
			
			if (ticket == null) {
				throw new RemoteException("Erreur: le ticket '" + idTicket + "' n'existe pas");
			}
			
			if (ticket.getEtat() != Etat.ASSIGNED) {
				throw new RemoteException("Ce ticket ne peut pas être résolu");
			}
			
			if (!nomDemandeur.equals(ticket.getAgentId())) {
				throw new RemoteException("Vous ne pouvez pas résoudre un ticket qui n'est pas le votre");
			}
			
			ticket.setDateResolution(LocalDateTime.now());
			ticket.setEtat(Etat.RESOLVED);
			
			incidentDao.save(ticket);
			System.out.println("INC >> Ticket : " + idTicket + "résolut par " + ticket.getAgentId());
		} else {
			throw new RemoteException("Session invalide ou expiré");
		}
	}
}
	

