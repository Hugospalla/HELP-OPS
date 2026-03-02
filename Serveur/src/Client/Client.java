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
import commons.modele.Role;

public class Client {
    
    private static String monToken = null;
    private static String monLogin = null;
    private static Role monRole = null;

    public static void main(String[] args) {
        try {
            
            IAuthService authService = (IAuthService) Naming.lookup("rmi://localhost:1099/AuthService");
            IIncidentService incidentService = (IIncidentService) Naming.lookup("rmi://localhost:1099/IncidentService");
            
            Scanner sc = new Scanner(System.in);
            
            System.out.println("===============================");
            System.out.println("   BIENVENUE SUR HELP'OPS   ");
            System.out.println("===============================");
            
            
            while (monToken == null) {
                System.out.print("Entrez votre login : ");
                String login = sc.nextLine();
            
                System.out.print("Entrez votre mot de passe : ");
                String password = sc.nextLine();
            
                try {
                    
                    AuthResponse reponseAuth = authService.seConnecter(login, password);
                    monToken = reponseAuth.getToken();
                    monLogin = reponseAuth.getLogin();
                    monRole = reponseAuth.getRole();
                    System.out.println("\n>>> Connexion réussie ! Bonjour " + monLogin + " <<<");
                } catch (RemoteException e) {
                    System.out.println(">>> Erreur : " + e.getMessage() + "\n");
                }
            }
            
            
            boolean continuer = true;
            while (continuer) {
                if (monRole == Role.UTILISATEUR) {
                    System.out.println("\n--- MENU PRINCIPAL ---");
                    System.out.println("1. Créer un nouveau ticket");
                    System.out.println("2. Consulter mes tickets");
                    System.out.println("3. Quitter");
                    System.out.print("Votre choix : ");
                } else if (monRole == Role.AGENT) {
                    System.out.println("\n--- MENU AGENT ---");
                    System.out.println("1. Consulter tous les tickets");
                    System.out.println("2. Quitter");
                    System.out.print("Votre choix : "); // Ajout pour l'agent
                }
                
                int res = -1;
                try {
                    res = Integer.parseInt(sc.nextLine()); 
                } catch (NumberFormatException e) {
                    System.out.println(">> Erreur : Veuillez entrer un chiffre valide !");
                    continue; 
                }
                
                switch(res) {
                    case 1:
                        if (monRole == Role.UTILISATEUR) {
                            System.out.println("\n[ CRÉATION DE TICKET ]");
                            System.out.print("Titre du problème : ");
                            String titre = sc.nextLine();
                        
                            Categorie[] lesCategories = Categorie.values();
                            Categorie categorieChoix = null; 
                        
                            while (categorieChoix == null) {
                                System.out.println("\nCatégories disponibles :");
                                for (int i = 0; i < lesCategories.length; i++) {
                                    System.out.println(i + ". " + lesCategories[i]);
                                }
                            
                                System.out.print("Numéro de la catégorie : ");
                                try {
                                    int choix = Integer.parseInt(sc.nextLine()); 
                                
                                    if (choix >= 0 && choix < lesCategories.length) {
                                        categorieChoix = lesCategories[choix]; 
                                    } else {
                                        System.out.println(">> Erreur : Numéro hors limite. Veuillez choisir un numéro de la liste.");
                                    }
                                
                                } catch (NumberFormatException e) {
                                
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
                        } 
                        // --- CODE COMPLÉTÉ POUR L'AGENT ---
                        else if (monRole == Role.AGENT) {
                            System.out.println("\n[ LISTE DE TOUS LES TICKETS ]");
                            try {
                                List<Incident> tousLesTickets = incidentService.getToutLesIncidents(monToken);
                                
                                if (tousLesTickets == null || tousLesTickets.isEmpty()) {
                                    System.out.println(">> Aucun ticket dans le système.");
                                } else {
                                    for (Incident t : tousLesTickets) {
                                        System.out.println(t.toString());
                                    }
                                }
                            } catch (RemoteException e) {
                                System.out.println(">> Erreur : " + e.getMessage());
                            }
                        }
                        // Le break doit être ici pour fermer le case 1 complètement !
                        break; 

                    case 2:
                        if (monRole == Role.UTILISATEUR) {
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
                        } 
                        // L'agent veut quitter (Choix 2 du menu Agent)
                        else if (monRole == Role.AGENT) {
                            System.out.println("Déconnexion... Au revoir " + monLogin + " !");
                            continuer = false;
                        }
                        break;
                        
                    case 3:
                        // L'utilisateur veut quitter (Choix 3 du menu Utilisateur)
                        if (monRole == Role.UTILISATEUR) {
                            System.out.println("Déconnexion... Au revoir " + monLogin + " !");
                            continuer = false;
                        } else {
                            System.out.println("Choix invalide.");
                        }
                        break;
                        
                    default:
                        System.out.println("Choix invalide. Veuillez sélectionner une option du menu.");
                }
            }
            sc.close();
            
        } catch (Exception e) {
            System.err.println("ERREUR CRITIQUE DE CONNEXION AU SERVEUR : " + e.getMessage());
            e.printStackTrace(); 
        }
    }      
}