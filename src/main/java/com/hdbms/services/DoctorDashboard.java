package com.hdbms.services;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.hdbms.DAO.UserDAOImpl;

import io.github.cdimascio.dotenv.Dotenv;

public class DoctorDashboard {
    private final String url;
    private final String user = "root";
    private final String password;
    private final String doctorHashId;

    public DoctorDashboard(String doctorHashId, Scanner scanner) {
        this.doctorHashId = doctorHashId;
        Dotenv dotenv = Dotenv.load();
        this.url = dotenv.get("DB_URL");
        this.password = dotenv.get("DB_PASSWORD");

        System.out.println("\nWelcome to Your Doctor Dashboard!");
        System.out.println("Your Unique Hash ID: " + doctorHashId);

        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. View Scheduled Appointments");
            System.out.println("2. Prescribe Medicine");
            System.out.println("3. Exit");

            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewAppointments();
                    break;
                case 2:

                    prescribeMedicine(scanner);
                    break;
                case 3:
                    System.out.println("Exiting dashboard. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // View scheduled appointments based on Hash ID
    public void viewAppointments() {
        String query = "SELECT appointment_date, patient_hash_id, status, additional_info FROM appointment WHERE doctor_hash_id = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, doctorHashId);
            ResultSet rs = stmt.executeQuery();
            PatientDoctorService patientDoctorService = new PatientDoctorService(); // to fetch patient name
            System.out.println("\n--- Scheduled Appointments ---");
            while (rs.next()) {
                System.out.println("Patient: " + patientDoctorService.getPatientName(rs.getString("patient_hash_id")));
                System.out.println("Appointment Date: " + rs.getString("appointment_date"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("Additional Info: " + rs.getString("additional_info"));
                System.out.println("------------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Prescribe medicine to a specific patient
    public void prescribeMedicine(Scanner scanner) {
        String patient_hash_id = null;
        String medicines = null;
        String suggestedTests = null;
        String prescription_id = null;
        UserDAOImpl userDAOImpl = new UserDAOImpl();
        try {
            System.out.println("Enter patient username: ");
            patient_hash_id = userDAOImpl.getUserId(scanner.nextLine());
            System.out.println("Medicines: ");
            medicines = scanner.nextLine();
            System.out.println("Suggested Tests: ");
            suggestedTests = scanner.nextLine();
            prescription_id = HashUtil.generateKey(patient_hash_id + doctorHashId + medicines + suggestedTests);

        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }

        String query = "INSERT INTO prescriptions (hash_id, patient_hash_id, doctor_hash_id, medicines, suggested_tests) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, prescription_id);
            stmt.setString(2, patient_hash_id);
            stmt.setString(3, doctorHashId);
            stmt.setString(4, medicines);
            stmt.setString(5, suggestedTests);
            stmt.executeUpdate();

            System.out.println("\nPrescription added successfully for Patient ID: " + patient_hash_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}