package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Alex
 */
public class DBTest {
    private static final boolean debug = true;
    private Connection db;
    private final String tableName = "property";
    private final String createpropertysql = "create table property" + Property.getSQLCreate() + ";";
    
    public void main(String[] args) {
        db = dbConnect();
        createPropertyTable();
        
        Property p1 = new Property("S", CityCode.ABQ, "909 Georgia St. SE", "87108", 5, 2, 1, 2400, 1500, 800); 
        addPropertyTest(p1);
        
        System.out.println("Table contents:");
        for (Property p : getAll()) {
            System.out.println(p.toString());
        }
        
        System.out.println("Properties in ABQ:");
        for (Property p : getCity(CityCode.ABQ)) {
            System.out.println(p.toString());
        }
    }
    
    public static Connection dbConnect() {
        Connection conn = null;
        String protocol = "jdbc:sqlite:";
        String dbName = "C:\\sqlite\\db\\Slumlords.db";
        String connString = protocol + dbName;
        
        try {
            conn = DriverManager.getConnection(connString);
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
    
    public void createPropertyTable() {
        PreparedStatement ps = null;
        try {
            ps = db.prepareStatement(createpropertysql);
            ps.execute();
        }
        catch (SQLException ex) {
            System.err.println("createTable: Received SQLException when trying to create or execute statement: "
                    + ex.getMessage());
            System.err.println("SQL: " + createpropertysql);
            System.exit(1);
        }
        if (debug) {
            System.out.println("Created table property");
        }
    }
    
    public void addPropertyTest(Property p) {
        if (debug) {
            System.out.println("About to insert property: " + p.toString());
        }
        
        PreparedStatement ps = null;
        String sql = "INSERT INTO property(type, cityCode, address, zipCode, roomCount, bathCount, garageCount, homeFootage, fYardFootage, bYardFootage) values (?,?,?,?,?,?,?,?,?,?);";
        int nInserted = 0;
        try {
            ps = db.prepareStatement(sql);
        } catch (SQLException ex) {
             System.err.printf("insert: Received SQLException when trying to create or execute statement: %s",
                    ex.getMessage());
            System.err.println("SQL: " + sql);
            System.exit(1);
        }
        nInserted = 1;
    }
    
    public ArrayList<Property> getAll() {
        if (debug) {
            System.out.println("About to getAll");
        }

        return selectSome("SELECT propertyid, type, citycode, addr, zipcode, roomCount, bathCount, garageCount, homeFootage, fYardFootage, bYardFootage from property;");
    }
    
    private ArrayList<Property> selectSome(String sql) {
        ArrayList<Property> properties = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet result = null;
        
        try {
            ps = db.prepareStatement(sql);
            result = ps.executeQuery();
            while (result.next()) {
                properties.add(new Property(result));
            }
            result.close();
        }
        catch (SQLException ex) {
            System.err.printf("getAll: Received SQLException when trying to create or execute statement: %s",
                    ex.getMessage());
            System.err.println("SQL: " + sql);
            System.exit(1);
        }
        return properties;
    }
    
    public ArrayList<Property> getCity(CityCode city) {
        if (debug) {
            System.out.println("About to getCity for " + city.getFullName());
        }
        return selectSome("SELECT propertyid, type, citycode, addr, zipcode, roomCount, bathCount, garageCount, homeFootage, fYardFootage, bYardFootage from property where citycode = 'ABQ';");
    }
}