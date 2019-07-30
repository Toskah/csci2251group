import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Alex
 */
public class PropertyTable {
    private static boolean debug = true;
    private Connection db;
    private final String tableName = "property";
    private final String createsql;
    
    PropertyTable(Connection db) {
        if (db == null) {
            throw new IllegalArgumentException("PropertyTable constructor: db connection is null");
        }
        this.db = db;
        this.createsql = "create table property" + Property.getSQLCreate() + ";";
    }
    
    public boolean insert(Property p) {
        if (debug) {
            System.out.println("About to insert property: " + p.toString());
        }
        
        PreparedStatement ps = null;
        String sql = p.getInsertPSString(tableName);
        int nInserted = 0;
        try {
            ps = db.prepareStatement(sql);
        } catch (SQLException ex) {
             System.err.printf("insert: Received SQLException when trying to create or execute statement: %s",
                    ex.getMessage());
            System.err.println("SQL: " + sql);
            System.exit(1);
        }
        return nInserted == 1;
    }
    
    public ArrayList<Property> getAll() {
        if(debug) {
            System.out.println("About to getAll");
        }
        return selectSome(Property.getSelectString(tableName));
    }
    
    public ArrayList<Property> getCity(CityCode city) {
        if (debug) {
            System.out.println("About to getCity for: " + city.getFullName());
        }
        return selectSome(Property.getSelectString(tableName, city));
    }
    
    public ArrayList<Property> selectSome(String sql) {
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
        } catch (SQLException ex) {
            System.err.printf("getAll: Received SQLException when trying to create or execute statement: %s",
                    ex.getMessage());
            System.err.println("SQL: " + sql);
            System.exit(1);
        }
        return properties;
    }
    
    public void dropTable() {
        PreparedStatement ps = null;
        String sql = "DROP TABLE property;";
        try {
            ps = db.prepareStatement(sql);
            ps.execute();
        } catch (SQLException ex) {
            System.err.println("dropTable: Received SQLException when trying to create or execute statement: "
                    + ex.getMessage());
            System.err.println("SQL: " + sql);
            System.exit(1);
        }
        if (debug) {
            System.out.println("Dropped table property");
        }
    }
    
    public void createTable() {
        PreparedStatement ps = null;
        try {
            ps = db.prepareStatement(createsql);
            ps.execute();
        }
        catch (SQLException ex) {
            System.err.println("createTable: Received SQLException when trying to create or execute statement: "
                    + ex.getMessage());
            System.err.println("SQL: " + createsql);
            System.exit(1);
        }
        if (debug) System.out.println("Created table property");
    }
    
    public boolean checkTableExists() {
        boolean answer = false;
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='"
                + tableName + "';";
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = db.prepareStatement(sql);
            if (ps.execute()) {
                result = ps.getResultSet();
                if (!result.isClosed()) {
                    result.next();
                    if (result.getRow() >= 0) {
                        answer = result.getString(1).equals(tableName);
                    } else {
                        System.out.println("Result set is closed.");
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("checkTableExists: Received SQLException "
                    + "when trying to create or execute: " + sql);
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        return answer;
    }
}
