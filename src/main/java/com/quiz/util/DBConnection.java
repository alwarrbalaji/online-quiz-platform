package com.quiz.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DBConnection — Utility class for JDBC connection management.
 *
 * OPTIMIZATIONS:
 *   1. Driver loaded once in static block (not on every call)
 *   2. Null-safe property reading with clear error messages
 *   3. isValid(2) check ensures returned connection is alive
 *   4. Custom RuntimeException thrown so callers don't silently get null
 */
public class DBConnection {

    private static final String PROPS_FILE = "db.properties";
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;
    private static boolean initialized = false;

    // ── Load driver and properties ONCE at class load time ────
    static {
        try (InputStream input = DBConnection.class
                .getClassLoader().getResourceAsStream(PROPS_FILE)) {

            if (input == null) {
                throw new RuntimeException(
                    "CRITICAL: '" + PROPS_FILE + "' not found in src/main/resources/");
            }

            Properties props = new Properties();
            props.load(input);

            dbUrl      = getNonEmpty(props, "db.url");
            dbUser     = getNonEmpty(props, "db.user");
            dbPassword = props.getProperty("db.password", "").trim();

            // Load driver once
            Class.forName("com.mysql.cj.jdbc.Driver");
            initialized = true;

        } catch (Exception e) {
            System.err.println("DBConnection static init failed: " + e.getMessage());
        }
    }

    // ── Get a fresh connection ─────────────────────────────────
    /**
     * Returns a valid, open JDBC Connection.
     * Always use in try-with-resources to ensure it is closed.
     *
     * Example:
     *   try (Connection conn = DBConnection.getConnection()) { ... }
     *
     * @throws RuntimeException if connection cannot be established
     */
    public static Connection getConnection() {
        if (!initialized) {
            throw new RuntimeException(
                "DBConnection not initialized. Check db.properties and MySQL server.");
        }
        try {
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            if (conn == null || !conn.isValid(2)) {
                throw new RuntimeException(
                    "Connection obtained but failed isValid() check. Is MySQL running?");
            }
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(
                "Failed to connect to database. Check MySQL is running and credentials are correct. "
                + e.getMessage(), e);
        }
    }

    // ── Helper: read required property, throw if missing ──────
    private static String getNonEmpty(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(
                "Missing required property '" + key + "' in " + PROPS_FILE);
        }
        return value.trim();
    }

    // ── Helper: safe close without throwing ───────────────────
    /**
     * Silently closes a connection. Safe to call with null.
     * Use this in finally blocks when NOT using try-with-resources.
     */
    public static void closeQuietly(Connection conn) {
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }
}