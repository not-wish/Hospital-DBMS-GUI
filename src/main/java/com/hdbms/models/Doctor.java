package com.hdbms.models;

public class Doctor extends User {
    private String department;
    private String idNumber;
    // private String dateOfJoining;

    // Getters and Setters
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    // public String getDateOfJoining() {
    //     return dateOfJoining;
    // }

    // public void setDateOfJoining(String dateOfJoining) {
    //     this.dateOfJoining = dateOfJoining;
    // }
}