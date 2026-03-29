package Serveur.dao;

import commons.modele.Incident;
import java.util.List;

public interface IIncidentDao {

	void save(Incident incident);
	
	List<Incident> getIncidentsByAuteur(String auteur);
	
	Incident getIncidentsById(String id);
	
	List<Incident> getAllIncidents();
	// ... tes autres méthodes
    public commons.modele.Statistiques getStatistiques();
	
}
