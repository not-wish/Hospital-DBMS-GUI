package com.hdbms.services;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.hdbms.DAO.UserDAOImpl;

import io.github.cdimascio.dotenv.Dotenv;

public class ReceptionistDashboard {

    private final String url;
    private final String user = "root";
    private final String password;

    public ReceptionistDashboard(Scanner scanner) {
        Dotenv dotenv = Dotenv.load();
        this.url = dotenv.get("DB_URL");
        this.password = dotenv.get("DB_PASSWORD");

        int choice = -1;

        do {

            System.out.println("=================================================");
            System.out.println("\nWelcome to the Receptionist Dashboard!\n");
            System.out.println("Please choose an option:");
            System.out.println("=================================================");
            System.out.println("1. Schedule an Appointment");
            System.out.println("2. View Appointments");
            System.out.println("3. Cancel an Appointment");
            System.out.println("4. Update Patient Information");
            System.out.println("5. View Patient Information");
            System.out.println("6. View Doctor Information");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
                // scanner.nextLine(); // Clear the newline character

                switch (choice) {
                    case 1:
                        scheduleAppointment(scanner);
                        break;
                    case 2:
                        viewAppointments(scanner);
                        break;
                    case 3:
                        cancelAppointment(scanner);
                        break;
                    case 4:
                        updatePatientInfo(scanner);
                        break;
                    case 5:
                        viewPatientInfo(scanner);
                        break;
                    case 6:
                        viewDoctorInfo(scanner);
                        break;
                    case 0:
                        System.out.println("Exiting Receptionist Dashboard.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number (1, 2, or 3).");
                scanner.nextLine(); // Clear the invalid input
                choice = -1; // Reset to keep the loop running
            }
        } while (choice != 0);

    }

    public void scheduleAppointment(Scanner scanner) {
        UserDAOImpl userDAOImpl = new UserDAOImpl();
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("DB_URL");
        String dbUser = "root";
        String dbPassword = dotenv.get("DB_PASSWORD");

        System.out.println("=================================================");
        System.out.println("Scheduling an appointment...");

        System.out.print("Enter patient username: ");
        String patient_hash_id = userDAOImpl.getUserId(scanner.nextLine());
        if (patient_hash_id == null) {
            System.out.println("Patient not found.");
            return;
        }

        System.out.print("Enter doctor username: ");
        String doctor_hash_id = userDAOImpl.getUserId(scanner.nextLine());
        if (doctor_hash_id == null) {
            System.out.println("Doctor not found.");
            return;
        }

        System.out.print("Enter appointment date and time (yyyy-MM-dd HH:mm:ss): ");
        String datetimeInput = scanner.nextLine();
        // Validate the datetime format (optional, but recommended)
        String datetimeRegex = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
        if (!datetimeInput.matches(datetimeRegex)) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd HH:mm:ss.");
            return;
        }

        System.out.print("Enter status (Scheduled, Completed, Cancelled): ");
        String status = scanner.nextLine();

        System.out.print("Enter any additional info (optional): ");
        String additionalInfo = scanner.nextLine();
        String hash_id = null;
        try {
            hash_id = HashUtil.generateKey(datetimeInput); // generate unique appointment ID
        } catch (Exception e) {
            System.out.println("Error generating appointment ID: " + e.getMessage());
            return;
        }

        System.out.println(hash_id);
        System.out.println(patient_hash_id);
        System.out.println(doctor_hash_id);
        System.out.println(datetimeInput);
        System.out.println(status);
        System.out.println(additionalInfo);

        String billHashId = null;
        Double amount = null;
        String additional_info = null;
        String bill_status = null;

        try {
            billHashId = HashUtil.generateKey(hash_id);
            System.out.println("Enter billing amount: ");
            amount = scanner.nextDouble();
            scanner.nextLine(); // Consume the newline character
            System.out.println("Additional info: ");
            additional_info = scanner.nextLine();
            System.out.println("Enter billing status (Paid, Pending): ");
            bill_status = scanner.nextLine();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }

        generateBill(billHashId, hash_id, patient_hash_id, datetimeInput, amount, bill_status, additional_info);

        // Execute only if paid
        if (checkBillingStatus(billHashId)) {
            String query = "INSERT INTO appointment (hash_id, patient_hash_id, doctor_hash_id, appointment_date, status, additional_info) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
                    PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, hash_id);
                stmt.setString(2, patient_hash_id);
                stmt.setString(3, doctor_hash_id);
                stmt.setString(4, datetimeInput); // assumes user enters valid format
                stmt.setString(5, status);
                stmt.setString(6, additionalInfo.isEmpty() ? null : additionalInfo);

                int rowsInserted = stmt.executeUpdate();

                if (rowsInserted > 0) {
                    System.out.println("Appointment scheduled successfully with ID: " + hash_id);
                } else {
                    System.out.println("Failed to schedule appointment.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Proceed with the rest of the code after the comment
        } else {
            System.out.println("Payment pending! Please complete billing first.");
        }
        // create billing method and if bill status = paid then the rest of the lines
        // run

    }

