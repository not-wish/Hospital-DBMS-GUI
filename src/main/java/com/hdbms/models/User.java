package com.hdbms.models;

import java.security.NoSuchAlgorithmException;

import com.hdbms.services.HashUtil;

public class User {
    private String username;
    private String password;
    private String name;
    private String surname;
    private int age;
    private String gender;
    private String hashid;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setHashID(String hashid) {
        this.hashid = hashid;
    }

    public String getHashID() {
        return this.hashid;
    }

    // create and set hash id
    public String createHashID() {
        try {
        setHashID(HashUtil.generateKey(getUsername()));
          return getHashID();   
        
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Encountered No Such Algorithm Exception");
            System.out.println(e);
        }
        return null;
    }
}