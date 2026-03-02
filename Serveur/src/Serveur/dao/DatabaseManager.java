package Serveur.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    // URL de connexion SQLite (crée un fichier helpops.db à la racine du projet)
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

        // Insertion des utilisateurs par défaut (INSERT OR IGNORE évite les erreurs si on relance le serveur)
        String sqlBouchon = "INSERT OR IGNORE INTO users (login, password, role) VALUES "
                + "('hugos', 'test', 'UTILISATEUR'),"
                + "('julien', 'test', 'UTILISATEUR'),"
                + "('hugol', 'test', 'UTILISATEUR'),"
                + "('fabien', 'test', 'UTILISATEUR'),"
                + "('admin', 'test', 'AGENT'),"
                + "('admin2', 'test', 'AGENT');";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlUsers);
            stmt.execute(sqlIncidents);
            stmt.execute(sqlBouchon);
            System.out.println("BDD >> Base de données SQLite prête !");
        } catch (SQLException e) {
            System.err.println("Erreur d'initialisation de la BD : " + e.getMessage());
        }
    }
}