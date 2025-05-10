package com.hdbms.services;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class PatientDoctorService {
    private final String url;
    private final String user = "root";
    private final String password;

    public PatientDoctorService() {
        Dotenv dotenv = Dotenv.load();
        this.url = dotenv.get("DB_URL");
        this.password = dotenv.get("DB_PASSWORD");
    }

    public boolean addPatient(String hashId, String name, String surname, String gender, int age,
            String dob, String bloodGroup) {
        String query = "INSERT INTO patient (hash_id, name, surname, gender, age, date_of_birth, blood_group) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, hashId);
            stmt.setString(2, name);
            stmt.setString(3, surname);
            stmt.setString(4, gender);
            stmt.setInt(5, age);
            stmt.setDate(6, Date.valueOf(dob));
            stmt.setString(7, bloodGroup);
            // stmt.setString(7, doctorHashId); // Uncomment if you want to set
            // doctor_hash_id
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // public boolean updatePatientDoctor(String patientHashId, String
    // newDoctorHashId) {
    // String query = "UPDATE patient SET doctor_hash_id = ? WHERE hash_id = ?";
    // try (Connection conn = DriverManager.getConnection(url, user, password);
    // PreparedStatement stmt = conn.prepareStatement(query)) {
    // stmt.setString(1, newDoctorHashId);
    // stmt.setString(2, patientHashId);
    // return stmt.executeUpdate() > 0;
    // } catch (SQLException e) {
    // e.printStackTrace();
    // return false;
    // }
    // }

    public boolean deletePatient(String hashId) {
        String query = "DELETE FROM patient WHERE hash_id = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, hashId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addDoctor(String hashId, String name, String surname, String gender, String department, String id_number) {
        String query = "INSERT INTO doctor (hash_id, name, surname, gender, department, id_number) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, hashId);
            stmt.setString(2, name);
            stmt.setString(3, surname);
            stmt.setString(4, gender);
            stmt.setString(5, department);
            stmt.setString(6, id_number);
            // stmt.setString(7, doctorHashId); // Uncomment if you want to set
            // doctor_hash_id
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDoctor(String hashId) {
        String query = "DELETE FROM doctor WHERE hash_id = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, hashId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePatientDetails(String hashId, String newBloodGroup, String newDateOfBirth) {
        String query = "UPDATE patient SET bloodGroup = ?, date_of_birth = ? WHERE hash_id = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newBloodGroup);
            stmt.setDate(2, Date.valueOf(newDateOfBirth)); // Ensure correct format (YYYY-MM-DD)
            stmt.setString(3, hashId);
            return stmt.executeUpdate() > 0; // Returns true if update succeeds
        } catch (SQLException e) {
            System.err.println("Database update erSror: " + e.getMessage());
            return false;
        }
    }

    public boolean updateDoctorDetails(String hashId, String newIdNumber, String newDepartment) {
        String query = "UPDATE doctor SET IdNumber = ?, Department = ? WHERE hash_id = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newIdNumber);
            stmt.setDate(2, Date.valueOf(newDepartment)); // Ensure correct format (YYYY-MM-DD)
            stmt.setString(3, hashId);
            return stmt.executeUpdate() > 0; // Returns true if update succeeds
        } catch (SQLException e) {
            System.err.println("Database update error: " + e.getMessage());
            return false;
        }
    }

    public String getDoctorName(String hash_id) {
        String query = "SELECT name, surname FROM doctor WHERE hash_id = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, hash_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("name") + " " + rs.getString("surname"); // assumes 'role' column exists
            }

        } catch (SQLException e) {
            System.err.println("Error fetching doctor name: " + e.getMessage());
            return "null"; // or handle as needed
        }

        return null; // or handle as needed
    }

    public String getPatientName(String hash_id) {
        String query = "SELECT name, surname FROM patient WHERE hash_id = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, hash_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("name") + " " + rs.getString("surname"); // assumes 'role' column exists
            }

        } catch (SQLException e) {
            System.err.println("Error fetching patient name: " + e.getMessage());
            return "null"; // or handle as needed
        }

        return null; // or handle as needed
    }

}
