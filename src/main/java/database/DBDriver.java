package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Creates database and sets up property and tenant tables within.
 * @author Alex Costello
 */
public class DBDriver {
    private static final boolean debug = true;
    private static Connection db;
    private static final String DB_NAME = "slumlords";
    private static final String createPropertySQL = "CREATE TABLE property (" + Property.getSQLCreate() + ");";
    private static final String createTenantSQL = "CREATE TABLE tenant (" + Tenant.getSQLCreate() + ");";
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/";
    static final String USER = "root";
    static final String PASS = "password";
    
    public static void main(String[] args) {
        createDB();
        db = dbConnect();
        dropTable(db, "property"); //delete, used for personal testing
        createTable(db, "property");
        createTable(db, "tenant");
        
        try {
            db.close();
        } catch (SQLException e) {
            System.out.println("Error closing database connection in main. Error: " + e.getMessage());
        }
    }
    
    /**
     * Checks if database already exists, if not, creates the database
     */
    public static void createDB() {
        boolean exists = false;
        Connection conn = null;
        Statement stmt = null;
        ResultSet resultSet = null;
        
        try {
            Class.forName(JDBC_DRIVER); 
            if (debug) {
                System.out.println("Connecting to localhost...");
            }
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            if (debug) {
                System.out.println("Checking if database exists...");
            }
            resultSet = conn.getMetaData().getCatalogs();
            
            while (resultSet.next()) {
                String databaseName = resultSet.getString(1);
                if (databaseName.equals(DB_NAME)) {
                    if (debug) {
                        System.out.printf("Database '%s' already exists.\n", DB_NAME);
                    }
                    exists = true;
                }
            }
            resultSet.close();
            
            if(!exists) {
                if (debug) {
                    System.out.printf("Database does not exist. Creating database '%s'...\n", DB_NAME);
                }   
                stmt = conn.createStatement();
                String sql = "CREATE DATABASE " + DB_NAME;
                stmt.executeUpdate(sql);
                
                if (debug) {
                    System.out.println("Database created successfully.");
                }
            }
        } catch (ClassNotFoundException c) {
            System.out.println("Invalid driver class name, class was not found. Error: " + c.getMessage());
            System.exit(1);
        } catch (SQLException e) {
            System.out.println("Error while checking for or creating database. Error: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Connects to the database for table methods
     * @return connection to database
     */
    public static Connection dbConnect() {
        Connection conn = null;
        String dbName = "Slumlord";
        String connString = DB_URL + dbName;
        
        try {
            conn = DriverManager.getConnection(connString, USER, PASS);
            DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
            if (debug) {
                System.out.println("dbConnect: Connected to database " + connString);
            }
        }
        catch (SQLException ex) {
            System.err.println("Received SQLException when trying to open db: "
                    + connString + " " + ex.getMessage());
            System.err.println("Connection string: " + connString);
            System.exit(1);
        }
        return conn;
    }
    
    /**
     * Tries to create the given table name. Requires that the code to create the table is
     * already predefined.
     * @param conn connection to the database
     * @param tableName name of the table to create
     */
    public static void createTable(Connection conn, String tableName) {
        boolean exists = false;
        Statement stmt = null;
        ResultSet resultSet = null;
        String sql;
//        testing to see if new method works, need tenant table to work first
//        PreparedStatement ps = null;
//        String sql = "SELECT property FROM name WHERE type='table' AND name='" +DB_NAME +"';";
        
        try {
//            if (debug) {
//                System.out.printf("Checking for '%s' table in database...\n", tableName);
//            }
//            ps = conn.prepareStatement(sql);
//            if (ps.execute()) {
//                resultSet = ps.getResultSet();
//                if (!resultSet.isClosed()) {
//                    resultSet.next();
//                    if (resultSet.getRow() >= 0) {
//                        exists = resultSet.getString(1).equals(tableName);
//                    }
//                    resultSet.close();
//                } else {
//                    if (debug) {
//                        System.out.println("Result set is closed.");
//                    }
//                }
//            }

            resultSet = conn.getMetaData().getTables(null, null, tableName, null);
            while (resultSet.next()) {
                if (resultSet.getString(3).equals(tableName)) {
                    if (debug) {
                        System.out.printf("%s table already exists in database.\n", tableName);
                    }
                    exists = true;
                }
            }
            
            if(!exists) {
                sql = getCreateSQL(tableName);
                stmt = conn.createStatement();
                stmt.executeUpdate(sql);
                if(debug) {
                    System.out.printf("Created %s table in database.\n", tableName);
                }
            }
        } catch (SQLException e) {
            System.out.printf("Error creating %s table. Error: %s\n", tableName, e.getMessage());
        }
    }
    
    /**
     * Drops given table, use the method with caution. Intended for development.
     * @param tableName name of table to drop
     */
    public static void dropTable(Connection db, String tableName) {
        PreparedStatement ps = null;
        String sql = "DROP TABLE " + tableName;
        try {
            ps = db.prepareStatement(sql);
            ps.execute();
        } catch (SQLException e) {
            System.out.println("dropTable: Received SQLException when trying to execute statement: "
                    + e.getMessage());
            System.out.println("SQL: " + sql);
        }
        if (debug) {
            System.out.println("Dropped table " + tableName);
        }
    }
    
    /**
     * Gets table creation SQL code based on name of table 
     * @param tableName name of table
     * @return SQL string
     */
    public static String getCreateSQL(String tableName) {
        if (tableName.equals("property")) {
            return createPropertySQL;
        } else if (tableName.equals("tenant")) {
            return createTenantSQL;
        }
        return null;
    }
    
    /**
     * Gets name of database
     * @return database name
     */
    public static String getDBName() {
        return DB_NAME;
    }
}