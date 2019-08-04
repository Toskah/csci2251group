package database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Verifies and stores information about Slumlords properties
 * @author Alex Costello
 */

//Notes to Josh: Not all variables are created or have their verification made yet (table needs more columns)
public class Property {
    private static final boolean debug = true;
    private Integer id, roomCount, bathCount, garageCount, homeFootage, fYardFootage,
            bYardFootage, numberOfTenants;
    private String type, zipCode;
    private CityCode cityCode;
    private BigDecimal rentalFee;
    private String streetAddress;

    private static final Integer cityCodeLength = 3;
    private static final Integer minAddrLength = 5;
    private static final Integer maxAddrLength = 40;
    private static final Integer zipCodeLength = 5;
    private static final Integer maxFootage = 5000;
    private final String state = "NM";    

    private static Connection db;
    private final Date lastPaymentDate;
    private final static String SQLcreate = "propertyID INT NOT NULL AUTO_INCREMENT, "
            + "property_type varchar(1), property_address varchar(" + maxAddrLength + "), property_city_code varchar(3), "
            + "property_numRooms int, property_numBrooms int, property_garage_count int, property_sqr_foot int, property_frontY_sqr_foot,"
            + "property_backY_sqr_foot int, property_num_tenants int, property_rental_fee number(6,2), property_last_payment_date date,"
            + "property_owner_id varchar(30), property_vacancy_ind varchar(1), primary key (propertyID), "
            + "foreign key (property_owner_id) references slumord(username)";

    /**
     * Property constructor for adding properties that checks required variables and stores them
     * @param type 
     * @param cityCode
     * @param streetAddress
     * @param zipCode
     * @param roomCount
     * @param bathCount
     * @param garageCount
     * @param homeFootage
     * @param fYardFootage
     * @param bYardFootage 
     */
    Property(String type, CityCode cityCode, String streetAddress, String zipCode, 
            int roomCount, int bathCount, int garageCount, int homeFootage, int fYardFootage, 
            int bYardFootage, BigDecimal rentalFee, Date lastPaymentDate) {
        validateType(type);
        validateCity(cityCode.toString());
        validateAddress(streetAddress);
        validateZip(zipCode);
        validateRooms(roomCount);
        validateBaths(bathCount);
        validateGarage(garageCount);
        validateHomeFootage(homeFootage);
        validateYardFootage(fYardFootage, bYardFootage);
        validateFee(rentalFee);
        validateDate(lastPaymentDate);
        this.type = type;
        this.cityCode = cityCode;
        this.streetAddress = streetAddress;
        this.zipCode = zipCode;
        this.roomCount = roomCount;
        this.bathCount = bathCount;
        this.garageCount = garageCount;
        this.homeFootage = homeFootage;
        this.fYardFootage = fYardFootage;
        this.bYardFootage = bYardFootage;
        this.rentalFee = rentalFee;
        this.lastPaymentDate = lastPaymentDate;
    }
    
    /**
     * Property constructor for checking and storing result sets.
     * @param result
     * @throws SQLException 
     */
    Property(ResultSet result) throws SQLException {
        String dbType, dbAddr, dbCity, dbZip;
        Integer dbID, dbRooms, dbBaths, dbGarages, dbHomeFootage, dbFYardFootage, dbBYardFootage;
        Date dbLastPaymentDate;
        dbID = result.getInt("propertyid");
        dbType = result.getString("type");
        dbCity = result.getString("citycode");
        dbAddr = result.getString("addr");
        dbZip = result.getString("zipcode");
        dbRooms = result.getInt("roomCount");
        dbBaths = result.getInt("bathCount");
        dbGarages = result.getInt("garageCount");
        dbHomeFootage = result.getInt("homeFootage");
        dbFYardFootage = result.getInt("fYardFootage");
        dbBYardFootage = result.getInt("bYardFootage");
        dbLastPaymentDate = result.getDate("lastPaymentDate");
        validateType(dbType);
        validateAddress(dbAddr);
        validateZip(dbZip);
        validateCity(dbCity);
        validateID(dbID);
        validateRooms(roomCount);
        validateBaths(bathCount);
        validateGarage(garageCount);
        validateHomeFootage(homeFootage);
        validateYardFootage(fYardFootage, bYardFootage);
        validateDate(dbLastPaymentDate);
        this.id = dbID;
        this.type = dbType;
        this.cityCode = CityCode.valueOf(dbCity);
        this.streetAddress = dbAddr;
        this.zipCode = dbZip;
        this.roomCount = dbRooms;
        this.bathCount = dbBaths;
        this.garageCount = dbGarages;
        this.homeFootage = dbHomeFootage;
        this.fYardFootage = dbFYardFootage;
        this.bYardFootage = dbBYardFootage;
        this.lastPaymentDate = dbLastPaymentDate;
    }
    
