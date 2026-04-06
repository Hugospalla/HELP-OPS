package Client;

import commons.interfaces.IAuthService;
import commons.interfaces.IIncidentService;
import commons.modele.AuthResponse;
import commons.modele.Categorie;
import commons.modele.Incident;
import commons.modele.Role;
import commons.modele.Statistiques;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

public class Client {
    
    private static String monToken = null;
    private static String monLogin = null;
    private static Role monRole = null;
    private static List<Incident> dernieresRecherches = null; 
    
    private static IAuthService authService;
    private static IIncidentService incidentService;
    private static Scanner sc;

    public static void main(String[] args) {
        initialiserConnexion();
        
        System.out.println("===============================");
        System.out.println("   BIENVENUE SUR HELP'OPS   ");
        System.out.println("===============================");
        
        authentifier();
        lancerMenuPrincipal();
        
        if (sc != null) sc.close();
    }

    private static void initialiserConnexion() {
        sc = new Scanner(System.in);
        boolean serveursPrets = false;
        
        System.out.println(">> Connexion à l'infrastructure HELP'OPS en cours...");
        
        while (!serveursPrets) {
            try {
                authService = (IAuthService) Naming.lookup("rmi://localhost:1099/AuthService");
                incidentService = (IIncidentService) Naming.lookup("rmi://localhost:1099/IncidentService");
                serveursPrets = true; 
            } catch (Exception e) {
                System.out.println(">> En attente du démarrage des serveurs (Auth et Incidents)... (Nouvel essai dans 3s)");
                try { Thread.sleep(3000); } catch (InterruptedException ie) {}
            }
        }
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
            System.out.println("1. Lister les tickets en attente (OPEN) pour prise en charge");
            System.out.println("2. Voir MES tickets en cours (ASSIGNED) pour résolution");
            System.out.println("3. Voir les Statistiques de la plateforme");
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
            case 2: actionVoirMesTicketsAssignes(); return true;
            case 3: actionVoirStatistiques(); return true; 
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
                return;
            }

            boolean resterDansVue = true;
            while (resterDansVue) {
                System.out.println("\n>> Vous avez " + dernieresRecherches.size() + " ticket(s) :");
                
                for (int i = 0; i < dernieresRecherches.size(); i++) {
                    Incident t = dernieresRecherches.get(i);
                    System.out.println(i + " -> [" + t.getEtat() + "] " + t.getTitre() + " (ID: " + t.getId().substring(0, 8) + " | Cat: " + t.getCategorie() + ")");
                }
                
                System.out.print("\nEntrez le NUMÉRO du ticket pour voir les détails (ou 'q' pour retourner au menu) : ");
                String input = sc.nextLine().trim();
                
                if (input.equalsIgnoreCase("q")) {
                    resterDansVue = false;
                } else if (!input.isEmpty()) {
                    try {
                        int choixNum = Integer.parseInt(input);
                        if (choixNum >= 0 && choixNum < dernieresRecherches.size()) {
                            System.out.println("\n--- DÉTAILS DU TICKET ---");
                            System.out.println(dernieresRecherches.get(choixNum).toString());
                            System.out.println("-------------------------");
                            System.out.print("\nAppuyez sur ENTRÉE pour revenir à la liste...");
                            sc.nextLine();
                        } else {
                            System.out.println(">> Erreur : Numéro hors limite.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(">> Erreur : Veuillez entrer un chiffre valide ou 'q'.");
                    }
                }
            }
        } catch (RemoteException e) {
            System.out.println(">> Erreur : " + e.getMessage());
        }
    }

    private static void actionVoirStatistiques() {
        try {
            Statistiques stats = incidentService.obtenirStatistiques(monToken);
            if (stats != null) {
                System.out.println(stats.afficherBilan());
            } else {
                System.out.println(">> Impossible de récupérer les statistiques.");
            }
        } catch (RemoteException e) {
            System.out.println(">> Erreur serveur : " + e.getMessage());
        }
    }

