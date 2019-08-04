package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class initially creates the system's database then connects to the database to create the required 
 * tables. Most of this can be done manually, however this is intended to make installation easier for
 * Slumlords, LLC. 
 * @author Alex Costello
 */
public class DBDriver {
    private static final boolean debug = true; //set to false to disable non-error output
    private static Connection db;

    private static final String DB_NAME = "slumlord";
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/";
    private static final String USER = "root";
    private static final String PASS = "password";
    private static final Integer cityCodeLength = 3;
    private static final Integer maxAddrLength = 40;
    
    private static final String slumlordColumns = "pid int AUTO_INCREMENT, slumlord_userName varchar(30), "
            + "slumlord_first_name varchar(30), slumlord_last_name varchar(30), slumlord_dob date, primary key (pid)";
    private static final String propertyColumns = "property_ID int AUTO_INCREMENT, "
            + "property_type varchar(1), property_address varchar(" + maxAddrLength + "), property_city_code "
            + "varchar(" + cityCodeLength + "), property_numRooms int, property_numBrooms int, property_garage_count int, "
            + "property_sqr_foot int, property_frontY_sqr_foot int, property_backY_sqr_foot int, property_num_tenants int, "
            + "property_rental_fee decimal(6,2), property_last_payment_date date, property_owner_id varchar(30), "
            + "property_vacancy_ind varchar(1), primary key (property_ID), foreign key (property_owner_id) references slumlord (slumord_userName)";
    private static final String tenantColumns = "property_ID INT, tenant_ID int AUTO_INCREMENT, tenant_first_name varchar(30), "
            + "tenant_last_name varChar(30), tenant_phone_number varchar(10), tenant_dob date, tenant_address varchar(40), "
            + "tenant_city varchar(20), tenant_zipCode char(5), primary key (tenant_ID), "
            + "foreign key (property_ID) references property (property_ID)";
        
    public static void main(String[] args) {
        createDB();
        db = dbConnect();
//        dropTable(db, "slumlord");
//        dropTable(db, "property");
//        dropTable(db, "tenant"); REMOVE WHEN TESTING IS FINISHED
        createTable(db, "slumlord");
        createTable(db, "property");
        createTable(db, "tenant");
        
        try { //closes connection to database after tables were created
            db.close();
        } catch (SQLException e) {
            System.out.println("Error closing database connection in main. Error: " + e.getMessage());
        }
    }
    
    /**
     * Checks if database already exists, and if database is not found this method will create it.
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
     * A method to connect to the database. The connection is required for table creation and deletion.
     * @return connection to database
     */
    public static Connection dbConnect() {
        Connection conn = null;
        String connString = DB_URL + DB_NAME;
        
        try {
            conn = DriverManager.getConnection(connString, USER, PASS);
            DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
            if (debug) {
                System.out.println("dbConnect: Connected to database " + connString);
            }
        } catch (SQLException ex) {
            System.err.println("Received SQLException when trying to open db: "
                    + connString + " " + ex.getMessage());
            System.err.println("Connection string: " + connString);
            System.exit(1);
        }
        return conn;
    }
    
    /**
     * A method to create table of name passed through parameters. This requires that the getCreateSQL
     * method has the SQL code required already set up to create the tables.
     * already predefined.
     * @param conn connection to the database
     * @param tableName name of the table to create
     */
    public static void createTable(Connection conn, String tableName) {
        boolean exists = false;
        String sql;
        Statement stmt = null;
        ResultSet resultSet = null;
        
        try {
            if (debug) {
                System.out.printf("Checking for '%s' table in database...\n", tableName);
            }
            
            resultSet = conn.getMetaData().getTables(null, null, tableName, null);
            if (resultSet.next()) {
                if (debug) {
                    System.out.printf("%s table already exists in database.\n", tableName);
                }
                exists = true;
            }
            
            if(!exists) { //creates table if not found
                sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + getColumnSQL(tableName) + ");";
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
     * Drops given table, use the method with caution.Intended for development. Note that using this 
     * method removes the given table and all data with it. You can not drop a table if it is referenced
     * in another, meaning tables have to be deleted in reverse order of creation.
     * @param db connection to the database
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
    public static String getColumnSQL(String tableName) {
        switch (tableName) {
            case "slumlord":
                return slumlordColumns;
            case "property":
                return propertyColumns;
            case "tenant":
                return tenantColumns;
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