package commons.modele;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Incident implements Serializable{

	private String id;
	private Categorie categorie;
	private String titre;
	private String desc;
	private Etat etat;
	private String agentId;
	private String auteur;
	private LocalDateTime dateCreation;
	private LocalDateTime dateAssignation;
	private LocalDateTime dateResolution;
	private String messageResolution;
	private String messageSuivi;
	
	public Incident(String id, Categorie categorie, String titre, String desc, String auteur) {
		this.id = id;
		this.categorie = categorie;
		this.titre = titre;
		this.desc = desc;
		this.auteur = auteur;
		this.etat = Etat.OPEN;
		this.agentId = null;
		this.dateAssignation = null;
		this.dateCreation = LocalDateTime.now();
		this.dateResolution = null;
		this.messageResolution = null;
		this.messageSuivi = null;
	}
	
	public String getId() {
		return id;
	}
	
	public Categorie getCategorie() {
		return categorie;
	}
	
	public String getTitre() {
		return titre;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public Etat getEtat() {
		return etat;
	}
	
	public void setEtat(Etat etat) {
		this.etat = etat;
	}
		
	public String getAuteur() {
		return auteur;
	}
	
	public LocalDateTime getDateCreation() {
		return dateCreation;
	}
	
	public String getAgentId() {
		return agentId;
	}
	
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	
	public LocalDateTime getDateAssignation() {
		return dateAssignation;
	}
	
	public void setDateAssignation(LocalDateTime dateAssignation) {
		this.dateAssignation = dateAssignation;
	}
	
	public LocalDateTime getDateResolution() {
		return dateResolution;
	}
	
	public void setDateResolution(LocalDateTime dateResolution) {
		this.dateResolution = dateResolution;
	}
	
	public String getMessageResolution() {
		return messageResolution;
	}
	
	public void setMessageResolution(String messageResolution) {
		this.messageResolution = messageResolution;
	}
	
	public String getMessageSuivi() {
		return messageSuivi;
	}
	
	public void setMessageSuivi(String messageSuivi) {
		this.messageSuivi = messageSuivi;
	}
	
	public void setDateCreation(LocalDateTime dateCreation) {
		this.dateCreation = dateCreation;
	}
	
	@Override
    public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm ");
		String dateFormatee = this.dateCreation.format(formatter);
		
        String chaine =  "[" + this.id.substring(0, 8) + "...] " + " | Date création: " + dateFormatee + " | Titre: "  + this.titre + " | Cat: " + this.categorie + " | Etat: " + this.etat +  " | Desc: " + this.desc + " | Auteur: " + this.auteur;
        
        if (this.agentId != null && !this.agentId.isEmpty() && !this.agentId.equals("null")) {
			chaine += (" | Admin en charge : ") + this.agentId;
		}
        
        if (this.etat == Etat.RESOLVED) {
			if (this.dateResolution != null) {
				String dateResoFormatee = this.dateResolution.format(formatter);
				chaine += "\n - CLÔTURÉ LE : " + dateResoFormatee;
			}
			if (this.messageResolution != null && !this.messageResolution.isEmpty()) {
				chaine += "\n - MESSAGE RÉSOLUTION : " + this.messageResolution;
			}
		}
        
        if (this.etat != Etat.RESOLVED && this.messageSuivi != null && !this.messageSuivi.isEmpty()) {
        	chaine += "\n - Message de suivi : " + this.messageSuivi;
        }
        
       
        
        return chaine;
    }
}
