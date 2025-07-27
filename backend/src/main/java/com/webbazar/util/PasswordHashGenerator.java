package com.webbazar.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String adminPass = "Admin123!";
        String userPass = "User123!";
        String testPass = "Test123!";

        System.out.println("Admin hash: " + encoder.encode(adminPass));
        System.out.println("User hash: " + encoder.encode(userPass));
        System.out.println("Test hash: " + encoder.encode(testPass));
    }
}
