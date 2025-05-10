package com.hdbms.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class UserDAOImpl implements userDAO {

    @Override
    public boolean saveUser(String hashId, String username, String password, String role) {
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("DB_URL");
        String dbUser = "root";
        String dbPassword = dotenv.get("DB_PASSWORD");

        String query = "INSERT INTO users (hash_id, username, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, hashId);
            stmt.setString(2, username);
            stmt.setString(3, password); // Hash it before calling this method if needed
            stmt.setString(4, role);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean getUserById(String hashId) {
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("DB_URL");
        String dbUser = "root";
        String dbPassword = dotenv.get("DB_PASSWORD");

        String query = "SELECT * FROM users WHERE hash_id = ?";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, hashId);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // true if user exists

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean getUserByUsername(String username) {
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("DB_URL");
        String dbUser = "root";
        String dbPassword = dotenv.get("DB_PASSWORD");

        String query = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // true if user exists

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean validateCredentials(String username, String password) {
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("DB_URL");
        String dbUser = "root";
        String dbPassword = dotenv.get("DB_PASSWORD");

        String query = "SELECT password FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password); // Use hashing in production
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public String getUserRole(String username) {
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("DB_URL");
        String dbUser = "root";
        String dbPassword = dotenv.get("DB_PASSWORD");

        String query = "SELECT role FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username.trim());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("User role: " + rs.getString("role")); // Debugging line
                return rs.getString("role"); // assumes 'role' column exists
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; 
    }

    public String getUserId(String username) {
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("DB_URL");
        String dbUser = "root";
        String dbPassword = dotenv.get("DB_PASSWORD");

        String query = "SELECT hash_id FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("hash_id"); // assumes 'hash_id' is a string (e.g., UUID)
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}