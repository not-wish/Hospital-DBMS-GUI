package com.hdbms.DAO;

import com.hdbms.models.Patient;

public interface patientDAO {

    void addPatient(Patient patient);
    void updatePatient(Patient patient);
    void deletePatient(Patient patient);
    Patient getPatientById(int id);
    // List<Patient> getPatients();

}