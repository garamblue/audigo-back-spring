package com.audigo.audigo_back;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestDBConnection {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://127.0.0.1:5433/audigo";
        String user = "postgres";
        String password = "cheerup25!";

        System.out.println("=== PostgreSQL Connection Test ===");
        System.out.println("URL: " + url);
        System.out.println("User: " + user);
        System.out.println();

        try {
            System.out.println("Loading PostgreSQL JDBC Driver...");
            Class.forName("org.postgresql.Driver");

            System.out.println("Attempting to connect...");
            Connection conn = DriverManager.getConnection(url, user, password);

            System.out.println("✓ SUCCESS: Connected to PostgreSQL!");
            System.out.println("Connection valid: " + conn.isValid(5));

            conn.close();
            System.out.println("✓ Connection closed successfully");

        } catch (ClassNotFoundException e) {
            System.out.println("✗ FAILED: PostgreSQL Driver not found");
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            System.out.println("✗ FAILED: SQL Exception");
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
