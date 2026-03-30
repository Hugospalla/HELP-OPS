package Serveur;

import Serveur.dao.IIncidentDao;
import Serveur.supervision.SupervisionManager;
import commons.interfaces.IAuthService;
import commons.interfaces.IIncidentService;
import commons.modele.Categorie;
import commons.modele.Etat;
import commons.modele.Incident;
import commons.modele.Role;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IncidentImpl extends UnicastRemoteObject implements IIncidentService{

	private IIncidentDao incidentDao;
	private SupervisionManager supervisionManager;
	
	private int consultationsEnCours = 0;
	private boolean modificationTicketEnCours = false;
	private int modificationsTicketEnAttente = 0;
	
	private synchronized void debuterConsultationTickets() throws InterruptedException {
		while (modificationTicketEnCours || modificationsTicketEnAttente > 0) {
			wait();
		}
		consultationsEnCours++;
	}
	
	private synchronized void terminerConsultationTickets() {
		consultationsEnCours--;
		if (consultationsEnCours ==0) {
			notifyAll();
		}
	}
	
	private synchronized void debuterModificationTicket() throws InterruptedException {
		modificationsTicketEnAttente++;
		while (modificationTicketEnCours || consultationsEnCours > 0) {
			wait();
		}
		modificationsTicketEnAttente--;
		modificationTicketEnCours = true;
	}
	
	private synchronized void terminerModificationTicket() {
		modificationTicketEnCours = false;
		notifyAll();
	}
	
	public IncidentImpl(IIncidentDao incidentDao, SupervisionManager supervisionManager) throws RemoteException {
		super();
		this.incidentDao = incidentDao;
		this.supervisionManager = supervisionManager;
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
			
			try {
				debuterModificationTicket();
				incidentDao.save(ticket);
				supervisionManager.publierEvenement("[NOUVEAU] Ticket '" + titre + "' créer par " + ticketAuteur + " (Etat: OPEN)");
			}catch (InterruptedException e) {
				throw new RemoteException("Création interrompue par le système", e);
			} finally {
				terminerModificationTicket();
			}
			
			System.out.println("INC >> Creation d'un ticket [" + ticketId.substring(0,8) + "] pour : " + ticketAuteur);
			return ticket;
		} else {
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
			
			List<Incident> res ;
			
			try {
				debuterConsultationTickets();
				res = incidentDao.getIncidentsByAuteur(demandeur);
			} catch (InterruptedException e) {
				throw new RemoteException("Consultation interrompue par le système", e);
			} finally {
				terminerConsultationTickets();
			}
			
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
			
			List<Incident> tous ;
			
			try {
				debuterConsultationTickets();
				tous = incidentDao.getAllIncidents();
			} catch (InterruptedException e) {
				throw new RemoteException("Consultation interrompue par le système", e);
			} finally {
				terminerConsultationTickets();
			}
			
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
			
			List<Incident> tous;
			
			try {
				debuterConsultationTickets();
				tous = incidentDao.getAllIncidents();
			} catch (InterruptedException e) {
				throw new RemoteException("Consultation interrompue par le système", e);
			} finally {
				terminerConsultationTickets();
			}
			
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
	public void prendreEnChargeTicket(String token, String idTicket) throws RemoteException{
		
		IAuthService auth = getAuth();
		if (auth == null) throw new RemoteException("Service d'authentification indisponible");
		
		if (auth.isTokenValid(token)) {
			Role roleDemandeur = auth.getRoleByToken(token);
			
			if (roleDemandeur != Role.AGENT) {
				throw new RemoteException("Accès refusé : Seul un agent a le droit d'afficher tous les tickets");
			}
			
			try {
				debuterModificationTicket();
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
				supervisionManager.publierEvenement("[ASSIGNATION] Ticket '" + ticket.getTitre() + "' pris en charge par " + auth.getLoginByToken(token));
				System.out.println("INC >> Ticket : " + idTicket + "assigné à " + ticket.getAgentId());
			} catch (InterruptedException e) {
				throw new RemoteException("Modification interrompue par le système", e);
			} finally {
				terminerModificationTicket();
			}
			
			
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
			
			try {
				debuterModificationTicket();
				
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
				supervisionManager.publierEvenement("[RESOLUTION] Ticket '" + ticket.getTitre() + "' clôturé par " + auth.getLoginByToken(token));
				System.out.println("INC >> Ticket : " + idTicket + "résolut par " + ticket.getAgentId());
			} catch (InterruptedException e) {
				throw new RemoteException("Modification interrompue par le système", e);
			} finally {
				terminerModificationTicket();
			}
			
			
		} else {
			throw new RemoteException("Session invalide ou expiré");
		}
	}
	@Override
	public commons.modele.Statistiques obtenirStatistiques(String token) throws RemoteException {
		IAuthService auth = getAuth();
		if (auth == null) throw new RemoteException("Service d'authentification indisponible");
		
		if (auth.isTokenValid(token)) {
			Role roleDemandeur = auth.getRoleByToken(token);
			
			if (roleDemandeur != Role.AGENT) {
				throw new RemoteException("Accès refusé : Seul un agent a le droit d'afficher les statistiques");
			}
			
			try {
				debuterConsultationTickets();
				return incidentDao.getStatistiques();
			} catch (InterruptedException e) {
				throw new RemoteException("Consultation interrompue par le système", e);
			} finally {
				terminerConsultationTickets();
			}
			
			
			
		} else {
			throw new RemoteException("Session invalide ou expirée");
		}
	}
}
	

