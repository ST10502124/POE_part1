



package com.mycompany.prog5121part1;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Scanner;

public class Prog5121part1 {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        ArrayList<Login> users = new ArrayList<>();

        System.out.println("\nWelcome to our chat app 2026!");

        while (true) {

            System.out.println("\nMain Menu:");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {

                case 1: // REGISTER
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();

                    // username validation
                    if (!(username.contains("_") && username.length() <= 5)) {
                        System.out.println("Username must contain '_' and be <= 5 characters.");
                        break;
                    }

                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();

                    // password validation
                    if (!(password.length() >= 8 &&
                            password.matches(".*[A-Z].*") &&
                            password.matches(".*\\d.*") &&
                            password.matches(".*[!@#$%^&*()].*"))) {

                        System.out.println("Password must be 8+ chars, include capital, number & special char.");
                        break;
                    }

                    System.out.print("Enter SA phone number (+27...): ");
                    String phoneNumber = scanner.nextLine();

                    if (!phoneNumber.matches("\\+27[6-8][0-9]{8}")) {
                        System.out.println("Invalid phone number format!");
                        break;
                    }

                    // check if user exists
                    boolean exists = users.stream()
                            .anyMatch(u -> u.getUsername().equals(username));

                    if (exists) {
                        System.out.println("Username already exists.");
                        break;
                    }

                    // add user
                    users.add(new Login(username, password, phoneNumber));
                    System.out.println("User registered successfully!");

                    break;

                case 2: // LOGIN
                    if (users.isEmpty()) {
                        System.out.println("No users registered yet.");
                        break;
                    }

                    promptLogin(scanner, users);
                    break;

                case 3:
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // LOGIN METHOD
    private static void promptLogin(Scanner scanner, ArrayList<Login> users) {

        System.out.print("Enter username: ");
        String enteredUsername = scanner.nextLine();

        System.out.print("Enter password: ");
        String enteredPassword = scanner.nextLine();

        Login user = users.stream()
                .filter(u -> u.getUsername().equals(enteredUsername))
                .findFirst()
                .orElse(null);

        if (user != null && user.loginUser(enteredPassword)) {
            System.out.println("Login successful! Welcome " + enteredUsername);
        } else {
            System.out.println("Invalid username or password.");
        }
    }
}

// LOGIN CLASS
class Login {

    private String username;
    private String phoneNumber;
    private String passwordHash;
    private byte[] salt;

    public Login(String username, String password, String phoneNumber) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.salt = generateSalt();
        this.passwordHash = hashPassword(password, salt);
    }

    public String getUsername() {
        return username;
    }

    public boolean loginUser(String enteredPassword) {
        String enteredHash = hashPassword(enteredPassword, salt);
        return this.passwordHash.equals(enteredHash);
    }

    // HASH PASSWORD
    private String hashPassword(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hash = md.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // GENERATE SALT
    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
}