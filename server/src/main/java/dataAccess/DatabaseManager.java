package dataAccess;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

public class DatabaseManager {
    private static final String databaseName;
    private static final String user;
    private static final String password;
    private static final String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) throw new Exception("Unable to load db.properties");
                Properties props = new Properties();
                props.load(propStream);
                databaseName = props.getProperty("db.name");
                user = props.getProperty("db.user");
                password = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    static void createDatabase() throws DataAccessException {
        try {
//            mysql:mysql-connector-java:8.0.30
            var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
//            Class.forName("com.mysql.cj.jdbc.Driver");
            var conn = DriverManager.getConnection(connectionUrl, user, password);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public static void configureDatabase() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException("Problem starting the server");
        }
        try (var conn = DatabaseManager.getConnection()) {
            Collection<String> createStatements = new HashSet<>();
            var createGameTable = """
              CREATE TABLE IF NOT EXISTS game (
                gameID int NOT NULL AUTO_INCREMENT,
                gameName varchar(128) NOT NULL,
                whiteUsername varchar(128),
                blackUsername varchar(128),
                game JSON NOT NULL,
                PRIMARY KEY(gameID)
              )""";
            createStatements.add(createGameTable);
            var createAuthTable = """
              CREATE TABLE IF NOT EXISTS auth (
                username varchar(126) NOT NULL,
                authToken varchar(126) NOT NULL,
                PRIMARY KEY(authToken)
              )""";
            createStatements.add(createAuthTable);
            var createUserTable = """
              CREATE TABLE IF NOT EXISTS user (
                username varchar(126) NOT NULL,
                password varchar(126) NOT NULL,
                email varchar(126) NOT NULL,
                PRIMARY KEY(username)
              )""";
            createStatements.add(createUserTable);

            for (String createTable : createStatements) {
                try (var preparedStatement=conn.prepareStatement(createTable)) {
                    preparedStatement.executeUpdate();
                }
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(connectionUrl, user, password);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
