package com.quiz;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

import com.quiz.util.DBConnection;

public class CheckDb {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Connected to DB! Executing database_setup.sql...");
            String sql = new String(Files.readAllBytes(Paths.get("database_setup.sql")));
            String[] commands = sql.split(";");
            
            for (String command : commands) {
                if (!command.trim().isEmpty()) {
                    stmt.execute(command);
                }
            }
            System.out.println("Database reset successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
