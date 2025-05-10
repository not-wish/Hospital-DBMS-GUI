package com.hdbms;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.swing.SwingUtilities;

import com.hdbms.DAO.UserDAOImpl;
import com.hdbms.models.Doctor;
import com.hdbms.models.Patient;
import com.hdbms.models.User;
import com.hdbms.services.DoctorDashboard;
import com.hdbms.services.HashUtil;
import com.hdbms.services.HospitalDatabaseSetup;
import com.hdbms.services.PatientDoctorService;
import com.hdbms.views.Login;


// class HashUtil {
//     public static String generateKey(String user) throws NoSuchAlgorithmException {
//         try {
//         MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
//         byte[] hash = digest.digest(user.getBytes());

//         StringBuilder hexID = new StringBuilder();

//         for (byte b : hash) {
//             hexID.append(String.format("%02x", b));  // %02x = 2-digit hex
//         }

//         return hexID.toString();
//         } catch (NoSuchAlgorithmException e) {
//             System.out.println(e);
//         }

//         return "NULL";
//     }
// }

// class User {
//     private String username;
//     private String password;
//     private String name;
//     private String surname;
//     private int age;
//     private String gender;
//     private String hashid;

//     // Getters and Setters
//     public String getUsername() {
//         return username;
//     }

//     public void setUsername(String username) {
//         this.username = username;
//     }

//     public String getPassword() {
//         return password;
//     }

//     public void setPassword(String password) {
//         this.password = password;
//     }

//     public String getName() {
//         return name;
//     }

//     public void setName(String name) {
//         this.name = name;
//     }

//     public String getSurname() {
//         return surname;
//     }

//     public void setSurname(String surname) {
//         this.surname = surname;
//     }

//     public int getAge() {
//         return age;
//     }

//     public void setAge(int age) {
//         this.age = age;
//     }

//     public String getGender() {
//         return gender;
//     }

//     public void setGender(String gender) {
//         this.gender = gender;
//     }

//     public void setHashID(String hashid) {
//         this.hashid = hashid;
//     }

//     public String getHashID() {
//         return this.hashid;
//     }

//     // create and set hash id
//     public void createHashID() {
//         try {
//         setHashID(HashUtil.generateKey(getUsername()));
        
//         } catch (NoSuchAlgorithmException e) {
//             System.out.println("Encountered No Such Algorithm Exception");
//             System.out.println(e);
//         }
//     }
// }

// class Patient extends User {
//     private String bloodGroup;
//     private String pastSurgeries;
//     private String referredBy;
//     private String dateOfBirth;

//     // Getters and Setters
//     public String getBloodGroup() {
//         return bloodGroup;
//     }

//     public void setBloodGroup(String bloodGroup) {
//         this.bloodGroup = bloodGroup;
//     }

//     public String getPastSurgeries() {
//         return pastSurgeries;
//     }

//     public void setPastSurgeries(String pastSurgeries) {
//         this.pastSurgeries = pastSurgeries;
//     }

//     public String getReferredBy() {
//         return referredBy;
//     }

//     public void setReferredBy(String referredBy) {
//         this.referredBy = referredBy;
//     }

//     public String getDateOfBirth() {
//         return dateOfBirth;
//     }

//     public void setDateOfBirth(String dateOfBirth) {
//         this.dateOfBirth = dateOfBirth;
//     }

// }

// class Doctor extends User {
//     private String department;
//     private String idNumber;
//     private String dateOfJoining;

//     // Getters and Setters
//     public String getDepartment() {
//         return department;
//     }

//     public void setDepartment(String department) {
//         this.department = department;
//     }

//     public String getIdNumber() {
//         return idNumber;
//     }

//     public void setIdNumber(String idNumber) {
//         this.idNumber = idNumber;
//     }

//     public String getDateOfJoining() {
//         return dateOfJoining;
//     }

//     public void setDateOfJoining(String dateOfJoining) {
//         this.dateOfJoining = dateOfJoining;
//     }
// }

public class app {
    static HashMap<String, User> userDatabase = new HashMap<>();


