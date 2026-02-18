package Client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import interfaceRMI.Auth;

public class MenuPage implements ActionListener{

	JFrame frame = new JFrame();
	JLabel userLabel = new JLabel();
	JButton cTicket = new JButton("Déclarer incident");
	JButton lTicket = new JButton("Liste incident");
	JPanel menuPanel = new JPanel();
	JPanel cTicketPanel = new JPanel();
	JPanel lTicketPanel = new JPanel();
	
	MenuPage() throws MalformedURLException, RemoteException, NotBoundException{
		
		
		Auth auth = (Auth) Naming.lookup("rmi://localhost:1099/AuthService");
		
		menuPanel.setBounds(0,0,250,700);
		menuPanel.setBackground(new Color(18, 18, 30));
		
		cTicketPanel.setBounds(400,0,495,1100);
		cTicketPanel.setBackground(Color.red);
		cTicketPanel.setVisible(true);
		
		lTicketPanel.setBounds(400,0,495,1100);
		lTicketPanel.setBackground(Color.blue);
		lTicketPanel.setVisible(false);
		
		cTicket.setBounds(50,300,200,25);
		cTicket.setBackground(new Color(123, 104, 238));
		cTicket.setFocusable(false);
		cTicket.addActionListener(this);
		
		
		lTicket.setBounds(50,400,200,25);
		lTicket.setBackground(new Color(123, 104, 238));
		lTicket.setFocusable(false);	
		lTicket.addActionListener(this);
		
		
		frame.add(cTicket);
		frame.add(lTicket);
		frame.add(menuPanel);
		frame.add(cTicketPanel);
		frame.add(lTicketPanel);
		
		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frame.setSize(1200,700);
		frame.setLocationRelativeTo(null);
		frame.setLayout(null);
		frame.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==cTicket) {
			lTicketPanel.setVisible(false);
			cTicketPanel.setVisible(true);
		}else if (e.getSource()==lTicket) {
			lTicketPanel.setVisible(true);
			cTicketPanel.setVisible(false);
		}
	}
}
