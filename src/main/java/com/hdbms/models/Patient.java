package com.hdbms.models;

public class Patient extends User {
    private String bloodGroup;
    // private String pastSurgeries;
    // private String referredBy;
    private String dateOfBirth;

    // Getters and Setters
    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    // public String getPastSurgeries() {
    //     return pastSurgeries;
    // }

    // public void setPastSurgeries(String pastSurgeries) {
    //     this.pastSurgeries = pastSurgeries;
    // }

    // public String getReferredBy() {
    //     return referredBy;
    // }

    // public void setReferredBy(String referredBy) {
    //     this.referredBy = referredBy;
    // }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

}