    public static void main(String[] args) {
        // SwingUtilities.invokeLater(() -> {
        //     new PatientDashboard("262cc47030b1803064844b94c1cb0054a247d1e550e26bb33f215149d8b2c72e").setVisible(true);
        // });

        SwingUtilities.invokeLater(Login::new);

        

        HospitalDatabaseSetup.runSetup(args);

        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        do {

            System.out.println("=================================================");
            System.out.println("\nWelcome to the Hospital DBMS!\n");
            System.out.println("Please choose an option:");
            System.out.println("=================================================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            try {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // Clear the newline character
                
                    switch (choice) {
                        case 1:
                            login(scanner);
                            break;
                        case 2:
                            register(scanner);
                            break;
                        case 3:
                            System.out.println("Thank you for using the Hospital DBMS!");
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number (1, 2, or 3).");
                scanner.nextLine(); // Clear the invalid input
                choice = -1; // Reset to keep the loop running
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number (1, 2, or 3).");
                scanner.nextLine(); // Clear the invalid input
                choice = -1; // Reset to keep the loop running
            }
        } while (choice != 3);

        scanner.close();
    }

    public static void login(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = null;
        try {
            password = HashUtil.generateKey(scanner.nextLine());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("SHA-256 Algorithm not found");
            System.out.println(e);
        } catch (Exception e) {
            System.out.println("Some error occured creating paassword or hashing password");
            System.out.println(e);
        }
        System.out.println("=================================================");
        System.out.println("Processing login...");
        System.out.println("=================================================");

        UserDAOImpl userDAOImpl = new UserDAOImpl();

        if ( userDAOImpl.validateCredentials(username, password) ) {// userDatabase.containsKey(username) && userDatabase.get(username).getPassword().equals(password)) {
            System.out.println("Login successful! Welcome, " + username + ".");
            // Run the dashboard for that role
            String role = userDAOImpl.getUserRole(username);
            if (role.equalsIgnoreCase("patient")) {
                // System.out.println("You are logged in as a Patient.");
                // PatientDashboard patientDashboard = new PatientDashboard(userDAOImpl.getUserId(username), scanner);
                // scanner.nextLine(); // Consume the newline character
                
            } else if (role.equalsIgnoreCase("doctor")) {
                // System.out.println("You are logged in as a Doctor.");
                // Call the doctor dashboard or service
                DoctorDashboard doctorDashboard = new DoctorDashboard(userDAOImpl.getUserId(username), scanner);
                // scanner.nextLine(); // Consume the newline character
            } else if (role.equalsIgnoreCase("receptionist")) {
                // System.out.println("You are logged in as a Receptionist.");
                // Call the receptionist dashboard or service
                // ReceptionistDashboard receptionistDashboard = new ReceptionistDashboard(scanner);
                // scanner.nextLine(); // Consume the newline character
            } else {
                System.out.println("Unknown role. Please contact support.");
            }
        } else {
            System.out.println("Invalid username or password. Please try again.");
        }
    }

    public static void register(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        UserDAOImpl userDAOImpl = new UserDAOImpl();

        if (userDatabase.containsKey(username) || userDAOImpl.getUserByUsername(username)) {
            // Check if the username already exists in the database
            System.out.println("Username already exists. Please choose a different username.");
            return;
        }

        System.out.print("Enter password: ");
        String password = null;
        try {
            password = HashUtil.generateKey(scanner.nextLine());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("SHA-256 Algorithm not found");
            System.out.println(e);
        } catch (Exception e) {
            System.out.println("Some error occured creating paassword or hashing password");
            System.out.println(e);
        }
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter surname: ");
        String surname = scanner.nextLine();
        String gender;
        do {
            System.out.print("Enter gender (F/M/Other): ");
            gender = scanner.nextLine();
            if (!gender.equals("F") && !gender.equals("M") && !gender.equals("Other")) {
                System.out.println("Invalid gender. Please enter F, M, or Other exactly.");
            }
        } while (!gender.equals("F") && !gender.equals("M") && !gender.equals("Other"));

        System.out.print("Are you a Patient or Doctor or Receptionist? (P/D/R): ");
        String role = scanner.nextLine();
        if (!role.equalsIgnoreCase("P") && !role.equalsIgnoreCase("D") && !role.equalsIgnoreCase("R")) {
            System.out.println("Invalid role. Please enter P for Patient or D for Doctor or R for Receptionist.");
            return;
        }

        boolean isRegistered = false;

        if (role.equalsIgnoreCase("P")) {
            // Create a new Patient object and set its properties
            Patient patient = new Patient();
            patient.setUsername(username);
            patient.setPassword(password);
            patient.setName(name);
            patient.setSurname(surname);
            patient.setGender(gender);
            userDAOImpl.saveUser(patient.createHashID(), username, password, "patient");
            

            System.out.print("Enter date of birth (YYYY-MM-DD): ");
            String dob = scanner.nextLine();

            // Calculate age from DOB
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate birthDate = LocalDate.parse(dob, formatter);
                int age = Period.between(birthDate, LocalDate.now()).getYears();
                patient.setDateOfBirth(dob);
                patient.setAge(age);
                System.out.println("Patient's age is: " + age);
            } catch (Exception e) {
                System.out.println("Invalid date format. Registration failed.");
                return;
            }

            System.out.print("Enter blood group: ");
            patient.setBloodGroup(scanner.nextLine());

            PatientDoctorService patientDoctorService = new PatientDoctorService();

            isRegistered = patientDoctorService.addPatient(patient.getHashID(), patient.getName(), patient.getSurname(), patient.getGender(), patient.getAge(), patient.getDateOfBirth(), patient.getBloodGroup());   
            // System.out.print("Enter past surgeries (if any): ");
            // patient.setPastSurgeries(scanner.nextLine());
            // System.out.print("Enter referred by: ");
            // patient.setReferredBy(scanner.nextLine());

            userDatabase.put(username, patient);
        } else if (role.equalsIgnoreCase("D")) {
            // Create a new Doctor object and set its properties
            Doctor doctor = new Doctor();
            doctor.setUsername(username);
            doctor.setPassword(password);
            doctor.setName(name);
            doctor.setSurname(surname);
            doctor.setGender(gender);
            userDAOImpl.saveUser(doctor.createHashID(), username, password, "doctor");
            

            System.out.print("Enter department: ");
            doctor.setDepartment(scanner.nextLine());
            System.out.print("Enter ID number: ");
            doctor.setIdNumber(scanner.nextLine());
            // System.out.print("Enter date of joining: ");
            // doctor.setDateOfJoining(scanner.nextLine());

            // isRegistered = false; // Assuming you don't need to save doctor in the database for now
            // You can implement similar logic for doctors as you did for patients

            userDatabase.put(username, doctor);
            PatientDoctorService patientDoctorService = new PatientDoctorService();

            isRegistered = patientDoctorService.addDoctor(doctor.getHashID(), doctor.getName(), doctor.getSurname(), doctor.getGender(), doctor.getDepartment(), doctor.getIdNumber());
        } else if (role.equalsIgnoreCase("R")) {
            // Create a new Receptionist object and set its properties
            // Receptionist receptionist = new Receptionist();
            // receptionist.setUsername(username);
            // receptionist.setPassword(password);
            // receptionist.setName(name);
            // receptionist.setSurname(surname);
            // receptionist.set
            try {
                userDAOImpl.saveUser(HashUtil.generateKey(username), username, password, "receptionist");
                isRegistered = true;
            } catch (NoSuchAlgorithmException e) {
                System.out.println("Error generating key for receptionist: " + e.getMessage());
                return;
            }


            }

        if (isRegistered == true) {
            System.out.println("You are registered successfully!");
        } else {
            System.out.println("Error occurred registering the user");
        }
        System.out.print("Do you want to proceed for login? (Yes/No): ");
        String proceedForLogin = scanner.nextLine();
        if (proceedForLogin.equalsIgnoreCase("Yes")) {
            System.out.println("=================================================");
            System.out.println("Proceeding for login...");
            login(scanner);
        } 
    }
}
