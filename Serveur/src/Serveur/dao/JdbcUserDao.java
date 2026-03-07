package Serveur.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import commons.modele.Role;
import commons.modele.User;

public class JdbcUserDao implements IUserDao {

    @Override
    public User getUserByLogin(String login) {
        String query = "SELECT login, password, role FROM users WHERE login = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, login);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("login"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role"))
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération de l'utilisateur : " + e.getMessage());
        }
        return null;
    }
}