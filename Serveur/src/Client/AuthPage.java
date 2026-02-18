package Client;

import java.awt.Color;
import java.awt.Font;
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
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import Serveur.User;
import interfaceRMI.Auth;

public class AuthPage implements ActionListener{
	
	

	private static String monToken = null;
	JFrame frame = new JFrame();
	JLabel titre = new JLabel("HELP'OPS");
	JButton loginButton = new JButton("Se connecter");
	JTextField userField = new JTextField();
	JPasswordField mdpField = new JPasswordField();
	JLabel userLabel = new JLabel("Nom de compte "); 
	JLabel mdpLabel = new JLabel("Mot de passe ");
	JLabel messageLabel = new JLabel();
	

	
	
	HashMap<String, User> UserBD = new HashMap <String, User>();
	
	AuthPage(HashMap<String, User> UserBDOriginal){
		
		UserBD = UserBDOriginal;
		
		titre.setBounds(170,40,200,55);
		titre.setFont(new Font(null,Font.BOLD,30));
		titre.setForeground(new Color(235, 235, 245));
		
		userLabel.setBounds(150,125,100,25);
		userLabel.setForeground(new Color(235, 235, 245));
		mdpLabel.setBounds(150,200,100,25);
		mdpLabel.setForeground(new Color(235, 235, 245));
		
		messageLabel.setBounds(125,250,250,35);
		messageLabel.setFont(new Font(null,Font.ITALIC,25));
		
		userField.setBounds(150,150,200,25);
		userField.setBackground(new Color(40, 40, 58));
		userField.setForeground(new Color(235, 235, 245));
		mdpField.setBounds(150,225,200,25);
		mdpField.setBackground(new Color(40, 40, 58));
		mdpField.setForeground(new Color(235, 235, 245));
		
		loginButton.setBounds(150,350,200,25);
		loginButton.setBackground(new Color(123, 104, 238));
		loginButton.setFocusable(false);
		loginButton.addActionListener(this);
		
		frame.add(titre);
		frame.add(userLabel);
		frame.add(mdpLabel);
		frame.add(messageLabel);
		frame.add(userField);
		frame.add(mdpField);
		frame.add(loginButton);
		
		
		frame.getContentPane().setBackground(new Color(18, 18, 30));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("HELP'OPS");
		frame.setSize(500,450);
		frame.setLocationRelativeTo(null);
		frame.setLayout(null);
		frame.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e){
		
		
		
		if (e.getSource()==loginButton) {
			
			Auth auth = null;
			try {
				auth = (Auth) Naming.lookup("rmi://localhost:1099/AuthService");
			} catch (MalformedURLException | RemoteException | NotBoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String login = userField.getText();
			String password = String.valueOf(mdpField.getPassword());
			
			
			if (e.getSource()==loginButton) {
			
				try {
					monToken = auth.authentification(login, password);
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			
				if (monToken != null) {
					messageLabel.setForeground(Color.green);
					messageLabel.setText("Connecté avec succès");
					frame.dispose();
					try {
						MenuPage menuPage = new MenuPage();
					} catch (MalformedURLException | RemoteException | NotBoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					messageLabel.setForeground(Color.red);
					messageLabel.setText("Login ou mdp incorect");
				}
			
	}
	
	}
	
}
}