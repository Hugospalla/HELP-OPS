package Serveur.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import commons.modele.Categorie;
import commons.modele.Etat;
import commons.modele.Incident;

public class JdbcIncidentDao implements IIncidentDao {

    @Override
    public void save(Incident incident) {
        // "INSERT OR REPLACE" permet de créer ou de mettre à jour le ticket en une seule requête (très pratique sur SQLite)
        String query = "INSERT OR REPLACE INTO incidents (id, categorie, titre, description, etat, agent_id, auteur, date_creation, date_assignation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
                    if (dateAssignationStr != null && !dateAssignationStr.isEmpty()) {
                        incident.setDateAssignation(LocalDateTime.parse(dateAssignationStr));
                    }
                    
                    incidents.add(incident);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lecture incidents : " + e.getMessage());
        }
        return incidents;
    }
}