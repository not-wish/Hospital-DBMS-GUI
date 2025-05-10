package com.hdbms.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.hdbms.services.HashUtil;

public class DoctorDashF extends JFrame {
    private String doctorHashId;
    private Connection conn;
    private JTextField patientUsernameField, medicinesField, suggestedTestsField;
    private JTable appointmentTable;
    private JScrollPane tableScrollPane;
    private JButton viewAppointmentsButton, prescribeMedicineButton, logoutButton;
    Login loginFrame;

    public DoctorDashF(String doctorHashId, Login loginFrame) {
        this.doctorHashId = doctorHashId;
        this.loginFrame = loginFrame;

        // Initialize database connection
        initializeDatabase();

        // Set up the main frame
        setTitle("Doctor Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(CommonConstants.PRIMARY_COLOR); // Darkest for frame background

        // Create top panel for controls
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(CommonConstants.PRIMARY_COLOR); // Darkest for top panel background
        viewAppointmentsButton = new JButton("View Appointments");
        viewAppointmentsButton.setBackground(CommonConstants.ACCENT_COLOR); // Light Green for button background
        viewAppointmentsButton.setForeground(CommonConstants.PRIMARY_COLOR); // Darkest for button text
        viewAppointmentsButton.setBorderPainted(false);
        viewAppointmentsButton.setFocusPainted(false);
        viewAppointmentsButton.setFont(new Font("Arial", Font.PLAIN, 16));

        prescribeMedicineButton = new JButton("Prescribe Medicine");
        prescribeMedicineButton.setBackground(CommonConstants.ACCENT_COLOR); // Light Green for button background
        prescribeMedicineButton.setForeground(CommonConstants.PRIMARY_COLOR); // Darkest for button text
        prescribeMedicineButton.setBorderPainted(false);
        prescribeMedicineButton.setFocusPainted(false);
        prescribeMedicineButton.setFont(new Font("Arial", Font.PLAIN, 16));

        logoutButton = new JButton("Logout");
        logoutButton.setBackground(CommonConstants.ACCENT_COLOR); // Light Green for button background
        logoutButton.setForeground(CommonConstants.PRIMARY_COLOR); // Darkest for button text
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 16));

        topPanel.add(logoutButton);
        topPanel.add(viewAppointmentsButton);
        topPanel.add(prescribeMedicineButton);

        // Create table for appointments
        String[] columns = { "Patient Name", "Appointment Date", "Status", "Additional Info" };
        ArrayList<Object[]> data = new ArrayList<>();
        appointmentTable = new JTable(data.toArray(Object[][]::new), columns);
        appointmentTable.setFillsViewportHeight(true);
        appointmentTable.setBackground(CommonConstants.SECONDARY_COLOR); // Darker for table background
        appointmentTable.setForeground(CommonConstants.TEXT_COLOR); // Grey/Light for table text
        appointmentTable.setGridColor(CommonConstants.DARK_ACCENT_COLOR); // Regular for grid lines
        appointmentTable.getTableHeader().setBackground(CommonConstants.DARK_ACCENT_COLOR); // Regular for header background
        appointmentTable.getTableHeader().setForeground(CommonConstants.TEXT_COLOR); // Grey/Light for header text
        tableScrollPane = new JScrollPane(appointmentTable);
        tableScrollPane.setBackground(CommonConstants.PRIMARY_COLOR); // Darkest for scroll pane background

        // Add components to frame
        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Add action listeners
        viewAppointmentsButton.addActionListener(e -> viewAppointments());
        prescribeMedicineButton.addActionListener(e -> prescribeMedicine());
        logoutButton.addActionListener(e -> logout());

        // Refresh table on startup
        viewAppointments();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose(); // Close the dashboard
            loginFrame.setVisible(true); // Show the login frame again
        }
    }

    private void initializeDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/hospital_db";
            String user = "root";
            String password = "Bw31zT9CJP^SHyD04r";
            this.conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
            System.exit(1);
        }
    }

    private void viewAppointments() {
        String query = "SELECT appointment_date, patient_hash_id, status, additional_info FROM appointment WHERE doctor_hash_id = ?";
        ArrayList<Object[]> data = new ArrayList<>();
        String[] columns = { "Patient Name", "Appointment Date", "Status", "Additional Info" };

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, doctorHashId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Simulate PatientDoctorService.getPatientName (mocked for frontend)
                String patientName = getPatientName(rs.getString("patient_hash_id"));
                data.add(new Object[] {
                        patientName,
                        rs.getString("appointment_date"),
                        rs.getString("status"),
                        rs.getString("additional_info")
                });
            }
            appointmentTable.setModel(new javax.swing.table.DefaultTableModel(
                    data.toArray(Object[][]::new), columns));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching appointments: " + e.getMessage());
        }
    }

    // Mock method to simulate PatientDoctorService.getPatientName
    private String getPatientName(String patientHashId) {
        // In a real implementation, this would query the database or call
        // PatientDoctorService
        try {
            String query = "SELECT username FROM users WHERE hash_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientHashId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown Patient";
    }

    private void prescribeMedicine() {
        // Create input fields for prescription
        patientUsernameField = new JTextField(20);
        medicinesField = new JTextField(20);
        suggestedTestsField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.setBackground(CommonConstants.PRIMARY_COLOR); // Darkest for panel background
        panel.add(new JLabel("Patient Username:") {{
            setForeground(CommonConstants.TEXT_COLOR); // Grey/Light for text
        }});
        panel.add(patientUsernameField);
        panel.add(new JLabel("Medicines:") {{
            setForeground(CommonConstants.TEXT_COLOR); // Grey/Light for text
        }});
        panel.add(medicinesField);
        panel.add(new JLabel("Suggested Tests:") {{
            setForeground(CommonConstants.TEXT_COLOR); // Grey/Light for text
        }});
        panel.add(suggestedTestsField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Prescribe Medicine", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String patientUsername = patientUsernameField.getText().trim();
            String medicines = medicinesField.getText().trim();
            String suggestedTests = suggestedTestsField.getText().trim();

            // Get patient_hash_id from username (mock UserDAOImpl)
            String patientHashId = getPatientHashId(patientUsername);
            if (patientHashId == null) {
                JOptionPane.showMessageDialog(this, "Invalid patient username");
                return;
            }

            // Generate prescription_id (mock HashUtil.generateKey)
            String prescriptionId;
            try {
                prescriptionId = HashUtil.generateKey(patientHashId + doctorHashId + medicines + suggestedTests); // Simplified hash
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error generating prescription ID: " + e.getMessage());
                return;
            }
            // Insert prescription into database
            String query = "INSERT INTO prescriptions (hash_id, patient_hash_id, doctor_hash_id, medicines, suggested_tests) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, prescriptionId);
                stmt.setString(2, patientHashId);
                stmt.setString(3, doctorHashId);
                stmt.setString(4, medicines);
                stmt.setString(5, suggestedTests);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Prescription added successfully for Patient ID: " + patientHashId);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding prescription: " + e.getMessage());
            }
        }
    }

    // Mock method to simulate UserDAOImpl.getUserId
    private String getPatientHashId(String username) {
        // In a real implementation, this would query the database or call UserDAOImpl
        try {
            String query = "SELECT hash_id FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("hash_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        
    }
}
