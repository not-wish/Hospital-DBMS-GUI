package com.hdbms.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class PatientDashboard extends JFrame {
    private JButton btnAppointments, btnPrescriptions, btnInvoices, btnLogout;
    private JTable dataTable;
    private JScrollPane scrollPane;
    private Login loginFrame; 

    // Database credentials
    private final String DB_URL;
    private final String DB_USER = "root";
    private final String DB_PASS;
    private final String patientID;

    public PatientDashboard(String patientID, Login loginFrame) {
        this.loginFrame = loginFrame; // Store reference to Login frame
        this.DB_URL = "jdbc:mysql://localhost:3306/hospital_db?useSSL=false&serverTimezone=UTC";
        this.DB_PASS = "Bw31zT9CJP^SHyD04r";
        this.patientID = patientID;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Patient Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(CommonConstants.PRIMARY_COLOR); // Darkest for frame background

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(CommonConstants.PRIMARY_COLOR); // Darkest for button panel background
        btnAppointments = new JButton("View Appointments");
        btnAppointments.setBackground(CommonConstants.ACCENT_COLOR); // Light Green for button background
        btnAppointments.setForeground(CommonConstants.PRIMARY_COLOR); // Darkest for button text
        btnAppointments.setBorderPainted(false);
        btnAppointments.setFocusPainted(false);
        btnAppointments.setFont(new Font("Arial", Font.PLAIN, 16));

        btnPrescriptions = new JButton("View Prescriptions");
        btnPrescriptions.setBackground(CommonConstants.ACCENT_COLOR); // Light Green for button background
        btnPrescriptions.setForeground(CommonConstants.PRIMARY_COLOR); // Darkest for button text
        btnPrescriptions.setBorderPainted(false);
        btnPrescriptions.setFocusPainted(false);
        btnPrescriptions.setFont(new Font("Arial", Font.PLAIN, 16));

        btnInvoices = new JButton("View All Invoices");
        btnInvoices.setBackground(CommonConstants.ACCENT_COLOR); // Light Green for button background
        btnInvoices.setForeground(CommonConstants.PRIMARY_COLOR); // Darkest for button text
        btnInvoices.setBorderPainted(false);
        btnInvoices.setFocusPainted(false);
        btnInvoices.setFont(new Font("Arial", Font.PLAIN, 16));

        btnLogout = new JButton("Logout");
        btnLogout.setBackground(CommonConstants.ACCENT_COLOR); // Light Green for button background
        btnLogout.setForeground(CommonConstants.PRIMARY_COLOR); // Darkest for button text
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setFont(new Font("Arial", Font.PLAIN, 16));

        buttonPanel.add(btnAppointments);
        buttonPanel.add(btnPrescriptions);
        buttonPanel.add(btnInvoices);
        buttonPanel.add(btnLogout);

        // Table Panel
        dataTable = new JTable();
        dataTable.setBackground(CommonConstants.SECONDARY_COLOR); // Darker for table background
        dataTable.setForeground(CommonConstants.TEXT_COLOR); // Grey/Light for table text
        dataTable.setGridColor(CommonConstants.DARK_ACCENT_COLOR); // Regular for grid lines
        dataTable.getTableHeader().setBackground(CommonConstants.DARK_ACCENT_COLOR); // Regular for header background
        dataTable.getTableHeader().setForeground(CommonConstants.TEXT_COLOR); // Grey/Light for header text
        scrollPane = new JScrollPane(dataTable);
        scrollPane.setBackground(CommonConstants.PRIMARY_COLOR); // Darkest for scroll pane background

        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        btnAppointments.addActionListener(e -> loadData("appointments"));
        btnPrescriptions.addActionListener(e -> loadData("prescriptions"));
        btnInvoices.addActionListener(e -> loadData("invoices"));
        btnLogout.addActionListener(e -> logout());
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose(); // Close the dashboard
            loginFrame.setVisible(true); // Show the login frame again
        }
    }

    private void loadData(String tableType) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "";
            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make all cells non-editable
                }
            };

            switch (tableType) {
                case "appointments":
                    query = "SELECT appointment_date, doctor_hash_id, status, additional_info "
                          + "FROM appointment WHERE patient_hash_id = ?";
                    PreparedStatement pstAppointments = conn.prepareStatement(query);
                    pstAppointments.setString(1, patientID);
                    ResultSet rsAppointments = pstAppointments.executeQuery();

                    // Set column headers for appointments
                    model.setColumnIdentifiers(new String[]{
                        "Appointment Date", "Doctor Name", "Status", "Additional Info"
                    });

                    // Populate data with doctor name mapping
                    while (rsAppointments.next()) {
                        String doctorHashId = rsAppointments.getString("doctor_hash_id");
                        String doctorName = getDoctorName(doctorHashId); // Implement this method or hardcode for now
                        model.addRow(new Object[]{
                            rsAppointments.getString("appointment_date"),
                            doctorName,
                            rsAppointments.getString("status"),
                            rsAppointments.getString("additional_info") != null ? 
                                rsAppointments.getString("additional_info") : "N/A"
                        });
                    }
                    rsAppointments.close();
                    break;

                case "prescriptions":
                    query = "SELECT hash_id, patient_hash_id, doctor_hash_id, medicines, suggested_tests "
                          + "FROM prescriptions WHERE patient_hash_id = ?";
                    PreparedStatement pstPrescriptions = conn.prepareStatement(query);
                    pstPrescriptions.setString(1, patientID);
                    ResultSet rsPrescriptions = pstPrescriptions.executeQuery();

                    // Set column headers for prescriptions
                    model.setColumnIdentifiers(new String[]{
                        "Prescription ID", "Patient Name", "Doctor Name", "Medicines", "Suggested Tests"
                    });

                    // Populate data with patient and doctor name mapping
                    while (rsPrescriptions.next()) {
                        String patientHashId = rsPrescriptions.getString("patient_hash_id");
                        String doctorHashIdPres = rsPrescriptions.getString("doctor_hash_id");
                        String patientName = getPatientName(patientHashId); // Implement or hardcode
                        String doctorNamePres = getDoctorName(doctorHashIdPres);
                        model.addRow(new Object[]{
                            rsPrescriptions.getString("hash_id"),
                            patientName,
                            doctorNamePres,
                            rsPrescriptions.getString("medicines"),
                            rsPrescriptions.getString("suggested_tests") != null ? 
                                rsPrescriptions.getString("suggested_tests") : "N/A"
                        });
                    }
                    rsPrescriptions.close();
                    break;

                case "invoices":
                    query = "SELECT hash_id, amount, bill_date, status "
                          + "FROM bill WHERE patient_hash_id = ?";
                    PreparedStatement pstInvoices = conn.prepareStatement(query);
                    pstInvoices.setString(1, patientID);
                    ResultSet rsInvoices = pstInvoices.executeQuery();

                    // Set column headers for invoices
                    model.setColumnIdentifiers(new String[]{
                        "Bill ID", "Amount", "Bill Date", "Status"
                    });

                    // Populate data for invoices
                    while (rsInvoices.next()) {
                        model.addRow(new Object[]{
                            rsInvoices.getString("hash_id"),
                            rsInvoices.getDouble("amount"),
                            rsInvoices.getString("bill_date"),
                            rsInvoices.getString("status")
                        });
                    }
                    rsInvoices.close();
                    break;
            }

            dataTable.setModel(model);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getDoctorName(String doctorHashId) {
        // Hardcoded mapping based on search results; replace with actual DB query in production
        switch (doctorHashId) {
            case "doc123":
                return "Dr. John Smith";
            case "doc456":
                return "Dr. Alice Johnson";
            default:
                // In a real app, query the database: SELECT name, surname FROM doctor WHERE hash_id = ?
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                     PreparedStatement stmt = conn.prepareStatement(
                         "SELECT name, surname FROM doctor WHERE hash_id = ?")) {
                    stmt.setString(1, doctorHashId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        return "Dr. " + rs.getString("name") + " " + rs.getString("surname");
                    }
                } catch (SQLException ex) {
                    System.err.println("Error fetching doctor name: " + ex.getMessage());
                }
                return "Unknown Doctor (" + doctorHashId + ")";
        }
    }

    private String getPatientName(String patientHashId) {
        // Hardcoded mapping based on search results; replace with actual DB query in production
        if (patientHashId.equals("pat123")) {
            return "Jane Doe";
        }
        // In a real app, query the database: SELECT name, surname FROM patient WHERE hash_id = ?
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT name, surname FROM patient WHERE hash_id = ?")) {
            stmt.setString(1, patientHashId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name") + " " + rs.getString("surname");
            }
        } catch (SQLException ex) {
            System.err.println("Error fetching patient name: " + ex.getMessage());
        }
        return "Unknown Patient (" + patientHashId + ")";
    }
}
