package validation;

import java.math.BigDecimal;
import java.util.Date;

/**
 * A class for validating property data
 * @author Alex Costello
 */
public class PropertyValidator {
    private Integer id, roomCount, bathCount, garageCount, squareFootage, fYardFootage,
            bYardFootage, numberOfTenants, vacancyIndicator;
    private String type, address, zipCode;
    private CityCode cityCode;
    private BigDecimal rentalFee;
    private Date lastPaymentDate;
    
    private static final Integer CITYCODE_LENGTH = 3;
    private static final Integer MIN_ADDR_LENGTH = 5;
    private static final Integer MAX_ADDR_LENGTH = 40;
    private static final Integer ZIPCODE_LENGTH = 5;
    private static final Integer MAX_FOOTAGE = 5000;
    private static final String STATE = "NM";

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
    PropertyValidator(String type, CityCode cityCode, String streetAddress, String zipCode, 
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

        if (streetAddress.length() < MIN_ADDR_LENGTH) {
            throw new IllegalArgumentException(String.format("Street address '%s' is %d "
                    + "characters long; min length is %d",
                    streetAddress, streetAddress.length(), MIN_ADDR_LENGTH));
        }
        if (streetAddress.length() > MAX_ADDR_LENGTH) {
            throw new IllegalArgumentException(String.format("Street address '%s' is %d "
                    + "characters long; max length is %d",
                    streetAddress, streetAddress.length(), MAX_ADDR_LENGTH));
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
        if (zipCode.length() != ZIPCODE_LENGTH) {
            throw new IllegalArgumentException(String.format("Illegal zip code '%s' (length %d);"
                    + " valid codes are %d chars",
                    zipCode, zipCode.length(), ZIPCODE_LENGTH));
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
                    + " invalid; must be <= %d", fYardFootage, MAX_FOOTAGE));
        }
        
        if (bYardFootage <= 0) {
             throw new IllegalArgumentException(String.format("Back yard's square footage %d is"
                     + " invalid; must be > 0", bYardFootage));
        }
        if (bYardFootage > maxFootage) {
            throw new IllegalArgumentException(String.format("Back yard's square footage %d is"
                    + " invalid; must be <= %d", bYardFootage, MAX_FOOTAGE));
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
    public Integer getSquareFootage() {
        return squareFootage;
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
}
