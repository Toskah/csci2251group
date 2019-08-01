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
    private Integer fYardFootage;
    private Integer bYardFootage;
    private static final Integer maxFootage = 5000;
    private final static String SQLcreate = "(propertyID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " type CHAR(1), cityCode CHAR(" + CityCode.getMaxLength() + "), addr VARCHAR(" 
            + maxAddrLength.toString() + "), zipCode char(10)), roomCount Integer(1), "
            + "bathCount Integer(1), garageCount Integer(1), homeFootage Integer, "
            + "fYardFootage Integer, bYardFootage Integer";
    
    Property(String type, CityCode cityCode, String streetAddress, String zipCode, int roomCount, int bathCount, int garageCount, int homeFootage, int fYardFootage, int bYardFootage) {
        validateType(type);
        validateCity(cityCode.toString());
        validateAddress(streetAddress);
        validateZip(zipCode);
        validateRooms(roomCount);
        validateBaths(bathCount);
        validateGarage(garageCount);
        validateHomeFootage(homeFootage);
        validateYardFootage(fYardFootage, bYardFootage);
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
    }
    
    Property(ResultSet result) throws SQLException {
        String dbType, dbAddr, dbCity, dbZip;
        Integer dbID, dbRooms, dbBaths, dbGarages, dbHomeFootage, dbFYardFootage, dbBYardFootage;
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
    
    private void validateYardFootage(Integer fYardFootage, Integer bYardFootage) {
        if (fYardFootage <= 0) {
             throw new IllegalArgumentException(String.format("Front yard's square footage %d is invalid; must be > 0", fYardFootage));
        }
        if (fYardFootage > maxFootage) {
            throw new IllegalArgumentException(String.format("Front yard's square footage %d is invalid; must be <= %d", fYardFootage, maxFootage));
        }
        
        if (bYardFootage <= 0) {
             throw new IllegalArgumentException(String.format("Back yard's square footage %d is invalid; must be > 0", bYardFootage));
        }
        if (bYardFootage > maxFootage) {
            throw new IllegalArgumentException(String.format("Back yard's square footage %d is invalid; must be <= %d", bYardFootage, maxFootage));
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
    
    public Integer getFrontYardFootage() {
        return fYardFootage;
    }
    
    public Integer getBackYardFootage() {
        return bYardFootage;
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
                + "%s/%s/%s. Home Footage: %s sq. ft. Front Yard Footage: %s sq. ft. Back Yard Footage: %s sq. .ft.", type, cityCode.toString(), 
                id, streetAddress, cityCode.getFullName(), state, zipCode, roomCount, bathCount, 
                garageCount, homeFootage, fYardFootage, bYardFootage);
    }
    
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
}
