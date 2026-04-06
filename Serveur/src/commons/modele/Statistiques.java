package commons.modele;

import java.io.Serializable;
import java.util.Map;

public class Statistiques implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Statistiques globales
    private int totalTickets;
    private int ticketsResolus;
    private double moyenneTicketsParJour;
    private double moyenneTicketsParSemaine;
    private double tempsMoyenResolutionMinutes;
    private Map<String, Integer> ticketsParEtat;
    
    // Statistiques par agent
    public static class AgentStats implements Serializable {
        private static final long serialVersionUID = 1L;
        public int ticketsAssignes;
        public int ticketsResolus;
        public double tempsMoyenMin;
        public double pourcentageCharge; // Part du travail de l'agent par rapport à l'équipe
        public double tauxResolution; // Efficacité de l'agent (%)
        public double pressionJour;
    }
    
    private Map<String, AgentStats> statsParAgent;

    // Getters et Setters
    public void setTotalTickets(int totalTickets) { this.totalTickets = totalTickets; }
    public void setTicketsResolus(int ticketsResolus) { this.ticketsResolus = ticketsResolus; }
    public void setMoyenneTicketsParJour(double moyenneTicketsParJour) { this.moyenneTicketsParJour = moyenneTicketsParJour; }
    public void setMoyenneTicketsParSemaine(double moyenneTicketsParSemaine) { this.moyenneTicketsParSemaine = moyenneTicketsParSemaine; }
    public void setTempsMoyenResolutionMinutes(double tempsMoyenResolutionMinutes) { this.tempsMoyenResolutionMinutes = tempsMoyenResolutionMinutes; }
    public void setTicketsParEtat(Map<String, Integer> ticketsParEtat) { this.ticketsParEtat = ticketsParEtat; }
    public void setStatsParAgent(Map<String, AgentStats> statsParAgent) { this.statsParAgent = statsParAgent; }

    // Affichage formaté pour le terminal
    public String afficherBilan() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=================================================================\n");
        sb.append("         TABLEAU DE BORD KPI - HELP'OPS (V-PRO)\n");
        sb.append("=================================================================\n");
        
        sb.append(" [ VUE GLOBALE ]\n");
        sb.append(String.format(" - Total des tickets signalés  : %d\n", totalTickets));
        double pourcentResolu = totalTickets > 0 ? ((double)ticketsResolus / totalTickets) * 100 : 0;
        sb.append(String.format(" - Tickets clôturés (RESOLVED) : %d (%.1f%% du total)\n", ticketsResolus, pourcentResolu));
        sb.append(String.format(" - Volume moyen d'activité     : %.1f tickets/jour | %.1f tickets/semaine\n", moyenneTicketsParJour, moyenneTicketsParSemaine));
        
        sb.append("\n [ RÉPARTITION PAR ÉTAT ]\n");
        for (Map.Entry<String, Integer> entry : ticketsParEtat.entrySet()) {
            sb.append(String.format("   > %-10s : %d\n", entry.getKey(), entry.getValue()));
        }
        
        sb.append("\n [ PERFORMANCES DE RÉSOLUTION ]\n");
        sb.append(String.format(" - Temps moyen de résolution global (OPEN->RESOLVED) : %.1f minutes\n", tempsMoyenResolutionMinutes));
        
        sb.append("\n [ ANALYSE DE LA CHARGE DE TRAVAIL PAR AGENT ]\n");
        if (statsParAgent == null || statsParAgent.isEmpty()) {
            sb.append("   > Aucun agent n'a encore pris de ticket en charge.\n");
        } else {
            for (Map.Entry<String, AgentStats> entry : statsParAgent.entrySet()) {
                String agent = entry.getKey();
                AgentStats st = entry.getValue();
                sb.append(String.format("   > Agent '%s' :\n", agent.toUpperCase()));
                sb.append(String.format("      * Tickets pris en charge : %d (%.1f%% de la charge globale d'équipe)\n", st.ticketsAssignes, st.pourcentageCharge));
                sb.append(String.format("      * Taux de résolution     : %.1f%% (%d tickets clôturés)\n", st.tauxResolution, st.ticketsResolus));
                if (st.ticketsResolus > 0) {
                    sb.append(String.format("      * Temps moyen résolution : %.1f minutes / ticket\n", st.tempsMoyenMin));
                } else {
                    sb.append("      * Temps moyen résolution : N/A (Aucun ticket clôturé)\n");
                }
                sb.append(String.format("      * Taux de pression       : %.1f tickets / jour\n", st.pressionJour));
                sb.append("\n");
            }
        }
        sb.append("=================================================================\n");
        return sb.toString();
    }
}