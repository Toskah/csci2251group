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

//Notes to Josh: going to change dbCreate() to check if db exists, create if not, same with tables
public class DBDriver {
    private static final boolean debug = true;
    private static Connection db;
    private static final String tableName = "property";
    private static final String createPropertySQL = "CREATE TABLE property (" + Property.getSQLCreate() + ");";
    //private static final String createTenantSQL = "create table tenant (" + Tenant.getSQLCreate() + ");";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/";
    static final String USER = "root";
    static final String PASS = "password";
    
    public static void main(String[] args) {
        //dbCreate();
        db = dbConnect();
        createPropertyTable(db);
    }
    
    public static void dbCreate() {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            Class.forName(JDBC_DRIVER);
            if (debug) {
                System.out.println("Connecting to database...");
            }
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            if (debug) {
                System.out.println("Creating database...");
            }
            stmt = conn.createStatement();
            
            String sql = "CREATE DATABASE Slumlords";
            stmt.executeUpdate(sql);
            System.out.println("Database created successfully...");
        } catch (ClassNotFoundException c) {
            System.out.println("Invalid class name, class was not found. Error: " + c.getMessage());
            System.exit(1);
        } catch (SQLException e) {
            System.out.println("Error creating database. Error: " + e.getMessage());
            System.exit(1);
        } finally {
            try {
                if (stmt != null)
                stmt.close();
            } catch (SQLException e2) {
                System.out.println("Error closing database creation statement. Error: " + e2.getMessage());
            }
            
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e3) {
                System.out.println("Error closing database connection after creation. Error: " + e3.getMessage());
            }
        }
    }
    
    public static Connection dbConnect() {
        Connection conn = null;
        String dbName = "Slumlords";
        String connString = DB_URL + dbName;
        
        try {
            conn = DriverManager.getConnection(connString, USER, PASS);
            DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
            if (debug) {
                System.out.println("dbConnect: Connected to database " + connString);
                System.out.println("Driver name: " + dm.getDriverName());
                System.out.println("Driver version: " + dm.getDriverVersion());
                System.out.println("Product name: " + dm.getDatabaseProductName());
                System.out.println("Product version: " + dm.getDatabaseProductVersion());
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
    
    public static void createPropertyTable(Connection conn) {
        Statement stmt = null;
        
        try {
            Class.forName(JDBC_DRIVER);
            if (debug) {
                System.out.println("Connecting to selected database...");
                System.out.println("Creating property table in database...");
            }
                      
            stmt = conn.createStatement();
            String sql = Property.getSQLCreate();
            stmt.executeUpdate(sql);
            System.out.println("Created property table in database...");
        } catch (SQLException e) {
            System.out.println("Error creating property table. Error: " + e.getMessage());
        } catch (ClassNotFoundException c) {
            System.out.println("Invalid database driver class given while creating property "
                    + "table. Error: " + c.getMessage());
        } finally {
            try {
                if (stmt != null) {
                    conn.close();
                }
            } catch (SQLException e2) { //do nothing
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e3) {
                System.out.println("Error closing connection while creating property table. "
                        + "Error: " + e3.getMessage());
            }
        }
    }
    
//    public static void createTenantTable() {
//        PreparedStatement ps = null;
//        try {
//            ps = db.prepareStatement(createTenantSQL);
//            ps.execute();
//        }
//        catch (SQLException ex) {
//            System.err.println("createTable: Received SQLException when trying to create or execute statement: "
//                    + ex.getMessage());
//            System.err.println("SQL: " + createTenantSQL);
//            System.exit(1);
//        }
//        if (debug) {
//            System.out.println("Created table property");
//        }
//    }
    
//    public static void addPropertyTest(Property p) {
//        if (debug) {
//            System.out.println("About to insert property: " + p.toString());
//        }
//        
//        PreparedStatement ps = null;
//        String sql = "INSERT INTO property(type, cityCode, address, zipCode, roomCount, bathCount, garageCount, homeFootage, fYardFootage, bYardFootage) values (?,?,?,?,?,?,?,?,?,?);";
//        int nInserted = 0;
//        try {
//            ps = db.prepareStatement(sql);
//        } catch (SQLException ex) {
//             System.err.printf("insert: Received SQLException when trying to create or execute statement: %s",
//                    ex.getMessage());
//            System.err.println("SQL: " + sql);
//            System.exit(1);
//        }
//        nInserted = 1;
//    }
//    
//    public static ArrayList<Property> getAll() {
//        if (debug) {
//            System.out.println("About to getAll");
//        }
//
//        return selectSome("SELECT propertyid, type, citycode, addr, zipcode, roomCount, bathCount, garageCount, homeFootage, fYardFootage, bYardFootage from property;");
//    }
//    
//    private static ArrayList<Property> selectSome(String sql) {
//        ArrayList<Property> properties = new ArrayList<>();
//        PreparedStatement ps = null;
//        ResultSet result = null;
//        
//        try {
//            ps = db.prepareStatement(sql);
//            result = ps.executeQuery();
//            while (result.next()) {
//                properties.add(new Property(result));
//            }
//            result.close();
//        }
//        catch (SQLException ex) {
//            System.err.printf("getAll: Received SQLException when trying to create or execute statement: %s",
//                    ex.getMessage());
//            System.err.println("SQL: " + sql);
//            System.exit(1);
//        }
//        return properties;
//    }
//    
//    public static ArrayList<Property> getCity(CityCode city) {
//        if (debug) {
//            System.out.println("About to getCity for " + city.getFullName());
//        }
//        return selectSome("SELECT propertyid, type, citycode, addr, zipcode, roomCount, bathCount, garageCount, homeFootage, fYardFootage, bYardFootage from property where citycode = 'ABQ';");
//    }
}