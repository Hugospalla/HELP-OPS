package commons.modele;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Incident implements Serializable{

	private String id;
	private Categorie categorie;
	private String titre;
	private String desc;
	private String etat;
	private String auteur;
	private LocalDateTime dateCreation;
	
	public Incident(String id, Categorie categorie, String titre, String desc, String auteur) {
		this.id = id;
		this.categorie = categorie;
		this.titre = titre;
		this.desc = desc;
		this.auteur = auteur;
		this.etat = "OPEN";
		this.dateCreation = LocalDateTime.now();
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
	
	public String getEtat() {
		return etat;
	}
		
	public String getAuteur() {
		return auteur;
	}
	
	public LocalDateTime getDateCreation() {
		return dateCreation;
	}
	
	@Override
    public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm ");
		String dateFormatee = this.dateCreation.format(formatter);
		
        return "[" + this.id.substring(0, 8) + "...] " + " | Date création: " + dateFormatee + " | Titre: "  + this.titre + " | Cat: " + this.categorie + " | Etat: " + this.etat +  " | Desc: " + this.desc + " | Auteur: " + this.auteur;
    }
}