    private static void actionListerTicketsOpen() {
        System.out.println("\n[ LISTE DES TICKETS EN ATTENTE (OPEN) ]");
        try {
            dernieresRecherches = incidentService.getIncidentsOpen(monToken);
            
            if (dernieresRecherches == null || dernieresRecherches.isEmpty()) {
                System.out.println(">> Aucun ticket en attente. Beau travail !");
                return;
            }

            boolean resterDansVue = true;
            while (resterDansVue) {
                System.out.println("\n>> Il y a " + dernieresRecherches.size() + " ticket(s) en attente :");
                
                for (int i = 0; i < dernieresRecherches.size(); i++) {
                    Incident t = dernieresRecherches.get(i);
                    System.out.println(i + " -> [" + t.getEtat() + "] " + t.getTitre() + " (ID: " + t.getId().substring(0, 8) + " | Par: " + t.getAuteur() + ")");
                }
                
                System.out.print("\nEntrez le NUMÉRO du ticket pour voir les détails (ou 'q' pour retourner au menu) : ");
                String input = sc.nextLine().trim();
                
                if (input.equalsIgnoreCase("q")) {
                    resterDansVue = false;
                } else if (!input.isEmpty()) {
                    try {
                        int choixNum = Integer.parseInt(input);
                        if (choixNum >= 0 && choixNum < dernieresRecherches.size()) {
                            Incident ticketVise = dernieresRecherches.get(choixNum);
                            System.out.println("\n--- DÉTAILS DU TICKET ---");
                            System.out.println(ticketVise.toString());
                            System.out.println("-------------------------");
                            
                            System.out.print("\n1. Prendre en charge ce ticket\n2. Revenir à la liste\nVotre choix : ");
                            String action = sc.nextLine().trim();
                            
                            if (action.equals("1")) {
                                incidentService.prendreEnChargeTicket(monToken, ticketVise.getId());
                                System.out.println(">> Succès ! Vous avez été assigné au ticket de manière exclusive.");
                                resterDansVue = false;
                            }
                        } else {
                            System.out.println(">> Erreur : Numéro hors limite.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(">> Erreur : Veuillez entrer un chiffre valide ou 'q'.");
                    } catch (RemoteException e) {
                        System.out.println(">> Erreur serveur : " + e.getMessage());
                    }
                }
            }
        } catch (RemoteException e) {
            System.out.println(">> Erreur : " + e.getMessage());
        }
    }

    private static void actionVoirMesTicketsAssignes() {
        System.out.println("\n[ MES TICKETS EN COURS (ASSIGNED) ]");
        try {
            dernieresRecherches = incidentService.getMesIncidentsAssigned(monToken);
            
            if (dernieresRecherches == null || dernieresRecherches.isEmpty()) {
                System.out.println(">> Vous n'avez aucun ticket en charge pour le moment.");
                return;
            }

            boolean resterDansVue = true;
            while (resterDansVue) {
                System.out.println("\n>> Vous traitez actuellement " + dernieresRecherches.size() + " ticket(s) :");
                
                for (int i = 0; i < dernieresRecherches.size(); i++) {
                    Incident t = dernieresRecherches.get(i);
                    System.out.println(i + " -> [" + t.getEtat() + "] " + t.getTitre() + " (ID: " + t.getId().substring(0, 8) + " | Par: " + t.getAuteur() + ")");
                }
                
                System.out.print("\nEntrez le NUMÉRO du ticket pour voir les détails (ou 'q' pour retourner au menu) : ");
                String input = sc.nextLine().trim();
                
                if (input.equalsIgnoreCase("q")) {
                    resterDansVue = false;
                } else if (!input.isEmpty()) {
                    try {
                        int choixNum = Integer.parseInt(input);
                        if (choixNum >= 0 && choixNum < dernieresRecherches.size()) {
                            Incident ticketVise = dernieresRecherches.get(choixNum);
                            System.out.println("\n--- DÉTAILS DU TICKET ---");
                            System.out.println(ticketVise.toString());
                            System.out.println("-------------------------");
                            
                            System.out.print("\n1. Résoudre ce ticket\n2. Ajouter un message de suivi\n3. Réassigner ce ticket à un collègue\n4. Revenir à la liste\nVotre choix : ");
                            String action = sc.nextLine().trim();
                            
                            if (action.equals("1")) {
                                System.out.print("Saisissez votre message de résolution : ");
                                String msgResolution = sc.nextLine().trim();
                                
                                incidentService.cloturerTicket(monToken, ticketVise.getId(), msgResolution);
                                
                                System.out.println(">> Succès ! Vous avez résolu ce ticket.");
                                resterDansVue = false;
                            } else if (action.equals("2")) {
                                System.out.print("Saisissez votre message de suivi : ");
                                String msgSuivi = sc.nextLine().trim();
                                
                                incidentService.ajouterMessageSuivi(monToken, ticketVise.getId(), msgSuivi);
                                
                                System.out.println(">> Succès ! Message de suivi ajouté.");
                            } else if (action.equals("3")) {
                                // NOUVELLE OPTION DE TRANSFERT
                                System.out.print("Saisissez le login du nouvel agent (ex: admin2) : ");
                                String nouvelAgent = sc.nextLine().trim();
                                
                                if (!nouvelAgent.isEmpty()) {
                                    incidentService.reassignerTicket(monToken, ticketVise.getId(), nouvelAgent);
                                    System.out.println(">> Succès ! Le ticket a été transféré à " + nouvelAgent + ".");
                                    resterDansVue = false; // Le ticket n'est plus à nous, on recharge la vue
                                } else {
                                    System.out.println(">> Erreur : Le login ne peut pas être vide.");
                                }
                            } else if (action.equals("4")) {
                                resterDansVue = false;
                            } else {
                                System.out.println(">> Erreur : Choix invalide.");
                            }
                        } else {
                            System.out.println(">> Erreur : Numéro hors limite.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(">> Erreur : Veuillez entrer un chiffre valide ou 'q'.");
                    } catch (RemoteException e) {
                        System.out.println(">> Erreur serveur : " + e.getMessage());
                    }
                }
            }
        } catch (RemoteException e) {
            System.out.println(">> Erreur : " + e.getMessage());
        }
    }
    
}