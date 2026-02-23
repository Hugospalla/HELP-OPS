package Client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

import commons.interfaces.IAuthService;
import commons.interfaces.IIncidentService;
import commons.modele.AuthResponse;
import commons.modele.Categorie;
import commons.modele.Incident;

public class Client {
    
    private static String monToken = null;
    private static String monLogin = null;

    public static void main(String[] args) {
        try {
            // 1. Connexion aux services RMI
            IAuthService authService = (IAuthService) Naming.lookup("rmi://localhost:1099/AuthService");
            IIncidentService incidentService = (IIncidentService) Naming.lookup("rmi://localhost:1099/IncidentService");
            
            Scanner sc = new Scanner(System.in);
            
            System.out.println("===============================");
            System.out.println("   BIENVENUE SUR HELP'OPS   ");
            System.out.println("===============================");
            
            // --- 2. BOUCLE D'AUTHENTIFICATION ---
            while (monToken == null) {
                System.out.print("Entrez votre login : ");
                String login = sc.nextLine();
            
                System.out.print("Entrez votre mot de passe : ");
                String password = sc.nextLine();
            
                try {
                    // Si ça rate, le serveur jette une exception qu'on attrape plus bas
                    AuthResponse reponseAuth = authService.seConnecter(login, password);
                    monToken = reponseAuth.getToken();
                    monLogin = reponseAuth.getLogin();
                    System.out.println("\n>>> Connexion réussie ! Bonjour " + monLogin + " <<<");
                } catch (RemoteException e) {
                    System.out.println(">>> Erreur : " + e.getMessage() + "\n");
                }
            }
            
            // --- 3. BOUCLE DU MENU PRINCIPAL ---
            boolean continuer = true;
            while (continuer) {
                System.out.println("\n--- MENU PRINCIPAL ---");
                System.out.println("1. Créer un nouveau ticket");
                System.out.println("2. Consulter mes tickets");
                System.out.println("3. Quitter");
                System.out.print("Votre choix : ");
                
                int res = -1;
                try {
                    // Lecture 100% sécurisée : on lit le texte et on le transforme en chiffre
                    res = Integer.parseInt(sc.nextLine()); 
                } catch (NumberFormatException e) {
                    System.out.println(">> Erreur : Veuillez entrer un chiffre valide !");
                    continue; // Annule ce tour et réaffiche le menu
                }
                
                switch(res) {
                    case 1:
                        System.out.println("\n[ CRÉATION DE TICKET ]");
                        System.out.print("Titre du problème : ");
                        String titre = sc.nextLine();
                        
                        Categorie[] lesCategories = Categorie.values();
                        Categorie categorieChoix = null; // Reste null tant que le choix n'est pas bon
                        
                        while (categorieChoix == null) {
                            System.out.println("\nCatégories disponibles :");
                            for (int i = 0; i < lesCategories.length; i++) {
                                System.out.println(i + ". " + lesCategories[i]);
                            }
                            
                            System.out.print("Numéro de la catégorie : ");
                            try {
                                int choix = Integer.parseInt(sc.nextLine()); 
                                
                                // On vérifie si le chiffre correspond bien à une catégorie existante
                                if (choix >= 0 && choix < lesCategories.length) {
                                    categorieChoix = lesCategories[choix]; // Choix valide, ça cassera la boucle !
                                } else {
                                    System.out.println(">> Erreur : Numéro hors limite. Veuillez choisir un numéro de la liste.");
                                }
                                
                            } catch (NumberFormatException e) {
                                // S'il tape "caca" ou du texte
                                System.out.println(">> Erreur : Veuillez entrer uniquement un chiffre.");
                            }
                        }
                        
                        System.out.print("Description détaillée : ");
                        String desc = sc.nextLine();
                    
                        try {
                            Incident nvTicket = incidentService.creerIncident(monToken, categorieChoix, titre, desc);
                            System.out.println(">> Création réussie ! Identifiant du ticket : " + nvTicket.getId());
                        } catch (RemoteException e) {
                            System.out.println(">> Erreur : " + e.getMessage());
                        }
                        break;
                        
                    case 2:
                        System.out.println("\n[ MES TICKETS ]");
                        try {
                            List<Incident> maListe = incidentService.getMesIncidents(monToken);
                            
                            if (maListe == null || maListe.isEmpty()) {
                                System.out.println(">> Vous n'avez aucun ticket en cours.");
                            } else {
                                System.out.println(">> Vous avez " + maListe.size() + " ticket(s) :");
                                for (Incident t : maListe) {
                                    System.out.println(t.toString());
                                }
                            }
                        } catch (RemoteException e) {
                            System.out.println(">> Erreur : " + e.getMessage());
                        }
                        break;
                        
                    case 3:
                        System.out.println("Déconnexion... Au revoir " + monLogin + " !");
                        continuer = false;
                        break;
                        
                    default:
                        System.out.println("Choix invalide. Veuillez sélectionner 1, 2 ou 3.");
                }
            }
            sc.close();
            
        } catch (Exception e) {
            System.err.println("ERREUR CRITIQUE DE CONNEXION AU SERVEUR : " + e.getMessage());
            e.printStackTrace(); // Affiche les détails techniques si le serveur est vraiment éteint
        }
    }      
}