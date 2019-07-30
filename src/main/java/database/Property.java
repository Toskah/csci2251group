package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Alex
 */
public class Property {
    private Integer id;
    private String type;
    private CityCode cityCode;
    private static final Integer cityCodeLength = 3;
    private String streetAddress;
    private static final Integer minAddrLength = 5;
    private static final Integer maxAddrLength = 40;
    private final String state = "NM";
    private String zipCode;
    private static final Integer zipCodeLength = 5;
    private Integer roomCount;
    private Integer bathCount;
    private Integer garageCount;
    private Integer homeFootage;
    private Integer yardFootage;
    private static final Integer maxFootage = 5000;
    private final static String SQLcreate = "(propertyid INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " type CHAR(1), citycode CHAR(" + CityCode.getMaxLength() + "), addr VARCHAR(" 
            + maxAddrLength.toString() + "), zipcode char(10)), roomCount Integer(1), "
            + "bathCount Integer(1), garageCount Integer(1), homeFootage Integer, yardFootage Integer";
    
    Property(String type, CityCode cityCode, String streetAddress, String zipCode, Integer roomCount, Integer bathCount, Integer garageCount, Integer homeFootage, Integer yardFootage) {
        validateType(type);
        validateCity(cityCode.toString());
        validateAddress(streetAddress);
        validateZip(zipCode);
        validateRooms(roomCount);
        validateBaths(bathCount);
        validateGarage(garageCount);
        validateHomeFootage(homeFootage);
        validateYardFootage(yardFootage);
        this.type = type;
        this.cityCode = cityCode;
        this.streetAddress = streetAddress;
        this.zipCode = zipCode;
        this.roomCount = roomCount;
        this.bathCount = bathCount;
        this.garageCount = garageCount;
        this.homeFootage = homeFootage;
        this.yardFootage = yardFootage;
    }
    
    Property(ResultSet result) throws SQLException {
        String dbType, dbAddr, dbCity, dbZip;
        Integer dbID, dbRooms, dbBaths, dbGarages, dbHomeFootage, dbYardFootage;
        dbID = result.getInt("propertyid");
        dbType = result.getString("type");
        dbCity = result.getString("citycode");
        dbAddr = result.getString("addr");
        dbZip = result.getString("zipcode");
        validateType(dbType);
        validateAddress(dbAddr);
        validateZip(dbZip);
        validateCity(dbCity);
        validateID(dbID);
        validateRooms(roomCount);
        validateBaths(bathCount);
        validateGarage(garageCount);
        validateHomeFootage(homeFootage);
        validateYardFootage(yardFootage);
        this.id = dbID;
        this.type = dbType;
        this.cityCode = CityCode.valueOf(dbCity);
        this.streetAddress = dbAddr;
        this.zipCode = dbZip;
        this.roomCount = roomCount;
        this.bathCount = bathCount;
        this.garageCount = garageCount;
        this.homeFootage = homeFootage;
        this.yardFootage = yardFootage;
    }
    
    private void validateType(String type) {
        if (type == null) {
            throw new IllegalArgumentException(String.format("Type must not be null"));
        }

        if (type.length() != 1) {
            throw new IllegalArgumentException(String.format("Type '%s' is %d characters long; length is 1",
                    type, type.length()));
        }

        String validTypes = "A, S, or V";
        switch (type.charAt(0)) {
            case 'A':
            case 'S':
            case 'V':
                break; // OK
            default:
                throw new IllegalArgumentException(String.format("Illegal property type '%s'; valid types are: %s",
                        type, validTypes));
        }
    }
    
    private void validateAddress(String streetAddress) {
        if (streetAddress == null) {
            throw new IllegalArgumentException(String.format("Street address must not be null"));
        }

        if (streetAddress.length() < minAddrLength) {
            throw new IllegalArgumentException(String.format("Street address '%s' is %d characters long; min length is %d",
                    streetAddress, streetAddress.length(), minAddrLength));
        }
        if (streetAddress.length() > maxAddrLength) {
            throw new IllegalArgumentException(String.format("Street address '%s' is %d characters long; max length is %d",
                    streetAddress, streetAddress.length(), maxAddrLength));
        }
    }
    
    private void validateCity(String city) {
        CityCode.validateCode(city);
    }
    
    private void validateZip(String zipCode) {
        if (zipCode == null) {
            throw new IllegalArgumentException(String.format("zip code must not be null"));
        }
        if (zipCode.length() != zipCodeLength) {
            throw new IllegalArgumentException(String.format("Illegal zip code '%s' (length %d); valid codes are %d chars",
                    zipCode, zipCode.length(), zipCodeLength));
        }
        if (!zipCode.matches("^[0-9]*$")) {
            throw new IllegalArgumentException(String.format("Illegal zip code '%s'; valid codes only contain digits", zipCode));
        }
        // ADD CHECKING FOR FIRST TWO DIGITS BEING 87 or 88 for New Mexico
    }
     
    private void validateID(Integer ID) {
        if (ID <= 0) {
            throw new IllegalArgumentException(String.format("ID %d is invalid; must be > 0", ID));
        }
    }
    
    private void validateRooms(Integer roomCount) {
        if (roomCount <= 0) {
            throw new IllegalArgumentException(String.format("Number of rooms %d is invalid; must be > 0", roomCount));
        }
        if (roomCount > 8) {
            throw new IllegalArgumentException(String.format("Number of rooms %d is invalid; cannot be more than 8 rooms.", roomCount));
        } //assumes there will not be a property with more than 8 rooms        
    }
    
    private void validateBaths(Integer bathCount) {
        if (bathCount <= 0) {
            throw new IllegalArgumentException(String.format("Number of bathrooms %d is invalid; must be > 0", bathCount));
        }
        if (roomCount > 6) {
            throw new IllegalArgumentException(String.format("Number of bathrooms %d is invalid; cannot be more than 6 rooms.", bathCount));
        }
    }
    
    private void validateGarage(Integer garageCount) {
        if (garageCount < 0) {
            throw new IllegalArgumentException(String.format("Number of garages %d is invalid; must be >= 0", garageCount));
        }
        if (garageCount > 4) {
            throw new IllegalArgumentException(String.format("Number of garages %d is invalid; must be < 4", garageCount));
        }
    }
    
    private void validateHomeFootage(Integer homeFootage) {
        if (homeFootage <= 0) {
             throw new IllegalArgumentException(String.format("Home's square footage %d is invalid; must be > 0", homeFootage));
        }
        if (homeFootage > maxFootage) {
            throw new IllegalArgumentException(String.format("Home's square footage %d is invalid; must be <= %d", homeFootage, maxFootage));
        }
    }
    
    private void validateYardFootage(Integer yardFootage) {
        if (yardFootage <= 0) {
             throw new IllegalArgumentException(String.format("Yard's square footage %d is invalid; must be > 0", yardFootage));
        }
        if (yardFootage > maxFootage) {
            throw new IllegalArgumentException(String.format("Yard's square footage %d is invalid; must be <= %d", yardFootage, maxFootage));
        }
    }
    
    public String getType() {
        return type;
    }
    
    public CityCode getCityCode() {
        return cityCode;
    }
    
    public String getAddress() {
        return streetAddress;
    }
    
    public String zipCode() {
        return zipCode;
    }
    
    public Integer getRoomCount() {
        return roomCount;
    }
    
    public Integer getBathCount() {
        return bathCount;
    }
    
    public Integer getGarageCount() {
        return garageCount;
    }
    
    public Integer getHomeFootage() {
        return homeFootage;
    }
    
    public Integer getYardFootage() {
        return yardFootage;
    }
    
    public static String getSQLCreate() {
        return SQLcreate;
    }
    
    @Override
    public String toString() {
        if (id == null) {
            id = -1;
        }
        return String.format("%s%s%03d; %s, %s %s %s. Number of bedrooms/bathrooms/garages: "
                + "%s/%s/%s. Home Footage: %s ft. Yard Footage: %s ft.", type, cityCode.toString(), 
                id, streetAddress, cityCode.getFullName(), state, zipCode, roomCount, bathCount, 
                garageCount, homeFootage, yardFootage);
    }
    
    public String getInsertPSString(String table) {
        return "INSERT INTO " + table + "(type, citycode, addr, zipcode)"
                + "values (?,?,?,?);"; //UNFINISHED
    }
    
    public void bindvars(PreparedStatement ps) throws SQLException {
        ps.setString(1, type);
        ps.setString(2, cityCode.toString());
        ps.setString(3, streetAddress);
        ps.setString(4, zipCode);//UNFINISHED
    }
    
    public static String getSelectString(String table) {
        return String.format("SELECT propertyid, type, citycode, addr, zipcode from %s;", table);
    }
    
    public static String getSelectString(String table, CityCode city) {
        return String.format("SELECT propertyid, type, citycode, addr, zipcode from %s where citycode = '%s';", table, city.toString());
    }
}
