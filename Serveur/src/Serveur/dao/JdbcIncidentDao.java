package Serveur.dao;

import commons.modele.Categorie;
import commons.modele.Etat;
import commons.modele.Incident;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcIncidentDao implements IIncidentDao {

    @Override
    public void save(Incident incident) {
        
        String query = "INSERT OR REPLACE INTO incidents (id, categorie, titre, description, etat, agent_id, auteur, date_creation, date_assignation, date_resolution, message_resolution) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, incident.getId());
            pstmt.setString(2, incident.getCategorie().name());
            pstmt.setString(3, incident.getTitre());
            pstmt.setString(4, incident.getDesc());
            pstmt.setString(5, incident.getEtat().name());
            pstmt.setString(6, incident.getAgentId());
            pstmt.setString(7, incident.getAuteur());
            pstmt.setString(8, incident.getDateCreation().toString());
            pstmt.setString(9, incident.getDateAssignation() != null ? incident.getDateAssignation().toString() : null);
            
            
            pstmt.setString(10, incident.getDateResolution() != null ? incident.getDateResolution().toString() : null);
            pstmt.setString(11, incident.getMessageResolution()  != null ? incident.getMessageResolution().toString() : null);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la sauvegarde de l'incident : " + e.getMessage());
        }
    }

    @Override
    public List<Incident> getIncidentsByAuteur(String auteur) {
        return executeSelectQuery("SELECT * FROM incidents WHERE auteur = ?", auteur);
    }

    @Override
    public Incident getIncidentsById(String id) {
        List<Incident> result = executeSelectQuery("SELECT * FROM incidents WHERE id = ?", id);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<Incident> getAllIncidents() {
        return executeSelectQuery("SELECT * FROM incidents", null);
    }

    private List<Incident> executeSelectQuery(String query, String param) {
        List<Incident> incidents = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            if (param != null) {
                pstmt.setString(1, param);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Incident incident = new Incident(
                        rs.getString("id"),
                        Categorie.valueOf(rs.getString("categorie")),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("auteur")
                    );
                    
                    incident.setEtat(Etat.valueOf(rs.getString("etat")));
                    incident.setAgentId(rs.getString("agent_id"));
                    
                    String dateAssignationStr = rs.getString("date_assignation");
                    if (dateAssignationStr != null && !dateAssignationStr.isEmpty() && !dateAssignationStr.equals("null")) {
                        incident.setDateAssignation(LocalDateTime.parse(dateAssignationStr));
                    }
                    
                    
                    String dateResolutionStr = rs.getString("date_resolution");
                    if (dateResolutionStr != null && !dateResolutionStr.isEmpty() && !dateResolutionStr.equals("null")) {
                        incident.setDateResolution(LocalDateTime.parse(dateResolutionStr));
                    } else {
                        incident.setDateResolution(null); 
                    }
                    
                    incident.setMessageResolution(rs.getString("message_resolution"));
                    
                    incidents.add(incident);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lecture incidents : " + e.getMessage());
        }
        return incidents;
    }
    @Override
    public commons.modele.Statistiques getStatistiques() {
        commons.modele.Statistiques stats = new commons.modele.Statistiques();
        
        try (Connection conn = DatabaseManager.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
             

             ResultSet rsTotal = stmt.executeQuery("SELECT COUNT(*) AS total FROM incidents");
             if (rsTotal.next()) stats.setTotalTickets(rsTotal.getInt("total"));
             

             ResultSet rsResolus = stmt.executeQuery("SELECT COUNT(*) AS resolus FROM incidents WHERE etat = 'RESOLVED'");
             if (rsResolus.next()) stats.setTicketsResolus(rsResolus.getInt("resolus"));
             

             ResultSet rsEtat = stmt.executeQuery("SELECT etat, COUNT(*) AS nb FROM incidents GROUP BY etat");
             java.util.Map<String, Integer> parEtat = new java.util.HashMap<>();
             while (rsEtat.next()) parEtat.put(rsEtat.getString("etat"), rsEtat.getInt("nb"));
             stats.setTicketsParEtat(parEtat);
             
             ResultSet rsTemps = stmt.executeQuery("SELECT AVG(julianday(date_resolution) - julianday(date_creation)) * 24 * 60 AS temps_moyen FROM incidents WHERE etat = 'RESOLVED'");
             if (rsTemps.next()) stats.setTempsMoyenResolutionMinutes(rsTemps.getDouble("temps_moyen"));
             
            
             String sqlAgent = "SELECT agent_id, COUNT(*) AS nb, "
                     + "(COUNT(*) / MAX(1.0, julianday('now', 'localtime') - julianday(MIN(date_assignation)))) AS pression "
                     + "FROM incidents WHERE agent_id IS NOT NULL GROUP BY agent_id";
             ResultSet rsAgent = stmt.executeQuery(sqlAgent);
             
             java.util.Map<String, Integer> parAgent = new java.util.HashMap<>();
             java.util.Map<String, Double> pression = new java.util.HashMap<>();
             while (rsAgent.next()) {
                 String agentId = rsAgent.getString("agent_id");
                 parAgent.put(agentId, rsAgent.getInt("nb"));
                 pression.put(agentId, rsAgent.getDouble("pression"));
             }
             stats.setTicketsParAgent(parAgent);
             stats.setPressionParAgent(pression);
             
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du calcul des statistiques : " + e.getMessage());
        }
        return stats;
    }
}