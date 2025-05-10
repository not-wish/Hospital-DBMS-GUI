package com.hdbms.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    public static String generateKey(String user) throws NoSuchAlgorithmException {
        try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
        byte[] hash = digest.digest(user.getBytes());

        StringBuilder hexID = new StringBuilder();

        for (byte b : hash) {
            hexID.append(String.format("%02x", b));  // %02x = 2-digit hex
        }

        return hexID.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }

        return "NULL";
    }
}