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
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class ReceptionistDashboard extends JFrame {

    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JPanel actionPanel;
    private Login loginFrame;

    public ReceptionistDashboard(Login loginFrame) {
        this.loginFrame = loginFrame;
        // Set up the frame

        setTitle("Receptionist Dashboard");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(CommonConstants.PRIMARY_COLOR); // Darkest for frame background

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(CommonConstants.PRIMARY_COLOR); // Darkest for main panel background

        // Create header
        JLabel headerLabel = new JLabel("Receptionist Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(CommonConstants.TEXT_COLOR); // Grey/Light for text
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        buttonPanel.setBackground(CommonConstants.PRIMARY_COLOR); // Darkest for button panel background

        // Create buttons
        JButton viewAppointmentsButton = new JButton("View Appointments");
        viewAppointmentsButton.setFont(new Font("Arial", Font.PLAIN, 16));
        viewAppointmentsButton.setBackground(CommonConstants.ACCENT_COLOR); // Light Green for button background
        viewAppointmentsButton.setForeground(CommonConstants.PRIMARY_COLOR); // Darkest for button text (high contrast)
        viewAppointmentsButton.setBorderPainted(false);
        viewAppointmentsButton.setFocusPainted(false);

        JButton viewPatientInfoButton = new JButton("View Patient Info");
        viewPatientInfoButton.setFont(new Font("Arial", Font.PLAIN, 16));
        viewPatientInfoButton.setBackground(CommonConstants.ACCENT_COLOR); // Light Green for button background
        viewPatientInfoButton.setForeground(CommonConstants.PRIMARY_COLOR); // Darkest for button text
        viewPatientInfoButton.setBorderPainted(false);
        viewPatientInfoButton.setFocusPainted(false);

        JButton viewDoctorInfoButton = new JButton("View Doctor Info");
        viewDoctorInfoButton.setFont(new Font("Arial", Font.PLAIN, 16));
        viewDoctorInfoButton.setBackground(CommonConstants.ACCENT_COLOR); // Light Green for button background
        viewDoctorInfoButton.setForeground(CommonConstants.PRIMARY_COLOR); // Darkest for button text
        viewDoctorInfoButton.setBorderPainted(false);
        viewDoctorInfoButton.setFocusPainted(false);

        JButton checkBillingStatusButton = new JButton("Check Billing Status");
        checkBillingStatusButton.setFont(new Font("Arial", Font.PLAIN, 16));
        checkBillingStatusButton.setBackground(CommonConstants.ACCENT_COLOR); // Light Green for button background
        checkBillingStatusButton.setForeground(CommonConstants.PRIMARY_COLOR); // Darkest for button text
        checkBillingStatusButton.setBorderPainted(false);
        checkBillingStatusButton.setFocusPainted(false);

        JButton scheduleAppointmentButton = new JButton("Schedule Appointment");
        scheduleAppointmentButton.setFont(new Font("Arial", Font.PLAIN, 16));
        scheduleAppointmentButton.setBackground(CommonConstants.ACCENT_COLOR); // Light Green for button background
        scheduleAppointmentButton.setForeground(CommonConstants.PRIMARY_COLOR); // Darkest for button text
        scheduleAppointmentButton.setBorderPainted(false);
        scheduleAppointmentButton.setFocusPainted(false);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 16));
        logoutButton.setBackground(CommonConstants.ACCENT_COLOR); // Light Green for button background
        logoutButton.setForeground(CommonConstants.PRIMARY_COLOR); // Darkest for button text
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        

        // Create table
        tableModel = new DefaultTableModel()  {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        // Create table
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        dataTable = new JTable(tableModel);
        dataTable.setBackground(CommonConstants.SECONDARY_COLOR); // Darker for table background
        dataTable.setForeground(CommonConstants.TEXT_COLOR); // Grey/Light for table text
        dataTable.setGridColor(CommonConstants.DARK_ACCENT_COLOR); // Regular for grid lines
        dataTable.getTableHeader().setBackground(CommonConstants.DARK_ACCENT_COLOR); // Regular for header background
        dataTable.getTableHeader().setForeground(CommonConstants.TEXT_COLOR); // Grey/Light for header text
        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(CommonConstants.DARK_ACCENT_COLOR), // Regular for border
            "Data",
            0,
            0,
            new Font("Arial", Font.BOLD, 14),
            CommonConstants.TEXT_COLOR // Grey/Light for title text
        ));
        scrollPane.setBackground(CommonConstants.PRIMARY_COLOR); // Darkest for scroll pane background

        actionPanel = new JPanel(new FlowLayout());
        actionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(CommonConstants.DARK_ACCENT_COLOR), // Regular for border
            "Actions",
            0,
            0,
            new Font("Arial", Font.BOLD, 14),
            CommonConstants.TEXT_COLOR // Grey/Light for title text
        ));
        actionPanel.setBackground(CommonConstants.PRIMARY_COLOR); // Darkest for action panel background

        // Add action listeners
        viewAppointmentsButton.addActionListener(e -> displayAppointments());
        viewPatientInfoButton.addActionListener(e -> displayPatientInfo());
        viewDoctorInfoButton.addActionListener(e -> displayDoctorInfo());
        checkBillingStatusButton.addActionListener(e -> checkBillingStatus());
        scheduleAppointmentButton.addActionListener(e -> scheduleAppointment());
        logoutButton.addActionListener(e -> logout());

        // Add buttons to button panel
        buttonPanel.add(viewAppointmentsButton);
        buttonPanel.add(viewPatientInfoButton);
        buttonPanel.add(viewDoctorInfoButton);
        buttonPanel.add(checkBillingStatusButton);
        buttonPanel.add(scheduleAppointmentButton); // You may need to update GridLayout to accommodate more buttons, e.g., GridLayout(3, 2, 20, 20)
        buttonPanel.add(logoutButton); // You may need to update GridLayout to accommodate more buttons, e.g., GridLayout(3, 2, 20, 20)

        // Add components to main panel
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose(); // Close the dashboard
            loginFrame.setVisible(true); // Show the login frame again
        }
    }

    private void displayAppointments() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            String query = "SELECT a.hash_id AS appointment_id, "
                    + "u1.username AS patient_username, "
                    + "u2.username AS doctor_username, "
                    + "a.appointment_date, "
                    + "a.status, "
                    + "a.additional_info "
                    + "FROM appointment a "
                    + "JOIN users u1 ON a.patient_hash_id = u1.hash_id "
                    + "JOIN users u2 ON a.doctor_hash_id = u2.hash_id";

            ResultSet rs = stmt.executeQuery(query);

            // Configure table columns
            tableModel.setColumnIdentifiers(new String[]{
                "Appointment ID",
                "Patient Username",
                "Doctor Username",
                "Appointment Date",
                "Status",
                "Additional Info"
            });
            tableModel.setRowCount(0);

            // Populate table data
            while (rs.next()) {
                String additionalInfo = rs.getString("additional_info");
                tableModel.addRow(new Object[]{
                    rs.getString("appointment_id"),
                    rs.getString("patient_username"),
                    rs.getString("doctor_username"),
                    rs.getTimestamp("appointment_date"),
                    rs.getString("status"),
                    additionalInfo != null ? additionalInfo : "N/A"
                });
            }

            rs.close();

            // Clear action panel
            actionPanel.removeAll();
            actionPanel.revalidate();
            actionPanel.repaint();

        } catch (SQLException ex) {
            showError("Error fetching appointments: " + ex.getMessage());
        }
    }

    private void displayPatientInfo() {
        // Prompt for patient username
        String username = JOptionPane.showInputDialog(
                this,
                "Enter patient username:",
                "Search Patient",
                JOptionPane.QUESTION_MESSAGE
        );
        if (username == null || username.trim().isEmpty()) {
            return; // Cancelled or empty input
        }

        String query = "SELECT u.hash_id, u.username, p.name, p.surname, p.gender, p.age, p.blood_group, p.date_of_birth "
                + "FROM users u JOIN patient p ON u.hash_id = p.hash_id "
                + "WHERE u.username = ?";

        try (
                Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username.trim());
            ResultSet rs = stmt.executeQuery();

            // Set table columns
            tableModel.setColumnIdentifiers(new String[]{
                "ID", "Username", "Name", "Surname", "Gender", "Age", "Blood Group", "Date of Birth"
            });
            tableModel.setRowCount(0);

            if (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("hash_id"),
                    rs.getString("username"),
                    rs.getString("name"),
                    rs.getString("surname"),
                    rs.getString("gender"),
                    rs.getInt("age"),
                    rs.getString("blood_group"),
                    rs.getDate("date_of_birth")
                });
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No patient found with the given username.",
                        "Not Found",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            rs.close();

            // Optional: Add update button if patient found
            actionPanel.removeAll();
            if (tableModel.getRowCount() > 0) {
                JButton updatePatientButton = new JButton("Update Patient Info");
                updatePatientButton.setBackground(CommonConstants.ACCENT_COLOR); // Light Green for button background
                updatePatientButton.setForeground(CommonConstants.PRIMARY_COLOR); // Darkest for button text
                updatePatientButton.setBorderPainted(false);
                updatePatientButton.setFocusPainted(false);
                updatePatientButton.addActionListener(e -> updatePatientInfo());
                actionPanel.add(updatePatientButton);
            }
            actionPanel.revalidate();
            actionPanel.repaint();
        } catch (SQLException ex) {
            showError("Error retrieving patient information: " + ex.getMessage());
        }
    }

    private void displayDoctorInfo() {
        // Prompt for doctor username
        String username = JOptionPane.showInputDialog(
                this,
                "Enter doctor username:",
                "Search Doctor",
                JOptionPane.QUESTION_MESSAGE
        );
        if (username == null || username.trim().isEmpty()) {
            return; // Cancelled or empty input
        }

        String query = "SELECT u.hash_id, u.username, d.name, d.surname, d.gender, d.department, d.id_number "
                + "FROM users u JOIN doctor d ON u.hash_id = d.hash_id "
                + "WHERE u.username = ?";

        try (
                Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username.trim());
            ResultSet rs = stmt.executeQuery();

            // Set table columns
            tableModel.setColumnIdentifiers(new String[]{
                "ID", "Username", "Name", "Surname", "Gender", "Department", "ID Number"
            });
            tableModel.setRowCount(0);

            if (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("hash_id"),
                    rs.getString("username"),
                    rs.getString("name"),
                    rs.getString("surname"),
                    rs.getString("gender"),
                    rs.getString("department"),
                    rs.getString("id_number")
                });
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No doctor found with the given username.",
                        "Not Found",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            rs.close();

            actionPanel.removeAll();
            if (tableModel.getRowCount() > 0) {
                JButton updateDoctorButton = new JButton("Update Doctor Info");
                updateDoctorButton.setBackground(CommonConstants.ACCENT_COLOR); // Light Green for button background
                updateDoctorButton.setForeground(CommonConstants.PRIMARY_COLOR); // Darkest for button text
                updateDoctorButton.setBorderPainted(false);
                updateDoctorButton.setFocusPainted(false);
                updateDoctorButton.addActionListener(e -> updateDoctorInfo());
                actionPanel.add(updateDoctorButton);
            }
            actionPanel.revalidate();
            actionPanel.repaint();

        } catch (SQLException ex) {
            showError("Error retrieving doctor information: " + ex.getMessage());
        }
    }

    private void checkBillingStatus() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            String query = "SELECT b.hash_id, u.username AS patient_username, b.amount, b.status, b.bill_date "
                    + "FROM bill b "
                    + "JOIN users u ON b.patient_hash_id = u.hash_id";

            ResultSet rs = stmt.executeQuery(query);

            tableModel.setColumnIdentifiers(new String[]{"Bill ID", "Patient Username", "Amount", "Status", "Date"});
            tableModel.setRowCount(0);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("hash_id"),
                    rs.getString("patient_username"),
                    rs.getDouble("amount"),
                    rs.getString("status"),
                    rs.getDate("bill_date")
                });
            }

            rs.close();

            // Add generate bill button
            actionPanel.removeAll();
            JButton generateBillButton = new JButton("Generate Bill");
            generateBillButton.setBackground(CommonConstants.ACCENT_COLOR); // Light Green for button background
            generateBillButton.setForeground(CommonConstants.PRIMARY_COLOR); // Darkest for button text
            generateBillButton.setBorderPainted(false);
            generateBillButton.setFocusPainted(false);
            generateBillButton.addActionListener(e -> generateBill());
            actionPanel.add(generateBillButton);
            actionPanel.revalidate();
            actionPanel.repaint();

        } catch (SQLException ex) {
            showError("Error fetching billing status: " + ex.getMessage());
        }
    }

    private void updatePatientInfo() {
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow >= 0) {
            String patientId = tableModel.getValueAt(selectedRow, 0).toString();
            JTextField nameField = new JTextField(tableModel.getValueAt(selectedRow, 2).toString());
            JTextField surnameField = new JTextField(tableModel.getValueAt(selectedRow, 3).toString());
            JTextField genderField = new JTextField(tableModel.getValueAt(selectedRow, 4).toString());
            JTextField bloodGroupField = new JTextField(tableModel.getValueAt(selectedRow, 6).toString());

            JPanel panel = new JPanel(new GridLayout(4, 2));
            panel.setBackground(CommonConstants.PRIMARY_COLOR); // Darkest for panel background
            panel.add(new JLabel("Name:") {{
                setForeground(CommonConstants.TEXT_COLOR); // Grey/Light for text
            }});
            panel.add(nameField);
            panel.add(new JLabel("Surname:") {{
                setForeground(CommonConstants.TEXT_COLOR); // Grey/Light for text
            }});
            panel.add(surnameField);
            panel.add(new JLabel("Gender:") {{
                setForeground(CommonConstants.TEXT_COLOR); // Grey/Light for text
            }});
            panel.add(genderField);
            panel.add(new JLabel("Blood Group:") {{
                setForeground(CommonConstants.TEXT_COLOR); // Grey/Light for text
            }});
            panel.add(bloodGroupField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Update Patient Info", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    Connection conn = getConnection();
                    String sql = "UPDATE patient SET name = ?, surname = ?, gender = ?, blood_group = ? WHERE hash_id = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, nameField.getText());
                    pstmt.setString(2, surnameField.getText());
                    pstmt.setString(3, genderField.getText());
                    pstmt.setString(4, bloodGroupField.getText());
                    pstmt.setString(5, patientId);
                    pstmt.executeUpdate();
                    pstmt.close();
                    conn.close();
                    JOptionPane.showMessageDialog(this, "Patient info updated successfully!");
                    displayPatientInfo(); // Refresh table
                } catch (SQLException ex) {
                    showError("Error updating patient info: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a patient to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateDoctorInfo() {
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow >= 0) {
            String doctorId = tableModel.getValueAt(selectedRow, 0).toString();
            JTextField nameField = new JTextField(tableModel.getValueAt(selectedRow, 2).toString());
            JTextField surnameField = new JTextField(tableModel.getValueAt(selectedRow, 3).toString());
            JTextField genderField = new JTextField(tableModel.getValueAt(selectedRow, 4).toString());
            JTextField departmentField = new JTextField(tableModel.getValueAt(selectedRow, 5).toString());
            JTextField idNumberField = new JTextField(tableModel.getValueAt(selectedRow, 6).toString());

            JPanel panel = new JPanel(new GridLayout(5, 2));
            panel.setBackground(CommonConstants.PRIMARY_COLOR); // Darkest for panel background
            panel.add(new JLabel("Name:") {{
                setForeground(CommonConstants.TEXT_COLOR); // Grey/Light for text
            }});
            panel.add(nameField);
            panel.add(new JLabel("Surname:") {{
                setForeground(CommonConstants.TEXT_COLOR); // Grey/Light for text
            }});
            panel.add(surnameField);
            panel.add(new JLabel("Gender:") {{
                setForeground(CommonConstants.TEXT_COLOR); // Grey/Light for text
            }});
            panel.add(genderField);
            panel.add(new JLabel("Department:") {{
                setForeground(CommonConstants.TEXT_COLOR); // Grey/Light for text
            }});
            panel.add(departmentField);
            panel.add(new JLabel("ID Number:") {{
                setForeground(CommonConstants.TEXT_COLOR); // Grey/Light for text
            }});
            panel.add(idNumberField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Update Doctor Info", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    Connection conn = getConnection();
                    String sql = "UPDATE doctor SET name = ?, surname = ?, gender = ?, department = ?, id_number = ? WHERE hash_id = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, nameField.getText());
                    pstmt.setString(2, surnameField.getText());
                    pstmt.setString(3, genderField.getText());
                    pstmt.setString(4, departmentField.getText());
                    pstmt.setString(5, idNumberField.getText());
                    pstmt.setString(6, doctorId);
                    pstmt.executeUpdate();
                    pstmt.close();
                    conn.close();
                    JOptionPane.showMessageDialog(this, "Doctor info updated successfully!");
                    displayDoctorInfo(); // Refresh table
                } catch (SQLException ex) {
                    showError("Error updating doctor info: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a doctor to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void scheduleAppointment() {
        // Step 1: Get patient username
        String patientUsername = JOptionPane.showInputDialog(
                this,
                "Enter patient username:",
                "Schedule Appointment - Patient",
                JOptionPane.QUESTION_MESSAGE
        );
        if (patientUsername == null || patientUsername.trim().isEmpty()) {
            return; // Cancelled or empty input
        }

        // Step 2: Get doctor username
        String doctorUsername = JOptionPane.showInputDialog(
                this,
                "Enter doctor username:",
                "Schedule Appointment - Doctor",
                JOptionPane.QUESTION_MESSAGE
        );
        if (doctorUsername == null || doctorUsername.trim().isEmpty()) {
            return; // Cancelled or empty input
        }

        // Step 3: Get appointment date and time
        String datetimeInput = JOptionPane.showInputDialog(
                this,
                "Enter appointment date and time (yyyy-MM-dd HH:mm:ss):",
                "Schedule Appointment - Date/Time",
                JOptionPane.QUESTION_MESSAGE
        );
        if (datetimeInput == null || datetimeInput.trim().isEmpty()) {
            return; // Cancelled or empty input
        }
        // Basic validation for datetime format
        String datetimeRegex = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
        if (!datetimeInput.matches(datetimeRegex)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid date format. Please use yyyy-MM-dd HH:mm:ss.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Step 4: Get status
        String status = JOptionPane.showInputDialog(
                this,
                "Enter status (Scheduled, Completed, Cancelled):",
                "Schedule Appointment - Status",
                JOptionPane.QUESTION_MESSAGE
        );
        if (status == null || status.trim().isEmpty()) {
            return; // Cancelled or empty input
        }

        // Step 5: Get additional info (optional)
        String additionalInfo = JOptionPane.showInputDialog(
                this,
                "Enter any additional info (optional):",
                "Schedule Appointment - Additional Info",
                JOptionPane.QUESTION_MESSAGE
        );
        if (additionalInfo == null) {
            additionalInfo = ""; // Treat null as empty string if cancelled
        }

        // Step 6: Get billing details
        String amountStr = JOptionPane.showInputDialog(
                this,
                "Enter billing amount:",
                "Schedule Appointment - Billing Amount",
                JOptionPane.QUESTION_MESSAGE
        );
        if (amountStr == null || amountStr.trim().isEmpty()) {
            return; // Cancelled or empty input
        }
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid amount format. Please enter a valid number.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String billAdditionalInfo = JOptionPane.showInputDialog(
                this,
                "Enter billing additional info (optional):",
                "Schedule Appointment - Billing Info",
                JOptionPane.QUESTION_MESSAGE
        );
        if (billAdditionalInfo == null) {
            billAdditionalInfo = ""; // Treat null as empty string if cancelled
        }

        String billStatus = JOptionPane.showInputDialog(
                this,
                "Enter billing status (Paid, Pending):",
                "Schedule Appointment - Billing Status",
                JOptionPane.QUESTION_MESSAGE
        );
        if (billStatus == null || billStatus.trim().isEmpty()) {
            return; // Cancelled or empty input
        }

        // Step 7: Generate hash IDs (simplified, assuming HashUtil is not available in frontend)
        String appointmentHashId = java.util.UUID.randomUUID().toString(); // Simple unique ID
        String billHashId = java.util.UUID.randomUUID().toString(); // Simple unique ID

        // Step 8: Generate bill first
        boolean billGenerated = generateBillForAppointment(
                billHashId,
                appointmentHashId,
                patientUsername,
                datetimeInput,
                amount,
                billStatus,
                billAdditionalInfo
        );

        if (!billGenerated) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to generate bill. Appointment not scheduled.",
                    "Billing Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Step 9: Check billing status (only proceed if Paid)
        boolean isPaid = checkBillingStatus(billHashId);
        if (!isPaid) {
            JOptionPane.showMessageDialog(
                    this,
                    "Payment pending! Please complete billing first.",
                    "Payment Pending",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Step 10: Schedule the appointment if payment is confirmed
        String query = "INSERT INTO appointment (hash_id, patient_hash_id, doctor_hash_id, appointment_date, status, additional_info) "
                + "VALUES (?, (SELECT hash_id FROM users WHERE username = ?), (SELECT hash_id FROM users WHERE username = ?), ?, ?, ?)";

        try (
                Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, appointmentHashId);
            stmt.setString(2, patientUsername);
            stmt.setString(3, doctorUsername);
            stmt.setString(4, datetimeInput);
            stmt.setString(5, status);
            stmt.setString(6, additionalInfo.isEmpty() ? null : additionalInfo);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Appointment scheduled successfully with ID: " + appointmentHashId,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
                displayAppointments(); // Refresh the appointments table
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to schedule appointment.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (SQLException ex) {
            showError("Error scheduling appointment: " + ex.getMessage());
        }
    }

    private void generateBill() {
        JTextField patientUsernameField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField statusField = new JTextField("Pending");
        JTextField additionalInfoField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Patient Username:"));
        panel.add(patientUsernameField);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Status (Paid/Pending):"));
        panel.add(statusField);
        panel.add(new JLabel("Additional Info (optional):"));
        panel.add(additionalInfoField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Generate Bill", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String billHashId = java.util.UUID.randomUUID().toString(); // Simple unique ID
                Connection conn = getConnection();
                String sql = "INSERT INTO bill (hash_id, patient_hash_id, amount, bill_date, status, additional_info) "
                        + "VALUES (?, (SELECT hash_id FROM users WHERE username = ?), ?, CURDATE(), ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, billHashId);
                pstmt.setString(2, patientUsernameField.getText());
                pstmt.setDouble(3, Double.parseDouble(amountField.getText()));
                pstmt.setString(4, statusField.getText());
                pstmt.setString(5, additionalInfoField.getText().isEmpty() ? null : additionalInfoField.getText());
                pstmt.executeUpdate();
                pstmt.close();
                conn.close();
                JOptionPane.showMessageDialog(this, "Bill generated successfully!");
                checkBillingStatus(); // Refresh table
            } catch (SQLException | NumberFormatException ex) {
                showError("Error generating bill: " + ex.getMessage());
            }
        }
    }

    private boolean generateBillForAppointment(String billHashId, String appointmentHashId, String patientUsername, String billDate, Double amount, String status, String additionalInfo) {
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO bill (hash_id, patient_hash_id, appointment_hash_id, amount, bill_date, status, additional_info) "
                + "VALUES (?, (SELECT hash_id FROM users WHERE username = ?), ?, ?, ?, ?, ?)"
        )) {
            stmt.setString(1, billHashId);
            stmt.setString(2, patientUsername);
            stmt.setString(3, appointmentHashId);
            stmt.setDouble(4, amount);
            stmt.setString(5, billDate);
            stmt.setString(6, status);
            stmt.setString(7, additionalInfo.isEmpty() ? null : additionalInfo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            showError("Error generating bill for appointment: " + ex.getMessage());
            return false;
        }
    }

    private boolean checkBillingStatus(String billHashId) {
        String query = "SELECT status FROM bill WHERE hash_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, billHashId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                return "Paid".equalsIgnoreCase(status);
            }
            return false;
        } catch (SQLException ex) {
            showError("Error checking billing status: " + ex.getMessage());
            return false;
        }
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/hospital_db";
        String user = "root";
        String password = "Bw31zT9CJP^SHyD04r";
        return DriverManager.getConnection(url, user, password);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
    }
}
