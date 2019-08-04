package database;

/**
 * INCOMPLETE
 * @author Alex Costello
 */
public class Tenant {
    private Integer propertyID;
    private String type;
    private CityCode cityCode;
    private String tenantName;
    private String phoneNumber;
    private String vacancyIndicator;
    private final static String SQLcreate = "propertyID INT, tenantID INT NOT NULL AUTO_INCREMENT, name varchar(30), "
            + "phoneNumber varchar(10), vacancyIndicator varchar(1), PRIMARY KEY (tenantID), "
            + "FOREIGN KEY (propertyID) REFERENCES property(propertyID)" ;
    
    Tenant(Integer propertyID, String tenantName, String phoneNumber) {
        validateID(propertyID);
        validateTenantName(tenantName);
        validateNumber(phoneNumber);
        this.propertyID = propertyID;
        this.tenantName = tenantName;
        this.phoneNumber = phoneNumber;
    }
    
    public void validateID(Integer ID) {
        if (ID <= 0) {
            throw new IllegalArgumentException(String.format("ID %d is invalid; must be > 0", ID));
        }
    }
    
    public void validateTenantName(String name) {
        if (name.length() < 25) {
            throw new IllegalArgumentException(String.format("Name %s is invalid; must be less than 26 characters", name));
        }
    }
    
    public void validateNumber(String number) {
        if(number.length() != 10) {
            throw new IllegalArgumentException(String.format("Phone number %s is invalid; must be a 10 digit number.", number));
        }
    }
    
    private Integer getPropertyID() {
        return propertyID;
    }
    
    private String getTenantName() {
        return tenantName;
    }
    
    private String getPhoneNumber() {
        return phoneNumber;
    }
    
    public static String getSQLCreate() {
        return SQLcreate;
    }
    
    @Override
    public String toString() {
        return String.format("Property %s%s%03d is rented to: %s. Phone Number: %s.", type, cityCode.toString(), 
                propertyID, tenantName, phoneNumber);
    }
}