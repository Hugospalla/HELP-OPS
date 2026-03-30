package Serveur.supervision;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SupervisionManager {

	private final List<PrintWriter> clientsConnectes = new CopyOnWriteArrayList<>();
	
	private final List<String> historique = Collections.synchronizedList(new ArrayList<>());
	private final int MAX_HISTORY = 20;
	
	public void ajouterClient(PrintWriter out) {
		clientsConnectes.add(out);
	}
	
	public void retirerClient(PrintWriter out) {
		clientsConnectes.remove(out);
	}
	
	public List<String> getHistorique() {
		synchronized (historique) {
			return new ArrayList<>(historique);
		}
	}
	
	public void publierEvenement(String evenement) {
		System.out.println("STEAM >>" + evenement);
		
		synchronized (historique) {
			if (historique.size() >= MAX_HISTORY) {
				historique.remove(0);
			}
			historique.add(evenement);
		}
		
		for (PrintWriter out : clientsConnectes) {
			try {
				out.println(evenement);
			} catch (Exception e) {
				retirerClient(out);
			}
		}
	}
	
	
}
