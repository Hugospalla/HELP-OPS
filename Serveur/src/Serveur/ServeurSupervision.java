package Serveur;

import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import Serveur.supervision.SupervisionHandler;
import Serveur.supervision.SupervisionManager;
import commons.interfaces.ISupervisionInternal;

public class ServeurSupervision extends UnicastRemoteObject implements ISupervisionInternal{

	private SupervisionManager manager;
	
	protected ServeurSupervision() throws RemoteException {
		this.manager = new SupervisionManager();
	}
	
	@Override
	public void notifierEvenement(String message) throws RemoteException {
		manager.publierEvenement(message);
	}
	
	public static void main(String[] args) {
		try {
			ServeurSupervision serveurSup = new ServeurSupervision();
			Naming.rebind("rmi://localhost:1099/SupervisionInternal", serveurSup);
			
			new Thread(() -> {
				try (ServerSocket serverSocket = new ServerSocket(8081)) {
					System.out.println("SUPERVISION >> Flux sockets prêt ! Port : 8081");
					while (true) {
						Socket client = serverSocket.accept();
						new Thread(new SupervisionHandler(client, serveurSup.manager)).start();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
		} catch (Exception e) {
		e.printStackTrace();
		}
	}
}
