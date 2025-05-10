package com.hdbms.views;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import com.hdbms.DAO.UserDAOImpl;
import com.hdbms.models.Doctor;
import com.hdbms.models.Patient;
import com.hdbms.services.HashUtil;
import com.hdbms.services.PatientDoctorService;

import io.github.cdimascio.dotenv.Dotenv;

class CommonConstants {
    public static final Color PRIMARY_COLOR = Color.decode("#344E41"); // Darkest for primary background
    public static final Color SECONDARY_COLOR = Color.decode("#3A5A40"); // Darker for secondary backgrounds (e.g., input fields)
    public static final Color TEXT_COLOR = Color.decode("#DAD7CD"); // Grey/Light for text (highest contrast)
    public static final Color ACCENT_COLOR = Color.decode("#A3B18A"); // Light Green for buttons or highlights
    public static final Color DARK_ACCENT_COLOR = Color.decode("#588157"); // Regular for borders or subtle accents
}


class Form extends JFrame {

    public Form(String title) {
        super(title);
        setSize(520, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(CommonConstants.PRIMARY_COLOR); // Grey/Light for frame background
    }

}

public class Login extends Form {

    private static final String JDBC_URL = Dotenv.load().get("DB_URL");
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = Dotenv.load().get("DB_PASSWORD");
    Font customFont;

    public Login() {
        super("Login");

        try {
            // Load font from resources using classpath
            InputStream fontStream = Login.class.getResourceAsStream("/Poppins-Regular.ttf");
            if (fontStream != null) {
                Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(24f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Login.class.getResourceAsStream("/Poppins-Regular.ttf")));
                this.customFont = font;
                fontStream.close(); // Close the stream after use
            } else {
                throw new IOException("Font file not found in resources");
            }
        } catch (IOException | FontFormatException e) {
            System.out.println("Error loading font: " + e.getMessage());
            // Fallback to default font if loading fails
            this.customFont = new Font("Arial", Font.PLAIN, 24);
        }

        addGuiComponents();
        setVisible(true);
    }

    private void addGuiComponents() {
        JLabel loginLabel = new JLabel("LOGIN");
        loginLabel.setBounds(0, 25, 520, 100);
        loginLabel.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        loginLabel.setFont(customFont.deriveFont(Font.BOLD, 40f));
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(loginLabel);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(30, 150, 400, 25);
        usernameLabel.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        usernameLabel.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        add(usernameLabel);

        JTextField userField = new JTextField();
        userField.setBounds(30, 185, 450, 55);
        userField.setBackground(CommonConstants.SECONDARY_COLOR); // Light Green for input background
        userField.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        userField.setBorder(new LineBorder(CommonConstants.DARK_ACCENT_COLOR, 3)); // Darker for border
        userField.setFont(customFont.deriveFont(Font.PLAIN, 24f));
        add(userField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(30, 260, 400, 25);
        passwordLabel.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        passwordLabel.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(30, 295, 450, 55);
        passwordField.setBackground(CommonConstants.SECONDARY_COLOR); // Light Green for input background
        passwordField.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        passwordField.setBorder(new LineBorder(CommonConstants.DARK_ACCENT_COLOR, 3)); // Darker for border
        passwordField.setFont(customFont.deriveFont(Font.PLAIN, 24f));
        add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(customFont.deriveFont(Font.BOLD, 18f));
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setBackground(CommonConstants.ACCENT_COLOR); // Regular for button background
        loginButton.setForeground(CommonConstants.PRIMARY_COLOR); // Grey/Light for button text
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setContentAreaFilled(true);
        loginButton.setBounds(125, 520, 250, 50);
        // Existing action listener code remains unchanged
        add(loginButton);

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passwordField.getPassword());
            if (validateLogin(username, password)) {
                JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                UserDAOImpl userDAOImpl = new UserDAOImpl();
                String role = userDAOImpl.getUserRole(username);
                System.out.println("User role: " + role);
                if (role.equalsIgnoreCase("receptionist")) {
                    SwingUtilities.invokeLater(() -> {
                        new ReceptionistDashboard(this).setVisible(true); // Pass 'this' as the Login frame
                        this.setVisible(false); // Hide the login frame
                    });
                } else if (role.equalsIgnoreCase("doctor")) {
                    SwingUtilities.invokeLater(() -> {
                        new DoctorDashF(userDAOImpl.getUserId(username), this).setVisible(true);
                        this.setVisible(false);
                    });
                    // new DoctorDashboard().setVisible(true);
                } else if (role.equalsIgnoreCase("patient")) {
                    SwingUtilities.invokeLater(() -> {
                        new PatientDashboard(userDAOImpl.getUserId(username), this).setVisible(true); // Pass 'this' as the Login frame
                        this.setVisible(false); // Hide the login frame
                    });
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid user role!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(loginButton);

        JLabel registerLabel = new JLabel("Not a user? Register Here");
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        registerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLabel.setForeground(CommonConstants.DARK_ACCENT_COLOR); // Darker for clickable text
        registerLabel.setFont(customFont.deriveFont(Font.PLAIN, 16f));
        registerLabel.setBounds(125, 600, 250, 30);
        // Existing mouse listener code remains unchanged
        registerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new RegisterFormGUI().setVisible(true);
                dispose();
            }
        });
        add(registerLabel);
    }

    private boolean validateLogin(String username, String password) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD); PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM users WHERE username = ? AND password = ?")) {
            preparedStatement.setString(1, username);
            try {
                preparedStatement.setString(2, HashUtil.generateKey(password));
            } catch (Exception e) {
                System.out.println("Error generating key(in Login.java validateLogin method): " + e.getMessage());
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static void main(String[] args) {

    }
}