    /**
     * Validates type of property
     * @param type 1 character string value of type of property
     */
    private void validateType(String type) {
        if (type == null) {
            throw new IllegalArgumentException(String.format("Type must not be null"));
        }

        if (type.length() != 1) {
            throw new IllegalArgumentException(String.format("Type '%s' is %d characters long; "
                    + "length is 1",
                    type, type.length()));
        }

        String validTypes = "A, S, or V";
        switch (type.charAt(0)) {
            case 'A':
            case 'S':
            case 'V':
                break; // OK
            default:
                throw new IllegalArgumentException(String.format("Illegal property type '%s'; "
                        + "valid types are: %s",
                        type, validTypes));
        }
    }
    
    /**
     * Validates street address of property
     * @param streetAddress String of street address
     */
    private void validateAddress(String streetAddress) {
        if (streetAddress == null) {
            throw new IllegalArgumentException(String.format("Street address must not be null"));
        }

        if (streetAddress.length() < minAddrLength) {
            throw new IllegalArgumentException(String.format("Street address '%s' is %d "
                    + "characters long; min length is %d",
                    streetAddress, streetAddress.length(), minAddrLength));
        }
        if (streetAddress.length() > maxAddrLength) {
            throw new IllegalArgumentException(String.format("Street address '%s' is %d "
                    + "characters long; max length is %d",
                    streetAddress, streetAddress.length(), maxAddrLength));
        } //Needs more validation
    }
    
    /**
     * Validates city of property
     * @param city String of city name
     */
    private void validateCity(String city) {
        CityCode.validateCode(city);
    }
    
    /**
     * Validates zip code of property
     * @param zipCode 5 digit string of zip code
     */
    private void validateZip(String zipCode) {
        if (zipCode == null) {
            throw new IllegalArgumentException(String.format("zip code must not be null"));
        }
        if (zipCode.length() != zipCodeLength) {
            throw new IllegalArgumentException(String.format("Illegal zip code '%s' (length %d);"
                    + " valid codes are %d chars",
                    zipCode, zipCode.length(), zipCodeLength));
        }
        if (!zipCode.matches("^[0-9]*$")) {
            throw new IllegalArgumentException(String.format("Illegal zip code '%s'; valid codes"
                    + " only contain digits", zipCode));
        }
        // ADD CHECKING FOR FIRST TWO DIGITS BEING 87 or 88 for New Mexico
    }
     
    /**
     * Validates ID of property
     * @param ID Integer of property id
     */
    private void validateID(Integer ID) {
        if (ID <= 0) {
            throw new IllegalArgumentException(String.format("ID %d is invalid; must be > 0",
                    ID));
        }
    }
    
    /**
     * Validates room count of property
     * @param roomCount Number of rooms
     */
    private void validateRooms(Integer roomCount) {
        if (roomCount <= 0) {
            throw new IllegalArgumentException(String.format("Number of rooms %d is invalid; "
                    + "must be > 0", roomCount));
        }
        if (roomCount > 8) {
            throw new IllegalArgumentException(String.format("Number of rooms %d is invalid; "
                    + "cannot be more than 8 rooms.", roomCount));
        } //assumes there will not be a property with more than 8 rooms        
    }
    
    /**
     * Validates bath count of property
     * @param bathCount Number of bathrooms
     */
    private void validateBaths(Integer bathCount) {
        if (bathCount <= 0) {
            throw new IllegalArgumentException(String.format("Number of bathrooms %d is invalid;"
                    + " must be > 0", bathCount));
        }
        if (roomCount > 6) {
            throw new IllegalArgumentException(String.format("Number of bathrooms %d is invalid;"
                    + " cannot be more than 6 rooms.", bathCount));
        }
    }
    
    /**
     * Validates garage count of property
     * @param garageCount Number of garages, can be 0
     */
    private void validateGarage(Integer garageCount) {
        if (garageCount < 0) {
            throw new IllegalArgumentException(String.format("Number of garages %d is invalid;"
                    + " must be >= 0", garageCount));
        }
        if (garageCount > 4) {
            throw new IllegalArgumentException(String.format("Number of garages %d is invalid;"
                    + " must be < 4", garageCount));
        }
    }
    
    /**
     * Validates square footage of the home
     * @param homeFootage Square footage of home
     */
    private void validateHomeFootage(Integer homeFootage) {
        if (homeFootage <= 0) {
             throw new IllegalArgumentException(String.format("Home's square footage %d is invalid;"
                     + " must be > 0", homeFootage));
        }
        if (homeFootage > maxFootage) {
            throw new IllegalArgumentException(String.format("Home's square footage %d is invalid;"
                    + " must be <= %d", homeFootage, maxFootage));
        }
    }
    
