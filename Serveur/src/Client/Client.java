package Client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

import Serveur.Incident;
import interfaceRMI.Auth;
import interfaceRMI.Incidents;

public class Client {
	
	private static String monToken = null;
	private static String monTicket = null;
	private static boolean estConnecte = false;
	private static boolean menu = false;

	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException{
	
		Auth auth = (Auth) Naming.lookup("rmi://localhost:1099/AuthService");
		
		Incidents incidents = (Incidents) Naming.lookup("rmi://localhost:1099/IncidentService");

		Scanner sc = new Scanner(System.in);
		
		while (estConnecte != true) {
			System.out.print("Entrez votre login : ");
			String login = sc.nextLine();
        
			System.out.print("Entrez votre mot de passe : ");
			String password = sc.nextLine();
		
        
        	try {
        		monToken = auth.authentification(login, password);
			
			
        		if (monToken != null) {
        			System.out.println("Connecté avec succès");
        			estConnecte = true;
        		//	System.out.println(monToken);
				
        			//boolean tokenValide = auth.vToken(monToken);
        			//System.out.println("Token user valide ? " + tokenValide);        	
        		} else {
        			System.out.println("Login ou mot de passe incorrect");
        		}
        		
        		
        		
        		if (estConnecte == true) {
        			
        			while (menu != true) {
        				System.out.println("1. Création de ticket");
        				System.out.println("2. Consulter ses ticket");
        				int res = sc.nextInt();
        				sc.nextLine();
        			
        			
        			
        				if(res == 1) {
        					
        					menu = true;
        					
        					System.out.print("Entrez la catégorie du ticket : ");
        					String categorie = sc.nextLine();
        				
        					System.out.print("Entrez le titre du ticket : ");
        					String titre = sc.nextLine();
        				
        					System.out.print("Entrez la description du ticket : ");
        					String desc = sc.nextLine();
        				
        					String etat = "OPEN";
        				
        					String auteur = auth.getLoginByToken(monToken);
        				
        					monTicket = incidents.creationInc(monToken, categorie, titre, desc, etat, auteur);
        				
        					if(monTicket == null) {
        						System.out.println("Erreur lors de la création de votre ticket !");
        					}else {
        						System.out.println("Création de votre ticket réussie - Information ticket :");
        						System.out.println("Titre : " +  titre);
        						System.out.println("Catégorie : " +  categorie);
        						System.out.println("Description : " +  desc);
        					
        					}
        					
        					menu = false;
        				}
        			
        				if(res == 2) {
        				
        					menu = true;
        					List<Incident> maListe  = incidents.mesIncidents(monToken);
        					
        					if (maListe == null) {
        						System.out.println("Aucun ticket Trouvé");
        					}else {
        						for (Incident t : maListe) {
        							System.out.println(" - " + t.toString());
        						}
        					}
        					menu = false;
        				
        				}
        			
        			}
        		}
        	} catch (RemoteException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
        }
	}      
}
