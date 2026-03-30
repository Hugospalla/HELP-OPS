package Serveur.supervision;

import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SupervisionHandler implements Runnable {
	
	private Socket socket;
	private SupervisionManager manager;
	
	public SupervisionHandler(Socket socket, SupervisionManager manager) {
		this.socket = socket;
		this.manager = manager;
	}
	
	@Override
	public void run() {
		PrintWriter out = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		
			String choix = in.readLine();
			
			if ("B".equalsIgnoreCase(choix)) {
				List<String> histo = manager.getHistorique();
				out.println("--- DEBUT DE L'HISTORIQUE ---");
				for (String evt : histo) {
					out.println(evt);
				}
				out.println("--- FIN DE L'HISTORIQUE ---");
			}
			
			manager.ajouterClient(out);
			out.println(">> Vous êtes maintenant connecté au flux en temps réel");
			
			while (in.readLine() != null) {
				
			}
		} catch (IOException e) {
			System.out.println("Un superviseur s'est déconnecté.");
		} finally {
			if (out != null) {
				manager.retirerClient(out);
			}
			try {
				socket.close();
			} catch (Exception e) {
				
			}
		}
	}
}
