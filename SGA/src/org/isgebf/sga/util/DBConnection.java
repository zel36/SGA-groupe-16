package org.isgebf.sga.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton helper that provides JDBC connections to an Oracle database.
 * <p>
 * Uses Oracle thin driver: oracle.jdbc.driver.OracleDriver
 */
public class DBConnection {

    private static final String DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
    // Default credentials; override with environment variables SGA_DB_USER / SGA_DB_PASS if needed
    private static final String DEFAULT_USER = "sga";
    private static final String DEFAULT_PASS = "sga";

    private static DBConnection instance;

    private DBConnection() {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Oracle JDBC Driver not found: " + DRIVER_CLASS, e);
        }
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    /**
     * Obtain a new Connection. Caller is responsible for closing it.
     * @return a new JDBC Connection
     * @throws SQLException If connection cannot be established
     */
    public Connection getConnection() throws SQLException {
        String user = System.getenv("SGA_DB_USER");
        String pass = System.getenv("SGA_DB_PASS");
        if (user == null || user.trim().isEmpty()) {
            user = DEFAULT_USER;
        }
        if (pass == null) {
            pass = DEFAULT_PASS;
        }
        return DriverManager.getConnection(URL, user, pass);
    }
}
