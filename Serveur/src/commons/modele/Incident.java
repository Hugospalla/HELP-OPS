package commons.modele;

import java.io.Serializable;


public class Incident implements Serializable{

	private String categorie;
	private String titre;
	private String desc;
	private String etat;
	private String auteur;
	
	public Incident(String categorie, String titre, String desc, String etat, String auteur) {
		this.categorie = categorie;
		this.titre = titre;
		this.desc = desc;
		this.auteur = auteur;
		this.etat = "OPEN";
	}
	
	public String getCategorie() {
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
	
	@Override
	public String toString() {
		return "Ticket [" + this.titre + "] : " + this.categorie + " (" + this.etat + ") - Auteur: " + this.auteur;
	}
}
