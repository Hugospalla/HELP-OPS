package commons.modele;

import java.io.Serializable;


public class Incident implements Serializable{

	private String id;
	private Categorie categorie;
	private String titre;
	private String desc;
	private String etat;
	private String auteur;
	
	public Incident(String id, Categorie categorie, String titre, String desc, String auteur) {
		this.id = id;
		this.categorie = categorie;
		this.titre = titre;
		this.desc = desc;
		this.auteur = auteur;
		this.etat = "OPEN";
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
	
	@Override
    public String toString() {
        return "[" + this.id.substring(0, 8) + "...] " + this.titre + " | Cat: " + this.categorie + " | Etat: " + this.etat + " | Auteur: " + this.auteur;
    }
}
