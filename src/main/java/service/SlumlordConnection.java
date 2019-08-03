package service;

import java.sql.*;
import java.util.Map;
import java.util.logging.Logger;

import static java.util.logging.Level.*;

/**
 * Provides database connection to standalone jobs,
 * This is used mainly due to every day use with DAOs and out of habit
 *
 * @author Joshua Escareno
 */
public class SlumlordConnection {
    // Retrieve logger and log class name for troubleshooting
    private static final String CLASS_NAME = SlumlordConnection.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS_NAME);

    private static final String DEFAULT_JDBC_CONNECTION = "jdbc:mysql://localhost/";
    private static final String DEFAULT_USERNAME = "testuser";

    private static final String JDBC_CONNECTION_ENV = "JDBC_CONNECTION";
    private static final String PASSWORD_ENV = "PPW";
    private static final String USERNAME_PASSWORD_ENV = "UPW";

    private static final String GLOBAL_NAME_QUERY = "select * from global_name";

    private static Connection instance;
    private static String globalName;

    private SlumlordConnection() {
        throw new IllegalArgumentException(
                "Cannot instantiate SlumlordConnection; use static getInstance()");
    }

    static ConnectionParameters buildConnectionParameters(Map<String, String> env) {
        String url = env.get(JDBC_CONNECTION_ENV);

        if (url == null) {
            url = DEFAULT_JDBC_CONNECTION;
            LOG.log(INFO, "{0} not defined; using default ''{1}''",
                    new Object[]{JDBC_CONNECTION_ENV, url});
        } else {
            LOG.log(CONFIG, "Using JDBC connection ''{0}''", url);
        }

        String username = DEFAULT_USERNAME;
        String password;
        String upw = env.get(USERNAME_PASSWORD_ENV);

        if (upw != null) {
            String[] nameAndPassword = upw.split("/");

            if (nameAndPassword.length != 2) {
                LOG.log(SEVERE, "Invalid {0}; unable to connect to slumlord database.",
                        USERNAME_PASSWORD_ENV);
                throw new IllegalArgumentException("Invalid " + USERNAME_PASSWORD_ENV);
            }

            username = nameAndPassword[0];
            password = nameAndPassword[1];
        } else {
            password = env.get(PASSWORD_ENV);

            if (password == null) {
                LOG.log(SEVERE, "{0} not defined; unable to connect to slumlord database.",
                        PASSWORD_ENV);
                throw new IllegalArgumentException(PASSWORD_ENV + " not defined");
            }
        }

        return new ConnectionParameters(url, username, password);
    }

    public static String getGlobalName() {
        if (globalName == null) {
            Connection conn = getInstance();
            Long start = System.currentTimeMillis();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(GLOBAL_NAME_QUERY)) {
                if (rs.next()) {
                    globalName = rs.getString(1);
                }
            } catch (SQLException e) {
                Long dur = System.currentTimeMillis() - start;
                LOG.log(WARNING, "[SQLStats] GLOBAL_NAME[] failed({0}) in {1} ms",
                        new Object[]{e.getMessage().trim(), dur});

                return null;
            }

            Long dur = System.currentTimeMillis() - start;
            LOG.log(INFO, "[SQLStats] GLOBAL_NAME[] = {0} in {1} ms",
                    new Object[]{globalName, dur});
        }

        return globalName;
    }

    public static Connection getInstance() {
        if (instance == null)
            instance = getInstance(System.getenv());

        return instance;
    }

    public static Connection getInstance(Map<String, String> env) {
        ConnectionParameters cp = buildConnectionParameters(env);
        Long start = System.currentTimeMillis();

        try {
            globalName = null;
            instance = DriverManager.getConnection(
                    cp.getUrl(), cp.getUsername(), cp.getPassword());

            Long dur = System.currentTimeMillis() - start;
            LOG.log(INFO, "[SQLStats] CONNECT[{0} as {1}] in {2} ms",
                    new Object[]{cp.getUrl(), cp.getUsername(), dur});
        } catch (SQLException e) {
            Long dur = System.currentTimeMillis() - start;
            LOG.log(SEVERE, "[SQLStats] CONNECT[{0} as {1}] failed({2}) in {3} ms",
                    new Object[]{cp.getUrl(), cp.getUsername(), e.getMessage().trim(), dur});
            throw new RuntimeException("Unable to connect to Slumlord database.");
        }

        return instance;
    }

    /**
     * Java bean for passing around connection parameters, used mainly for testing the {@code
     * buildConnectionParameters()} method.
     */
    static final class ConnectionParameters {
        private final String url;
        private final String username;
        private final String password;

        public ConnectionParameters(String url, String username, String password) {
            this.url = url;
            this.username = username;
            this.password = password;
        }

        public String getUrl() {
            return url;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
