package Client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import interfaceRMI.Auth;

public class Client {
	
	private static String monToken = null;
	private static boolean estConnecte = false;

	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException{
	
		Auth auth = (Auth) Naming.lookup("rmi://localhost:1099/AuthService");

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
        			System.out.println(monToken);
				
        			boolean tokenValide = auth.vToken(monToken);
        			System.out.println("Token user valide ? " + tokenValide);        	
        		} else {
        			System.out.println("Login ou mot de passe incorrect");
        		}
        	} catch (RemoteException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
        }
	}      
}
