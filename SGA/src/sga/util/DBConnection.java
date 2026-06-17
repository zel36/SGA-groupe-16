package sga.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton thread-safe pour la gestion des connexions JDBC vers Oracle XE.
 *
 * Les paramètres de connexion peuvent être fournis dans l'ordre suivant (priorité décroissante):
 * 1) propriétés système: -Ddb.url=... -Ddb.user=... -Ddb.password=...
 * 2) fichier `config.properties` (cherché en priorité dans le répertoire de travail puis dans le classpath)
 *    clés attendues: jdbc.url, jdbc.user, jdbc.password
 * 3) valeurs par défaut codées
 */
public final class DBConnection {

    private static final String DEFAULT_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String DEFAULT_USER = "system";
    private static final String DEFAULT_PASSWORD = "manager";

    private static final Properties PROPS = loadProperties();

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
     * Charge les propriétés depuis config.properties (fichier à la racine du projet ou dans src),
     * puis depuis le classpath. Si aucune valeur n'est trouvée, on utilisera les valeurs par défaut.
     */
    private static Properties loadProperties() {
        Properties p = new Properties();
        // 1) Tentative: fichier config.properties dans le répertoire de travail (projet racine)
        File f = new File("config.properties");
        if (f.exists() && f.isFile() && f.canRead()) {
            try (InputStream in = new FileInputStream(f)) {
                p.load(in);
                System.out.println("Loaded DB configuration from file: " + f.getAbsolutePath());
                return p;
            } catch (IOException e) {
                System.err.println("Failed to load config.properties from project root: " + e.getMessage());
            }
        }

        // 2) Tentative: resource sur le classpath (ex: placé dans src)
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                p.load(in);
                System.out.println("Loaded DB configuration from classpath resource: config.properties");
                return p;
            }
        } catch (IOException e) {
            System.err.println("Failed to load config.properties from classpath: " + e.getMessage());
        }

        // 3) Aucun fichier trouvé -> retourner propriétés vides (les defaults seront utilisées)
        System.out.println("No config.properties found - using defaults or system properties");
        return p;
    }

    /**
     * Retourne une nouvelle connexion JDBC avec autoCommit désactivé.
     */
    public static Connection getConnection() throws SQLException {
        // Priority: system properties override config.properties which override defaults
        String url = System.getProperty("db.url", PROPS.getProperty("jdbc.url", DEFAULT_URL));
        String user = System.getProperty("db.user", PROPS.getProperty("jdbc.user", DEFAULT_USER));
        String password = System.getProperty("db.password", PROPS.getProperty("jdbc.password", DEFAULT_PASSWORD));
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
