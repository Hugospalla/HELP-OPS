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
    private static List<Incident> dernieresRecherches = null; 
    
    private static IAuthService authService;
    private static IIncidentService incidentService;
    private static Scanner sc;

    public static void main(String[] args) {
        try {
            initialiserConnexion();
            
            System.out.println("===============================");
            System.out.println("   BIENVENUE SUR HELP'OPS   ");
            System.out.println("===============================");
            
            authentifier();
            lancerMenuPrincipal();
            
            sc.close();
            
        } catch (Exception e) {
            System.err.println("ERREUR CRITIQUE DE CONNEXION AU SERVEUR : " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    private static void initialiserConnexion() throws Exception {
        authService = (IAuthService) Naming.lookup("rmi://localhost:1099/AuthService");
        incidentService = (IIncidentService) Naming.lookup("rmi://localhost:1099/IncidentService");
        sc = new Scanner(System.in);
    }

    private static void authentifier() {
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
                System.out.println("\n>>> Connexion réussie ! Bonjour " + monLogin + " (Rôle: " + monRole + ") <<<");
            } catch (RemoteException e) {
                System.out.println(">>> Erreur : " + e.getMessage() + "\n");
            }
        }
    }

    private static void lancerMenuPrincipal() {
        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            
            int res = -1;
            try {
                res = Integer.parseInt(sc.nextLine()); 
            } catch (NumberFormatException e) {
                System.out.println(">> Erreur : Veuillez entrer un chiffre valide !");
                continue; 
            }
            
            if (monRole == Role.UTILISATEUR) {
                continuer = traiterChoixUtilisateur(res);
            } else if (monRole == Role.AGENT) {
                continuer = traiterChoixAgent(res);
            }
        }
    }

    private static void afficherMenu() {
        if (monRole == Role.UTILISATEUR) {
            System.out.println("\n--- MENU UTILISATEUR ---");
            System.out.println("1. Créer un nouveau ticket");
            System.out.println("2. Consulter mes tickets");
            System.out.println("3. Quitter");
        } else if (monRole == Role.AGENT) {
            System.out.println("\n--- MENU AGENT ---");
            System.out.println("1. Lister les tickets en attente (OPEN)");
            System.out.println("2. Prendre en charge un ticket");
            System.out.println("3. Voir MES tickets en cours (ASSIGNED)");
            System.out.println("4. Quitter");
        }
        System.out.print("Votre choix : ");
    }

    private static boolean traiterChoixUtilisateur(int res) {
        switch(res) {
            case 1: actionCreerTicket(); return true;
            case 2: actionConsulterMesTickets(); return true;
            case 3:
                System.out.println("Déconnexion... Au revoir " + monLogin + " !");
                return false;
            default:
                System.out.println("Choix invalide.");
                return true;
        }
    }

    private static boolean traiterChoixAgent(int res) {
        switch(res) {
            case 1: actionListerTicketsOpen(); return true;
            case 2: actionPrendreEnCharge(); return true;
            case 3: actionVoirMesTicketsAssignes(); return true;
            case 4:
                System.out.println("Déconnexion... Au revoir " + monLogin + " !");
                return false;
            default:
                System.out.println("Choix invalide.");
                return true;
        }
    }

    private static void actionCreerTicket() {
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
                    System.out.println(">> Erreur : Numéro hors limite.");
                }
            } catch (NumberFormatException e) {
                System.out.println(">> Erreur : Veuillez entrer un chiffre.");
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

    private static void actionConsulterMesTickets() {
        System.out.println("\n[ MES TICKETS ]");
        try {
            dernieresRecherches = incidentService.getMesIncidents(monToken);
            
            if (dernieresRecherches == null || dernieresRecherches.isEmpty()) {
                System.out.println(">> Vous n'avez aucun ticket en cours.");
            } else {
                System.out.println(">> Vous avez " + dernieresRecherches.size() + " ticket(s) :");
                
                for (int i = 0; i < dernieresRecherches.size(); i++) {
                    Incident t = dernieresRecherches.get(i);
                    System.out.println(i + " -> [" + t.getEtat() + "] " + t.getTitre());
                }
                
                System.out.print("\nEntrez le NUMÉRO du ticket pour voir les détails (ou 'q' pour annuler) : ");
                String input = sc.nextLine().trim();
                
                if (!input.equalsIgnoreCase("q") && !input.isEmpty()) {
                    try {
                        int choixNum = Integer.parseInt(input);
                        if (choixNum >= 0 && choixNum < dernieresRecherches.size()) {
                            System.out.println("\n--- DÉTAILS DU TICKET ---");
                            System.out.println(dernieresRecherches.get(choixNum).toString());
                            System.out.println("-------------------------");
                        } else {
                            System.out.println(">> Erreur : Numéro hors limite.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(">> Retour au menu principal.");
                    }
                } else {
                    System.out.println(">> Action annulée.");
                }
            }
        } catch (RemoteException e) {
            System.out.println(">> Erreur : " + e.getMessage());
        }
    }

    private static void actionListerTicketsOpen() {
        System.out.println("\n[ LISTE DES TICKETS EN ATTENTE (OPEN) ]");
        try {
            dernieresRecherches = incidentService.getIncidentsOpen(monToken);
            
            if (dernieresRecherches == null || dernieresRecherches.isEmpty()) {
                System.out.println(">> Aucun ticket en attente. Beau travail !");
            } else {
                for (int i = 0; i < dernieresRecherches.size(); i++) {
                    System.out.println(i + " -> " + dernieresRecherches.get(i).toString());
                }
            }
        } catch (RemoteException e) {
            System.out.println(">> Erreur : " + e.getMessage());
        }
    }

    private static void actionPrendreEnCharge() {
        System.out.println("\n[ PRENDRE EN CHARGE UN TICKET ]");
        
        if (dernieresRecherches == null || dernieresRecherches.isEmpty()) {
            System.out.println(">> Veuillez d'abord lister les tickets (Choix 1) pour voir les numéros.");
        } else {
            System.out.print("Entrez le NUMÉRO du ticket (ex: 0, 1...) : ");
            try {
                int choixNum = Integer.parseInt(sc.nextLine());
                
                if (choixNum >= 0 && choixNum < dernieresRecherches.size()) {
                    String idTicketVise = dernieresRecherches.get(choixNum).getId();
                    incidentService.prendreEnChargeTicket(monToken, idTicketVise);
                    System.out.println(">> Succès ! Vous avez été assigné au ticket de manière exclusive.");
                    dernieresRecherches = null; 
                } else {
                    System.out.println(">> Erreur : Ce numéro n'est pas dans la liste.");
                }
            } catch (NumberFormatException e) {
                System.out.println(">> Erreur : Veuillez entrer un chiffre valide.");
            } catch (RemoteException e) {
                System.out.println(">> Erreur serveur : " + e.getMessage());
            }
        }
    }

    private static void actionVoirMesTicketsAssignes() {
        System.out.println("\n[ MES TICKETS EN COURS (ASSIGNED) ]");
        try {
            dernieresRecherches = incidentService.getMesIncidentsAssigned(monToken);
            
            if (dernieresRecherches == null || dernieresRecherches.isEmpty()) {
                System.out.println(">> Vous n'avez aucun ticket en charge pour le moment.");
            } else {
                System.out.println(">> Vous traitez actuellement " + dernieresRecherches.size() + " ticket(s) :");
                
                for (int i = 0; i < dernieresRecherches.size(); i++) {
                    Incident t = dernieresRecherches.get(i);
                    System.out.println(i + " -> [" + t.getEtat() + "] " + t.getTitre());
                }
                
                System.out.print("\nEntrez le NUMÉRO du ticket pour voir les détails (ou 'q' pour annuler) : ");
                String input = sc.nextLine().trim();
                
                if (!input.equalsIgnoreCase("q") && !input.isEmpty()) {
                    try {
                        int choixNum = Integer.parseInt(input);
                        if (choixNum >= 0 && choixNum < dernieresRecherches.size()) {
                            System.out.println("\n--- DÉTAILS DU TICKET ---");
                            System.out.println(dernieresRecherches.get(choixNum).toString());
                            System.out.println("-------------------------");
                        } else {
                            System.out.println(">> Erreur : Numéro hors limite.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(">> Retour au menu principal.");
                    }
                } else {
                    System.out.println(">> Action annulée.");
                }
            }
        } catch (RemoteException e) {
            System.out.println(">> Erreur : " + e.getMessage());
        }
    }
}