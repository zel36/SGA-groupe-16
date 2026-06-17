package sga.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton thread-safe pour la gestion des connexions JDBC vers Oracle XE.
 *
 * Par défaut la configuration est :
 * URL  : jdbc:oracle:thin:@localhost:1521:XE
 * User : system
 * Pass : manager
 *
 * Vous pouvez modifier ces valeurs en passant les propriétés système :
 * -Ddb.url=... -Ddb.user=... -Ddb.password=...
 */
public final class DBConnection {

    private static final String DEFAULT_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String DEFAULT_USER = "system";
    private static final String DEFAULT_PASSWORD = "manager";

    private static volatile DBConnection INSTANCE;

    private DBConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Oracle JDBC driver not found on classpath: " + e.getMessage());
        }
    }

    public static DBConnection getInstance() {
        if (INSTANCE == null) {
            synchronized (DBConnection.class) {
                if (INSTANCE == null) INSTANCE = new DBConnection();
            }
        }
        return INSTANCE;
    }

    /**
     * Retourne une nouvelle connexion JDBC avec autoCommit désactivé.
     */
    public static Connection getConnection() throws SQLException {
        String url = System.getProperty("db.url", DEFAULT_URL);
        String user = System.getProperty("db.user", DEFAULT_USER);
        String password = System.getProperty("db.password", DEFAULT_PASSWORD);
        Connection conn = DriverManager.getConnection(url, user, password);
        conn.setAutoCommit(false);
        return conn;
    }

    /**
     * Ferme silencieusement la connexion passée.
     */
    public static void closeQuietly(Connection c) {
        if (c != null) {
            try {
                c.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
