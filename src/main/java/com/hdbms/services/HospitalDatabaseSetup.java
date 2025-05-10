package com.hdbms.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import io.github.cdimascio.dotenv.Dotenv;

public class HospitalDatabaseSetup {
    @SuppressWarnings("CallToPrintStackTrace")
    public static void runSetup(String[] args) {
        // Database connection parameters
        String baseUrl = "jdbc:mysql://localhost:3306/";
        String dbName = "hospital_db";
        String url = baseUrl + dbName + "?useSSL=false&serverTimezone=UTC";
        Dotenv dotenv = Dotenv.load();
        String user = "root";
        String password = dotenv.get("DB_PASSWORD");

        // Load MySQL JDBC Driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // System.out.println("MySQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver not found. Ensure the MySQL Connector/J library is included.");
            e.printStackTrace();
            return;
        }

        // Establish database connection
        try (Connection conn = DriverManager.getConnection(baseUrl, user, password);
                Statement stmt = conn.createStatement()) {

            // Create database
            try {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
                // System.out.println("Database '" + dbName + "' created or already exists.");
            } catch (SQLException e) {
                System.err.println("Error creating database: " + e.getMessage() +
                        ", SQL State: " + e.getSQLState() +
                        ", Error Code: " + e.getErrorCode());
                e.printStackTrace();
                return;
            }

            // Connect to the specific database
            try (Connection dbConn = DriverManager.getConnection(url, user, password);
                    Statement dbStmt = dbConn.createStatement()) {

                // Select the database
                try {
                    dbStmt.executeUpdate("USE " + dbName);
                    // System.out.println("Using database '" + dbName + "'.");
                } catch (SQLException e) {
                    System.err.println("Error selecting database: " + e.getMessage() +
                            ", SQL State: " + e.getSQLState() +
                            ", Error Code: " + e.getErrorCode());
                    e.printStackTrace();
                    return;
                }

                // Create users table
                try {
                    dbStmt.executeUpdate(
                            "CREATE TABLE IF NOT EXISTS users (" +
                                    "hash_id VARCHAR(64) PRIMARY KEY, " +
                                    "username VARCHAR(100) UNIQUE NOT NULL, " +
                                    "password VARCHAR(255) NOT NULL," +
                                    "role ENUM('admin', 'doctor', 'receptionist', 'lab_technician', 'patient') NOT NULL"
                                    +
                                    ")");
                    // System.out.println("Table 'users' created or already exists.");
                } catch (SQLException e) {
                    System.err.println("Error creating table 'users': " + e.getMessage() +
                            ", SQL State: " + e.getSQLState() +
                            ", Error Code: " + e.getErrorCode());
                    e.printStackTrace();
                }

                // Create doctor table
                try {
                    dbStmt.executeUpdate(
                            "CREATE TABLE IF NOT EXISTS doctor (" +
                                    "hash_id VARCHAR(64) PRIMARY KEY, " +
                                    "name VARCHAR(100) NOT NULL, " +
                                    "surname VARCHAR(100) NOT NULL, " +
                                    "gender ENUM('M', 'F', 'Other') NOT NULL, " +
                                    "department VARCHAR(100) NOT NULL, " +
                                    "id_number VARCHAR(50) NOT NULL, " +
                                    // "date_of_joining DATE NOT NULL, " +
                                    // "admin_hash_id VARCHAR(64), " +
                                    // "address VARCHAR(255), " +
                                    // "email VARCHAR(255) UNIQUE, " +
                                    // "phone_number VARCHAR(20), " +
                                    // "additional_info TEXT, " +
                                    "FOREIGN KEY (hash_id) REFERENCES users(hash_id) ON DELETE CASCADE " +
                                    // "FOREIGN KEY (admin_hash_id) REFERENCES admin(hash_id) ON DELETE SET NULL" +
                                    ")");
                    // System.out.println("Table 'doctor' created or already exists.");
                } catch (SQLException e) {
                    System.err.println("Error creating table 'doctor': " + e.getMessage() +
                            ", SQL State: " + e.getSQLState() +
                            ", Error Code: " + e.getErrorCode());
                    e.printStackTrace();
                }

                // Create patient table
                try {
                    dbStmt.executeUpdate(
                            "CREATE TABLE IF NOT EXISTS patient (" +
                                    "hash_id VARCHAR(64) PRIMARY KEY, " +
                                    "name VARCHAR(100) NOT NULL, " +
                                    "surname VARCHAR(100) NOT NULL, " +
                                    "gender ENUM('M', 'F', 'Other') NOT NULL, " +
                                    "age INT NOT NULL, " +
                                    "blood_group VARCHAR(10), " +
                                    // "medical_history TEXT,"+
                                    // "past_surgeries TEXT, " +
                                    // "referred_by VARCHAR(100), " +
                                    "date_of_birth DATE NOT NULL " +
                                    // "address VARCHAR(255), " +
                                    // "email VARCHAR(255) UNIQUE, " +
                                    // "phone_number VARCHAR(20), " +
                                    // "doctor_hash_id VARCHAR(64), " +
                                    // "receptionist_hash_id VARCHAR(64), " +
                                    // "technician_hash_id VARCHAR(64), " +
                                    // "additional_info TEXT, " +
                                    // "FOREIGN KEY (hash_id) REFERENCES users(hash_id) ON DELETE CASCADE, " +
                                    // "FOREIGN KEY (doctor_hash_id) REFERENCES doctor(hash_id) ON DELETE SET NULL,
                                    // " +
                                    // "FOREIGN KEY (receptionist_hash_id) REFERENCES receptionist(hash_id) ON
                                    // DELETE SET NULL, " +
                                    // "FOREIGN KEY (technician_hash_id) REFERENCES lab_technician(hash_id) ON
                                    // DELETE SET NULL" +
                                    ")");
                    // System.out.println("Table 'patient' created or already exists.");
                } catch (SQLException e) {
                    System.err.println("Error creating table 'patient': " + e.getMessage() +
                            ", SQL State: " + e.getSQLState() +
                            ", Error Code: " + e.getErrorCode());
                    e.printStackTrace();
                }

                // Create appointment table
                try {
                    dbStmt.executeUpdate(
                            "CREATE TABLE IF NOT EXISTS appointment (" +
                                    "hash_id VARCHAR(64) PRIMARY KEY, " +
                                    "patient_hash_id VARCHAR(64) NOT NULL, " +
                                    "doctor_hash_id VARCHAR(64) NOT NULL, " +
                                    "appointment_date DATETIME NOT NULL, " +
                                    "status ENUM('Scheduled', 'Completed', 'Cancelled') NOT NULL, " +
                                    "additional_info TEXT, " +
                                    "FOREIGN KEY (patient_hash_id) REFERENCES patient(hash_id) ON DELETE CASCADE, " +
                                    "FOREIGN KEY (doctor_hash_id) REFERENCES doctor(hash_id) ON DELETE CASCADE" +
                                    ")");
                    // System.out.println("Table 'appointment' created or already exists.");
                } catch (SQLException e) {
                    System.err.println("Error creating table 'appointment': " + e.getMessage() +
                            ", SQL State: " + e.getSQLState() +
                            ", Error Code: " + e.getErrorCode());
                    e.printStackTrace();
                }

                // Create bill table
                try {
                    dbStmt.executeUpdate(
                            "CREATE TABLE IF NOT EXISTS bill (" +
                                    "hash_id VARCHAR(64) PRIMARY KEY, " +
                                    "patient_hash_id VARCHAR(64) NOT NULL, " +
                                    "appointment_hash_id VARCHAR(64) NOT NULL," +
                                    "amount DECIMAL(10, 2) NOT NULL, " +
                                    "bill_date DATE NOT NULL, " +
                                    "status ENUM('Pending', 'Paid') NOT NULL, " +
                                    "additional_info TEXT, " +
                                    "FOREIGN KEY (patient_hash_id) REFERENCES patient(hash_id) ON DELETE CASCADE" +
                                    // "FOREIGN KEY (appointment_hash_id) REFERENCES appointment(hash_id) ON DELETE CASCADE"
                                    // +
                                    ")");
                    // System.out.println("Table 'bill' created or already exists.");
                } catch (SQLException e) {
                    System.err.println("Error creating table 'bill': " + e.getMessage() +
                            ", SQL State: " + e.getSQLState() +
                            ", Error Code: " + e.getErrorCode());
                    e.printStackTrace();
                }
                // Create prescriptions table
                try {
                    dbStmt.executeUpdate(
                            "CREATE TABLE IF NOT EXISTS prescriptions (" +
                                    "hash_id VARCHAR(64) PRIMARY KEY, " +
                                    "patient_hash_id VARCHAR(64) NOT NULL, " +
                                    "doctor_hash_id VARCHAR(64) NOT NULL, " +
                                    "medicines TEXT NOT NULL, " +
                                    "suggested_tests TEXT, " +
                                    "FOREIGN KEY (patient_hash_id) REFERENCES patient(hash_id) ON DELETE CASCADE, " +
                                    "FOREIGN KEY (doctor_hash_id) REFERENCES doctor(hash_id) ON DELETE CASCADE" +
                                    ")");
                    // System.out.println("Table 'prescriptions' created or already exists.");
                } catch (SQLException e) {
                    System.err.println("Error creating table 'prescriptions': " + e.getMessage() +
                            ", SQL State: " + e.getSQLState() +
                            ", Error Code: " + e.getErrorCode());
                    e.printStackTrace();
                }

            } catch (SQLException e) {
                System.err.println("Connection error: " + e.getMessage() +
                        ", SQL State: " + e.getSQLState() +
                        ", Error Code: " + e.getErrorCode());
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.err.println("Connection error: " + e.getMessage() +
                    ", SQL State: " + e.getSQLState() +
                    ", Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }
        System.out.println("Database setup completed successfully.");

    }

    /*
     * public static void main(String[] args) {
     * runSetup(args);
     * }
     */
}