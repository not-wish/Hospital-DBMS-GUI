package com.hdbms.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import io.github.cdimascio.dotenv.Dotenv;

public class PatientDashboard {
    private final String url;
    private final String user = "root";
    private final String password;

    private final String patientId;

    public PatientDashboard(String patientId, Scanner scanner) {
        this.patientId = patientId;
        Dotenv dotenv = Dotenv.load();
        this.url = dotenv.get("DB_URL");
        this.password = dotenv.get("DB_PASSWORD");

        if (!authenticatePatient(patientId)) {
            System.out.println("Invalid Patient ID! Access Denied.");
            return;
        }

        System.out.println("\nWelcome to Your Patient Dashboard!");
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. View Appointments");
            System.out.println("2. View Prescriptions");
            System.out.println("3. View Payment Status");
            System.out.println("4. Exit");

            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    viewAppointments();
                    break;
                case 2:
                    viewPrescriptions();
                    break;
                case 3:
                    viewAllInvoice();
                    break;
                case 4:
                    System.out.println("Exiting dashboard. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

    }

    // Authentication check
    public boolean authenticatePatient(String patientId) {
        String query = "SELECT hash_id FROM patient WHERE hash_id = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // If record exists, authentication succeeds
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to fetch appointments
    public void viewAppointments() {
        String query = "SELECT appointment_date, doctor_hash_id, status, additional_info FROM appointment WHERE patient_hash_id = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();

            PatientDoctorService doctorService = new PatientDoctorService(); // to fetch doctor names
            System.out.println("\n--- Your Appointments ---");
            while (rs.next()) {
                String doctor_name = doctorService.getDoctorName(rs.getString("doctor_hash_id"));
                System.out.println("Date: " + rs.getString("appointment_date"));
                System.out.println("Doctor: " + doctor_name);
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("Additional Info: " + rs.getString("additional_info"));
                System.out.println("-------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to fetch prescriptions
    public void viewPrescriptions() {
        String query = "SELECT * FROM prescriptions WHERE patient_hash_id = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n--- Your Prescriptions ---");
            while (rs.next()) {
                PatientDoctorService patientDoctorService = new PatientDoctorService();
                System.out.println("Prescription ID: " + rs.getString("hash_id"));
                System.out.println("Patient Name: " + patientDoctorService.getPatientName(rs.getString("patient_hash_id")));
                System.out.println("Doctor Name: " + patientDoctorService.getDoctorName(rs.getString("doctor_hash_id")));
                System.out.println("Medicine: " + rs.getString("medicines"));
                System.out.println("Suggested Tests: " + rs.getString("suggested_tests"));
                System.out.println("--------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to check payment status
    public void viewAllInvoice() {
        String query = "SELECT * FROM bill WHERE patient_hash_id = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n--- All Invoice ---");
            while (rs.next()) {
                System.out.println("Bill ID: " + rs.getString("hash_id"));
                System.out.println("Amount: " + rs.getString("amount"));
                System.out.println("Bill Date: " + rs.getString("bill_date"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("---------------------------------------------------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}