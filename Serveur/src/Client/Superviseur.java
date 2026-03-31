package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Superviseur {
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("   CONSOLE DE SUPERVISION HELP'OPS    ");
        System.out.println("=========================================");

        try (Scanner sc = new Scanner(System.in);
             Socket socket = new Socket("127.0.0.1", 8081);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String choix = "";
            while (!choix.equals("A") && !choix.equals("B")) {
                System.out.println("\nChoisissez le mode de supervision :");
                System.out.println("A - Option A : Flux à partir de maintenant");
                System.out.println("B - Option B : Rattrapage des 20 derniers + Flux en direct");
                System.out.print("Votre choix (A/B) : ");
                choix = sc.nextLine().toUpperCase();
            }

            out.println(choix);

            System.out.println("\n--- EN ATTENTE DES ÉVÉNEMENTS ---");
            
            
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }

        } catch (Exception e) {
            System.err.println("Connexion au serveur de supervision perdue : " + e.getMessage());
        }
    }
}