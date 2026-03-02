package Serveur.dao;

import java.util.List;

import commons.modele.Incident;

public interface IIncidentDao {

	void save(Incident incident);
	
	List<Incident> getIncidentsByAuteur(String auteur);
	
	Incident getIncidentsById(String id);
	
	List<Incident> getAllIncidents();
	
}