class RegisterFormGUI extends Form {

    private static final String JDBC_URL = Dotenv.load().get("DB_URL");
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = Dotenv.load().get("DB_PASSWORD");

    private JTextField userField, nameField, surnameField, dobField, bloodGroupField, departmentField, idNumberField;
    private JPasswordField passwordField;
    private JComboBox<String> genderCombo, roleCombo;
    Font customFont;

    public RegisterFormGUI() {
        super("Register");

        try {
            // Load font from resources using classpath
            InputStream fontStream = Login.class.getResourceAsStream("/Poppins-Regular.ttf");
            if (fontStream != null) {
                Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(24f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Login.class.getResourceAsStream("/Poppins-Regular.ttf")));
                this.customFont = font;
                fontStream.close(); // Close the stream after use
            } else {
                throw new IOException("Font file not found in resources");
            }
        } catch (IOException | FontFormatException e) {
            System.out.println("Error loading font: " + e.getMessage());
            // Fallback to default font if loading fails
            this.customFont = new Font("Arial", Font.PLAIN, 24);
        }

        addGuiComponents();
        setVisible(true);
    }

    private void addGuiComponents() {
        // Register Label
        JLabel registerLabel = new JLabel("Register");
        registerLabel.setBounds(0, 25, 520, 50);
        registerLabel.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        registerLabel.setFont(customFont.deriveFont(Font.BOLD, 40f));
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(registerLabel);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(30, 80, 400, 25);
        usernameLabel.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        usernameLabel.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        add(usernameLabel);

        userField = new JTextField();
        userField.setBounds(30, 105, 450, 40);
        userField.setBackground(CommonConstants.SECONDARY_COLOR); // Light Green for input background
        userField.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        userField.setBorder(new LineBorder(CommonConstants.DARK_ACCENT_COLOR, 3)); // Darker for border
        userField.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        add(userField);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(30, 150, 400, 25);
        passwordLabel.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        passwordLabel.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(30, 175, 450, 40);
        passwordField.setBackground(CommonConstants.SECONDARY_COLOR); // Light Green for input background
        passwordField.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        passwordField.setBorder(new LineBorder(CommonConstants.DARK_ACCENT_COLOR, 3)); // Darker for border
        passwordField.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        add(passwordField);

        // Name
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(30, 220, 400, 25);
        nameLabel.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        nameLabel.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(30, 245, 450, 40);
        nameField.setBackground(CommonConstants.SECONDARY_COLOR); // Light Green for input background
        nameField.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        nameField.setBorder(new LineBorder(CommonConstants.DARK_ACCENT_COLOR, 3)); // Darker for border
        nameField.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        add(nameField);

        // Surname
        JLabel surnameLabel = new JLabel("Surname:");
        surnameLabel.setBounds(30, 290, 400, 25);
        surnameLabel.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        surnameLabel.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        add(surnameLabel);

        surnameField = new JTextField();
        surnameField.setBounds(30, 315, 450, 40);
        surnameField.setBackground(CommonConstants.SECONDARY_COLOR); // Light Green for input background
        surnameField.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        surnameField.setBorder(new LineBorder(CommonConstants.DARK_ACCENT_COLOR, 3)); // Darker for border
        surnameField.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        add(surnameField);

        // Gender
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(30, 360, 400, 25);
        genderLabel.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        genderLabel.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        add(genderLabel);

        String[] genders = {"F", "M", "Other"};
        genderCombo = new JComboBox<>(genders);
        genderCombo.setBounds(30, 385, 450, 40);
        genderCombo.setBackground(CommonConstants.SECONDARY_COLOR); // Light Green for dropdown background
        genderCombo.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        genderCombo.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        add(genderCombo);

        // Role
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(30, 430, 400, 25);
        roleLabel.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        roleLabel.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        add(roleLabel);

        String[] roles = {"Patient (P)", "Doctor (D)", "Receptionist (R)"};
        roleCombo = new JComboBox<>(roles);
        roleCombo.setBounds(30, 455, 450, 40);
        roleCombo.setBackground(CommonConstants.SECONDARY_COLOR); // Light Green for dropdown background
        roleCombo.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        roleCombo.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        roleCombo.addActionListener(e -> updateRoleSpecificFields());
        add(roleCombo);

        // Role-specific fields for Patient
        JLabel dobLabel = new JLabel("Date of Birth (YYYY-MM-DD):");
        dobLabel.setBounds(30, 500, 400, 25);
        dobLabel.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        dobLabel.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        dobLabel.setVisible(false);
        add(dobLabel);

        dobField = new JTextField();
        dobField.setBounds(30, 525, 450, 40);
        dobField.setBackground(CommonConstants.SECONDARY_COLOR); // Light Green for input background
        dobField.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        dobField.setBorder(new LineBorder(CommonConstants.DARK_ACCENT_COLOR, 3)); // Darker for border
        dobField.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        dobField.setVisible(false);
        add(dobField);

        JLabel bloodGroupLabel = new JLabel("Blood Group:");
        bloodGroupLabel.setBounds(30, 570, 400, 25);
        bloodGroupLabel.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        bloodGroupLabel.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        bloodGroupLabel.setVisible(false);
        add(bloodGroupLabel);

        bloodGroupField = new JTextField();
        bloodGroupField.setBounds(30, 595, 450, 40);
        bloodGroupField.setBackground(CommonConstants.SECONDARY_COLOR); // Light Green for input background
        bloodGroupField.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        bloodGroupField.setBorder(new LineBorder(CommonConstants.DARK_ACCENT_COLOR, 3)); // Darker for border
        bloodGroupField.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        bloodGroupField.setVisible(false);
        add(bloodGroupField);

        // Role-specific fields for Doctor
        JLabel departmentLabel = new JLabel("Department:");
        departmentLabel.setBounds(30, 500, 400, 25);
        departmentLabel.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        departmentLabel.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        departmentLabel.setVisible(false);
        add(departmentLabel);

        departmentField = new JTextField();
        departmentField.setBounds(30, 525, 450, 40);
        departmentField.setBackground(CommonConstants.SECONDARY_COLOR); // Light Green for input background
        departmentField.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        departmentField.setBorder(new LineBorder(CommonConstants.DARK_ACCENT_COLOR, 3)); // Darker for border
        departmentField.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        departmentField.setVisible(false);
        add(departmentField);

        JLabel idNumberLabel = new JLabel("ID Number:");
        idNumberLabel.setBounds(30, 570, 400, 25);
        idNumberLabel.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        idNumberLabel.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        idNumberLabel.setVisible(false);
        add(idNumberLabel);

        idNumberField = new JTextField();
        idNumberField.setBounds(30, 595, 450, 40);
        idNumberField.setBackground(CommonConstants.SECONDARY_COLOR); // Light Green for input background
        idNumberField.setForeground(CommonConstants.TEXT_COLOR); // Darkest for text
        idNumberField.setBorder(new LineBorder(CommonConstants.DARK_ACCENT_COLOR, 3)); // Darker for border
        idNumberField.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        idNumberField.setVisible(false);
        add(idNumberField);

        // Register Button
        JButton registerButton = new JButton("Register");
        registerButton.setFont(customFont.deriveFont(Font.BOLD, 18f));
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerButton.setBackground(CommonConstants.ACCENT_COLOR); // Regular for button background
        registerButton.setForeground(CommonConstants.PRIMARY_COLOR); // Grey/Light for button text
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setContentAreaFilled(true);
        registerButton.setBounds(125, 650, 250, 50);

        registerButton.addActionListener(e -> {
            if (registerUser()) {
                JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                new Login().setVisible(true);
                dispose();
            }
        });
        add(registerButton);

        // Adjust frame size to accommodate more fields
        setSize(520, 750);
    }

    private void updateRoleSpecificFields() {
        String selectedRole = roleCombo.getSelectedItem().toString();
        boolean isPatient = selectedRole.startsWith("Patient");
        boolean isDoctor = selectedRole.startsWith("Doctor");

        // Show/hide Patient fields
        dobField.setVisible(isPatient);
        bloodGroupField.setVisible(isPatient);
        dobField.getParent().getComponents()[dobField.getParent().getComponentZOrder(dobField) - 1].setVisible(isPatient); // Label
        bloodGroupField.getParent().getComponents()[bloodGroupField.getParent().getComponentZOrder(bloodGroupField) - 1].setVisible(isPatient); // Label

        // Show/hide Doctor fields
        departmentField.setVisible(isDoctor);
        idNumberField.setVisible(isDoctor);
        departmentField.getParent().getComponents()[departmentField.getParent().getComponentZOrder(departmentField) - 1].setVisible(isDoctor); // Label
        idNumberField.getParent().getComponents()[idNumberField.getParent().getComponentZOrder(idNumberField) - 1].setVisible(isDoctor); // Label

        revalidate();
        repaint();
    }

    private boolean registerUser() {
        String username = userField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String gender = genderCombo.getSelectedItem().toString();
        String roleSelection = roleCombo.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        UserDAOImpl userDAOImpl = new UserDAOImpl();
        if (userDAOImpl.getUserByUsername(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different username.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String hashedPassword;
        try {
            hashedPassword = HashUtil.generateKey(password);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error hashing password: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String role;
        if (roleSelection.startsWith("Patient")) {
            role = "patient";
        } else if (roleSelection.startsWith("Doctor")) {
            role = "doctor";
        } else {
            role = "receptionist";
        }

        boolean isRegistered = false;

        if (role.equals("patient")) {
            String dob = dobField.getText().trim();
            String bloodGroup = bloodGroupField.getText().trim();

            if (dob.isEmpty() || bloodGroup.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in Date of Birth and Blood Group!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            int age;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate birthDate = LocalDate.parse(dob, formatter);
                age = Period.between(birthDate, LocalDate.now()).getYears();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid date format (use YYYY-MM-DD)!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            Patient patient = new Patient();
            patient.setUsername(username);
            patient.setPassword(hashedPassword);
            patient.setName(name);
            patient.setSurname(surname);
            patient.setGender(gender);
            patient.setDateOfBirth(dob);
            patient.setAge(age);
            patient.setBloodGroup(bloodGroup);

            userDAOImpl.saveUser(patient.createHashID(), username, hashedPassword, role);
            PatientDoctorService patientDoctorService = new PatientDoctorService();
            isRegistered = patientDoctorService.addPatient(patient.getHashID(), patient.getName(), patient.getSurname(), patient.getGender(), patient.getAge(), patient.getDateOfBirth(), patient.getBloodGroup());
        } else if (role.equals("doctor")) {
            String department = departmentField.getText().trim();
            String idNumber = idNumberField.getText().trim();

            if (department.isEmpty() || idNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in Department and ID Number!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            Doctor doctor = new Doctor();
            doctor.setUsername(username);
            doctor.setPassword(hashedPassword);
            doctor.setName(name);
            doctor.setSurname(surname);
            doctor.setGender(gender);
            doctor.setDepartment(department);
            doctor.setIdNumber(idNumber);

            userDAOImpl.saveUser(doctor.createHashID(), username, hashedPassword, role);
            PatientDoctorService patientDoctorService = new PatientDoctorService();
            isRegistered = patientDoctorService.addDoctor(doctor.getHashID(), doctor.getName(), doctor.getSurname(), doctor.getGender(), doctor.getDepartment(), doctor.getIdNumber());
        } else if (role.equals("receptionist")) {
            try {
                userDAOImpl.saveUser(HashUtil.generateKey(username), username, hashedPassword, role);
                isRegistered = true;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error registering receptionist: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        if (isRegistered) {
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "Error occurred during registration!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