    /**
     * Validates front and back yard square footage of property
     * @param fYardFootage Front yard square footage
     * @param bYardFootage Back yard square footage
     */
    private void validateYardFootage(Integer fYardFootage, Integer bYardFootage) {
        if (fYardFootage <= 0) {
             throw new IllegalArgumentException(String.format("Front yard's square footage %d is"
                     + " invalid; must be > 0", fYardFootage));
        }
        if (fYardFootage > maxFootage) {
            throw new IllegalArgumentException(String.format("Front yard's square footage %d is"
                    + " invalid; must be <= %d", fYardFootage, maxFootage));
        }
        
        if (bYardFootage <= 0) {
             throw new IllegalArgumentException(String.format("Back yard's square footage %d is"
                     + " invalid; must be > 0", bYardFootage));
        }
        if (bYardFootage > maxFootage) {
            throw new IllegalArgumentException(String.format("Back yard's square footage %d is"
                    + " invalid; must be <= %d", bYardFootage, maxFootage));
        }
    }
    
    private void validateFee(BigDecimal fee) {
        
    }
    
    private void validateDate(Date d) {
        //if () {
            
        //}
    }

    /**
     * Gets the type
     * @return type of property
     */
    public String getType() {
        return type;
    }
    
    /**
     * Gets city code
     * @return enum of city code
     */
    public CityCode getCityCode() {
        return cityCode;
    }
    
    /**
     * Gets address
     * @return address of property
     */
    public String getAddress() {
        return streetAddress;
    }
    
    /**
     * Gets zip code
     * @return zip code
     */
    public String zipCode() {
        return zipCode;
    }
    
    /**
     * Gets number of bedrooms
     * @return room count
     */
    public Integer getRoomCount() {
        return roomCount;
    }
    
    /**
     * Gets number of bathrooms
     * @return bath count
     */
    public Integer getBathCount() {
        return bathCount;
    }
    
    /**
     * Gets number of garages
     * @return garage count
     */
    public Integer getGarageCount() {
        return garageCount;
    }
    
    /**
     * Gets home square footage
     * @return home footage
     */
    public Integer getHomeFootage() {
        return homeFootage;
    }
    
    /**
     * Gets front yard square footage
     * @return front yard footage
     */
    public Integer getFrontYardFootage() {
        return fYardFootage;
    }
    
    /**
     * Gets back yard square footage
     * @return back yard 
     */
    public Integer getBackYardFootage() {
        return bYardFootage;
    }

    /**
     * Gets SQL string that defines required columns in property table
     * @return SQLcreate string
     */
    public static String getSQLCreate() {
        return SQLcreate;
    }
    
    /**
     * String version of property for testing
     * @return string of property
     */
    @Override
    public String toString() {
        if (id == null) {
            id = -1;
        }
        return String.format("%s%s%03d; %s, %s %s %s. Number of bedrooms/bathrooms/garages: "
                + "%s/%s/%s. Home Footage: %s sq. ft. Front Yard Footage: %s sq. ft. Back "
                + "Yard Footage: %s sq. .ft.", type, cityCode.toString(), 
                id, streetAddress, cityCode.getFullName(), state, zipCode, roomCount, bathCount, 
                garageCount, homeFootage, fYardFootage, bYardFootage);
    }
    
    /**
     * Binds variables for prepared statement, will drop before final version
     * @param ps prepared statement that needs binding
     * @throws SQLException 
     */
    public void bindvars(PreparedStatement ps) throws SQLException {
        ps.setString(1, type);
        ps.setString(2, cityCode.toString());
        ps.setString(3, streetAddress);
        ps.setString(4, zipCode);
        ps.setInt(5, roomCount);
        ps.setInt(6, bathCount);
        ps.setInt(7, garageCount);
        ps.setInt(8, homeFootage);
        ps.setInt(9, fYardFootage);
        ps.setInt(10, bYardFootage);
    }
    
//    public void createTable() {
//        PreparedStatement ps = null;
//        try {
//            ps = db.prepareStatement(SQLcreate);
//            ps.execute();
//        }
//        catch (SQLException ex) {
//            System.err.println("createTable: Received SQLException when trying to create or execute statement: "
//                    + ex.getMessage());
//            System.err.println("SQL: " + SQLcreate);
//            System.exit(1);
//        }
//        if (debug) {
//            System.out.println("Created table property");
//        }
//    } 
}
