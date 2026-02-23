package Serveur.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commons.modele.Incident;

public class InMemoryIncidentDao implements IIncidentDao{

	private Map<String, Incident> incidentbd = new HashMap<>();
	
	@Override
	public void save(Incident incident) {
		incidentbd.put(incident.getId(), incident);
	}
	
	@Override
	public List<Incident> getIncidentsByAuteur(String auteur){
		List<Incident> res = new ArrayList<>();
		for (Incident ticket : incidentbd.values()) {
			if (auteur.equals(ticket.getAuteur())) {
				res.add(ticket);
			}
		}
		return res;
	}
	
	@Override
	public Incident getIncidentsById(String id) {
		return incidentbd.get(id);
	}
}
