package Serveur.dao;

import Serveur.utils.PasswordUtil; // Import du code de ton camarade
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:helpops.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initDatabase() {
        String sqlUsers = "CREATE TABLE IF NOT EXISTS users ("
                + "login TEXT PRIMARY KEY, "
                + "password TEXT NOT NULL, "
                + "role TEXT NOT NULL"
                + ");";

        String sqlIncidents = "CREATE TABLE IF NOT EXISTS incidents ("
                + "id TEXT PRIMARY KEY, "
                + "categorie TEXT NOT NULL, "
                + "titre TEXT NOT NULL, "
                + "description TEXT NOT NULL, "
                + "etat TEXT NOT NULL, "
                + "agent_id TEXT, "
                + "auteur TEXT NOT NULL, "
                + "date_creation TEXT NOT NULL, "
                + "date_assignation TEXT, "
                + "FOREIGN KEY (auteur) REFERENCES users(login), "
                + "FOREIGN KEY (agent_id) REFERENCES users(login)"
                + ");";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlUsers);
            stmt.execute(sqlIncidents);

            // Insertion sécurisée avec le hachage de ton ami
            String insertQuery = "INSERT OR IGNORE INTO users (login, password, role) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                
                String mdpHache = PasswordUtil.hash("test"); 

                String[][] defaultUsers = {
                    {"hugos", "UTILISATEUR"},
                    {"julien", "UTILISATEUR"},
                    {"hugol", "UTILISATEUR"},
                    {"fabien", "UTILISATEUR"},
                    {"admin", "AGENT"},
                    {"admin2", "AGENT"}
                };

                for (String[] u : defaultUsers) {
                    pstmt.setString(1, u[0]);
                    pstmt.setString(2, mdpHache); // On insère le mot de passe chiffré !
                    pstmt.setString(3, u[1]);
                    pstmt.executeUpdate();
                }
            }
            System.out.println("BDD >> Base de données SQLite prête avec mots de passe chiffrés !");
            
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'initialisation de la BD : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur de chiffrement : " + e.getMessage());
        }
    }
}