    public boolean checkBillingStatus(String billHashId) {
        String query = "SELECT status FROM bill WHERE hash_id = ?";
        
        try (Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, billHashId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    return "Paid".equalsIgnoreCase(status);  // Also handles NULL safely
                } else {
                    System.out.println("Bill not found with ID: " + billHashId);
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking billing status for ID " + billHashId);
            e.printStackTrace();
            return false;
        }
    }

    public boolean generateBill(String billHashId, String appointment_hash_id, String patient_hash_id, String bill_date,
            Double amount, String status, String additional_info) {
        String query = "INSERT INTO bill (hash_id, patient_hash_id, appointment_hash_id,amount ,bill_date , status, additional_info) VALUES (?, ?, ?, ?, ?, ?,?)";
        try (Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, billHashId);
            stmt.setString(2, patient_hash_id);
            stmt.setString(3, appointment_hash_id);
            stmt.setDouble(4, amount);
            stmt.setString(5, bill_date);
            stmt.setString(6, status);
            stmt.setString(7, additional_info);
            // stmt.setString(7, doctorHashId); // Uncomment if you want to set
            // doctor_hash_id
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    private void viewAppointments(Scanner scanner) {
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("DB_URL");
        String dbUser = "root";
        String dbPassword = dotenv.get("DB_PASSWORD");

        System.out.println("=================================================");
        System.out.println("Viewing all appointments...");

        String query = "SELECT a.hash_id, u1.username AS patient_username, u2.username AS doctor_username, "
                + "a.appointment_date, a.status, a.additional_info "
                + "FROM appointment a "
                + "JOIN users u1 ON a.patient_hash_id = u1.hash_id "
                + "JOIN users u2 ON a.doctor_hash_id = u2.hash_id";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            System.out.printf("%-15s %-20s %-20s %-20s %-15s %-30s%n",
                    "Appointment ID", "Patient Username", "Doctor Username",
                    "Appointment Date", "Status", "Additional Info");
            System.out.println(
                    "===========================================================================================");

            while (rs.next()) {
                String appointmentId = rs.getString("hash_id");
                String patientUsername = rs.getString("patient_username");
                String doctorUsername = rs.getString("doctor_username");
                String appointmentDate = rs.getString("appointment_date");
                String status = rs.getString("status");
                String additionalInfo = rs.getString("additional_info");

                System.out.printf("Appointment ID: %s\n", appointmentId);
                System.out.println(
                        "===========================================================================================");
                System.out.printf("%-20s %-20s %-20s %-15s %-30s%n",
                        patientUsername, doctorUsername,
                        appointmentDate, status, additionalInfo != null ? additionalInfo : "N/A");
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving appointments: " + e.getMessage());
        }
    }

    private boolean cancelAppointment(Scanner scanner) {
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("DB_URL");
        String dbUser = "root";
        String dbPassword = dotenv.get("DB_PASSWORD");

        System.out.println("=================================================");
        System.out.println("Cancelling an appointment...");

        System.out.print("Enter the appointment ID to cancel: ");
        String appointmentId = scanner.nextLine();

        String query = "UPDATE appointment SET status = 'Cancelled' WHERE hash_id = ?";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, appointmentId);

            return stmt.executeUpdate() > 0; // Returns true if update succeeds
        } catch (SQLException e) {
            System.err.println("Database update error: " + e.getMessage());
            return false;
        }
    }

    private void updatePatientInfo(Scanner scanner) {
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("DB_URL");
        String dbUser = "root";
        String dbPassword = dotenv.get("DB_PASSWORD");

        System.out.println("=================================================");
        System.out.println("Updating patient information...");

        System.out.print("Enter patient username: ");
        String patientUsername = scanner.nextLine();

        // Get the hash_id using the method from PatientDoctorService
        UserDAOImpl userDAOImpl = new UserDAOImpl();
        String patientHashId = userDAOImpl.getUserId(patientUsername);

        if (patientHashId == null) {
            System.out.println("No patient found with the given username.");
            return;
        }

        boolean updating = true;
        while (updating) {
            System.out.println("Select the field you want to update:");
            System.out.println("1. Name");
            System.out.println("2. Surname");
            System.out.println("3. Gender");
            System.out.println("4. Blood Group");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            int choice = Integer.parseInt(scanner.nextLine());
            String field = null;
            String newValue = null;

            switch (choice) {
                case 1:
                    field = "name";
                    System.out.print("Enter new name: ");
                    newValue = scanner.nextLine();
                    break;
                case 2:
                    field = "surname";
                    System.out.print("Enter new surname: ");
                    newValue = scanner.nextLine();
                    break;
                case 3:
                    field = "gender";
                    System.out.print("Enter new gender(M/F/Other): ");
                    newValue = scanner.nextLine();
                    break;
                case 4:
                    field = "blood_group";
                    System.out.print("Enter new blood group: ");
                    newValue = scanner.nextLine();
                    break;
                case 0:
                    updating = false;
                    continue;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    continue;
            }

            if (field != null && newValue != null) {
                String query = "UPDATE patient SET " + field + " = ? WHERE hash_id = ?";
                try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setString(1, newValue);
                    stmt.setString(2, patientHashId);

                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("Patient " + field + " updated successfully.");
                    } else {
                        System.out.println("Failed to update patient " + field + ".");
                    }

                } catch (SQLException e) {
                    System.out.println("Error updating patient information: " + e.getMessage());
                }
            }
        }

        System.out.println("Exiting update menu.");
    }

    // private void viewAppointments() {
    // System.out.println("=================================================");
    // System.out.println("Viewing all upcoming appointments...");

    // String query = "SELECT a.hash_id, a.appointment_date, a.status,
    // a.additional_info, "
    // + "p.username AS patient_username, d.username AS doctor_username "
    // + "FROM appointment a "
    // + "JOIN users p ON a.patient_hash_id = p.hash_id "
    // + "JOIN users d ON a.doctor_hash_id = d.hash_id "
    // + "ORDER BY a.appointment_date ASC";

    // try (Connection conn = DriverManager.getConnection(url, user, password);
    // PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs =
    // stmt.executeQuery()) {

    // System.out.printf("%-15s %-20s %-15s %-15s %-15s %-30s%n",
    // "Appointment ID", "Date", "Patient", "Doctor", "Status", "Additional Info");
    // System.out.println("------------------------------------------------------------------------------------------");

    // boolean hasResults = false;
    // while (rs.next()) {
    // hasResults = true;
    // String id = rs.getString("hash_id");
    // String date = rs.getString("appointment_date");
    // String patient = rs.getString("patient_username");
    // String doctor = rs.getString("doctor_username");
    // String status = rs.getString("status");
    // String info = rs.getString("additional_info");

    // System.out.printf("%-15s %-20s %-15s %-15s %-15s %-30s%n",
    // id, date, patient, doctor, status, info != null ? info : "-");
    // }

    // if (!hasResults) {
    // System.out.println("No appointments found.");
    // }

    // } catch (SQLException e) {
    // System.err.println("SQL error: " + e.getMessage());
    // }
    // }

    private void viewPatientInfo(Scanner scanner) {
        Dotenv dotenv = Dotenv.load();
        String url = "jdbc:mysql://localhost:3306/hospital_db?useSSL=false&serverTimezone=UTC";
        String dbUser = "root";
        String dbPassword = dotenv.get("DB_PASSWORD");

        System.out.println("=================================================");
        System.out.println("Viewing patient information...");

        System.out.print("Enter patient username: ");
        String patientUsername = scanner.nextLine();

        String query = "SELECT u.hash_id, u.username, p.name, p.surname, p.gender, p.age, p.blood_group, p.date_of_birth "
                + "FROM users u JOIN patient p ON u.hash_id = p.hash_id "
                + "WHERE u.username = ?";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, patientUsername);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashId = rs.getString("hash_id");
                    String username = rs.getString("username");
                    String name = rs.getString("name");
                    String surname = rs.getString("surname");
                    String gender = rs.getString("gender");
                    int age = rs.getInt("age");
                    String bloodGroup = rs.getString("blood_group");
                    java.sql.Date dob = rs.getDate("date_of_birth");

                    System.out.println("=================================================");
                    System.out.println("Patient Information:");
                    System.out.println("-------------------------------------------------");
                    System.out.println("ID: " + hashId);
                    System.out.println("Username: " + username);
                    System.out.println("Name: " + name + " " + surname);
                    System.out.println("Gender: " + gender);
                    System.out.println("Age: " + age);
                    System.out.println("Blood Group: " + (bloodGroup != null ? bloodGroup : "N/A"));
                    System.out.println("Date of Birth: " + dob);
                    System.out.println("=================================================");
                } else {
                    System.out.println("No patient found with the given username.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving patient information: " + e.getMessage());
        }
    }

    private void viewDoctorInfo(Scanner scanner) {
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("DB_URL");
        String dbUser = "root";
        String dbPassword = dotenv.get("DB_PASSWORD");

        System.out.println("=================================================");
        System.out.println("Viewing doctor information...");

        System.out.print("Enter doctor username: ");
        String doctorUsername = scanner.nextLine();

        String query = "SELECT u.hash_id, u.username, d.name, d.surname, d.gender, d.department, d.id_number "
                + "FROM users u "
                + "JOIN doctor d ON u.hash_id = d.hash_id "
                + "WHERE u.username = ?";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, doctorUsername);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashId = rs.getString("hash_id");
                    String username = rs.getString("username");
                    String name = rs.getString("name");
                    String surname = rs.getString("surname");
                    String gender = rs.getString("gender");
                    String department = rs.getString("department");
                    String idNumber = rs.getString("id_number");

                    System.out.println("=================================================");
                    System.out.println("Doctor Information:");
                    System.out.println("-------------------------------------------------");
                    System.out.println("ID: " + hashId);
                    System.out.println("Username: " + username);
                    System.out.println("Name: " + name + " " + surname);
                    System.out.println("Gender: " + gender);
                    System.out.println("Department: " + department);
                    System.out.println("ID Number: " + idNumber);
                    System.out.println("=================================================");
                } else {
                    System.out.println("No doctor found with the given username.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving doctor information: " + e.getMessage());
        }
    }

}
