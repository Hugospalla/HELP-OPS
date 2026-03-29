// Fichier : Serveur/src/commons/modele/Statistiques.java
package commons.modele;

import java.io.Serializable;
import java.util.Map;

public class Statistiques implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int totalTickets;
    private int ticketsResolus;
    private Map<String, Integer> ticketsParEtat;
    private double tempsMoyenResolutionMinutes;
    private Map<String, Integer> ticketsParAgent;
    private Map<String, Double> pressionParAgent;

    // Getters et Setters
    public void setTotalTickets(int totalTickets) { this.totalTickets = totalTickets; }
    public void setTicketsResolus(int ticketsResolus) { this.ticketsResolus = ticketsResolus; }
    public void setTicketsParEtat(Map<String, Integer> ticketsParEtat) { this.ticketsParEtat = ticketsParEtat; }
    public void setTempsMoyenResolutionMinutes(double tempsMoyenResolutionMinutes) { this.tempsMoyenResolutionMinutes = tempsMoyenResolutionMinutes; }
    public void setTicketsParAgent(Map<String, Integer> ticketsParAgent) { this.ticketsParAgent = ticketsParAgent; }
    public void setPressionParAgent(Map<String, Double> pressionParAgent) { this.pressionParAgent = pressionParAgent; }

    // Méthode pour afficher joliment le bilan dans le terminal du Client
    public String afficherBilan() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n================================================\n");
        sb.append("        STATISTIQUES GLOBALES HELP'OPS\n");
        sb.append("================================================\n");
        sb.append(String.format(" - Nombre total de tickets       : %d\n", totalTickets));
        sb.append(String.format(" - Nombre de tickets résolus     : %d\n", ticketsResolus));
        
        sb.append("\n [ TICKETS PAR ÉTAT ]\n");
        for (Map.Entry<String, Integer> entry : ticketsParEtat.entrySet()) {
            sb.append(String.format("   > %-10s : %d\n", entry.getKey(), entry.getValue()));
        }
        
        sb.append(String.format("\n - Temps moyen (OPEN->RESOLVED)  : %.2f minutes\n", tempsMoyenResolutionMinutes));
        
        sb.append("\n [ CHARGE DE TRAVAIL PAR AGENT ]\n");
        for (Map.Entry<String, Integer> entry : ticketsParAgent.entrySet()) {
            String agent = entry.getKey();
            double pression = pressionParAgent.getOrDefault(agent, 0.0);
            sb.append(String.format("   > Agent '%s' : %d ticket(s) traités | Taux de pression : %.2f tickets/jour\n", 
                    agent, entry.getValue(), pression));
        }
        sb.append("================================================\n");
        return sb.toString();
    }
}