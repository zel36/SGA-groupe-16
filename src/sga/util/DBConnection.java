package sga.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utilitaire de connexion JDBC vers Oracle XE.
 * Configurez les propriétés système ou modifiez les constantes ci-dessous si nécessaire.
 */
public class DBConnection {

    // Valeurs par défaut (modifiable selon votre environnement)
    private static final String DEFAULT_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String DEFAULT_USER = "system";
    private static final String DEFAULT_PASSWORD = "sga";

    static {
        try {
            // Driver Oracle JDBC : la présence du driver doit être assurée dans le classpath
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            // Si le driver n'est pas trouvé, laisser l'exception remonter lors de la connexion
            System.err.println("Oracle JDBC driver not found on classpath: " + e.getMessage());
        }
    }

    /**
     * Obtient une connexion JDBC. La connexion est en mode auto-commit = false pour permettre
     * une gestion explicite des transactions par les DAO.
     *
     * Les paramètres peuvent être fournis via les propriétés système :
     * - db.url, db.user, db.password
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
     * Ferme silencieusement la connexion.
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
