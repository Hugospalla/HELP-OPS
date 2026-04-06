package Serveur;

import java.rmi.Naming;

import Serveur.dao.JdbcIncidentDao;
import commons.interfaces.ISupervisionInternal;

public class ServeurIncident {

	public static void main(String[] args) {
		try {
			ISupervisionInternal supInternal = (ISupervisionInternal) Naming.lookup("rmi://localhost:1099/SupervisionInternal");
			
			JdbcIncidentDao dao = new JdbcIncidentDao();

			IncidentImpl incidentService = new IncidentImpl(dao, supInternal);
			
			Naming.rebind("rmi://localhost:1099/IncidentService", incidentService);
			System.out.println("INC >> Serveur d'incidents prêt");
		} catch (Exception e) {
			System.err.println("INC >> Erreur : ServeurAuth ou ServeurSupervision non lancés.");
			e.printStackTrace();
		}
	}
}
