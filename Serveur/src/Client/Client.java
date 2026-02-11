package Client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import interfaceRMI.Auth;

public class Client {

	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException{
	
		Auth auth = (Auth) Naming.lookup("rmi://localhost:1099/test");

		Scanner sc = new Scanner(System.in);
		
		System.out.print("Entrez votre login : ");
        String login = sc.nextLine();
        
        System.out.print("Entrez votre mot de passe : ");
        String password = sc.nextLine();
        
        try {
        	boolean res = auth.authentification(login, password);
        	
        	if(res) {
        		System.out.println("SUCESS");
        	}else {
        		System.out.println("ECHEC");
        	}
        } catch (Exception e) {
        	System.err.println("Erreur : " + e.getMessage());
        	e.printStackTrace();
        }
	}
}
