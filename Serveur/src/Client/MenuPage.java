package Client;

import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import Serveur.User;
import interfaceRMI.Auth;

public class MenuPage implements ActionListener{

	JFrame frame = new JFrame();
	JLabel userLabel = new JLabel();
	JButton cTicket = new JButton();
	
	
	
	MenuPage() throws MalformedURLException, RemoteException, NotBoundException{
		
		
		Auth auth = (Auth) Naming.lookup("rmi://localhost:1099/AuthService");
		
		
		String user = auth.getLoginByToken();
		
		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frame.setSize(960,540);
		frame.setLayout(null);
		frame.setVisible(true);
	}
}
