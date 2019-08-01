package dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Data access class for the {@code Property} table
 * It's important to note that tons of functionality has been intentionally left out of
 * this DAO as we could have queries to return maps of properties related to their owners,
 * which tenants are in which properties, properties by city, etc,
 * for Dr. Slumlord's SlumManagement software power user
 * @author Joshua Escareno
 */
public interface PropertyDAO extends BaseDAO {

    /**
     *  A Method to retrieve a list of all the properties owned by a user
     * @param ownerId the person who owns the properties we wish to list
     * @return a list of PropertyBaseData ojects
     * @throws SQLException if the query cannot be executed
     */
    List<PropertyBaseData> listAllPropertiesByOwner(String ownerId) throws SQLException;

    /**
     *  A Method to retrieve all vacant properties in the database, that is properties with 0 tenants and a
     *  {@code vacancyIndicator} of "V"
     * @return a list of PropertyBaseData objects
     * @throws SQLException if the query cannot be completed
     */
    List<PropertyBaseData> listAllVacantProperties() throws SQLException;

    /**
     * A method to insert a new property record into the {@code Property} table
     * @param property the property object to be inserted
     * @throws SQLException if the insert cannot be executed
     */
    void insertNewProperty(PropertyBaseData property) throws SQLException;

    /**
     * A method to update the {@code property_rental_fee} field for a given property
     * @param propertyId the id of the property that we are updating
     * @param newRentalFee the new rental fee
     * @throws SQLException if the update command cannot be executed
     */
    void updateRentalFee(BigDecimal newRentalFee, int propertyId, String ownerId) throws SQLException;

    /**
     * Data access class for passing data from the db easily.
     */
    final class PropertyBaseData{
        private final int propertyId;
        private final String propertyType;
        private final String propertyAddress;
        private final String cityCode;
        private final int numberOfRooms;
        private final int numberOfTenants;
        private final BigDecimal rentalFee;
        private final LocalDate lastPaymentDate;
        private final String ownerID;
        private final String vacancyIndicator;

        public PropertyBaseData(
                int propertyId,
                String propertyType,
                String propertyAddress,
                String cityCode,
                int numberOfRooms,
                int numberOfTenants,
                BigDecimal rentalFee,
                LocalDate lastPaymentDate,
                String ownerID,
                String vacancyIndicator
        ){
            this.propertyId = propertyId;
            this.propertyType = propertyType;
            this.propertyAddress = propertyAddress;
            this.cityCode = cityCode;
            this.numberOfRooms = numberOfRooms;
            this.numberOfTenants = numberOfTenants;
            this.rentalFee = rentalFee;
            this.lastPaymentDate = lastPaymentDate;
            this.ownerID = ownerID;
            this.vacancyIndicator = vacancyIndicator;
        }

        public int getPropertyId() {
            return propertyId;
        }

        public String getPropertyType() {
            return propertyType;
        }

        public String getPropertyAddress() {
            return propertyAddress;
        }

        public int getNumberOfRooms() {
            return numberOfRooms;
        }

        public int getNumberOfTenants() {
            return numberOfTenants;
        }

        public BigDecimal getRentalFee() {
            return rentalFee;
        }

        public LocalDate getLastPaymentDate() {
            return lastPaymentDate;
        }

        public String getOwnerID() {
            return ownerID;
        }

        public String getVacancyIndicator() {
            return vacancyIndicator;
        }

        public String getCityCode() {
            return cityCode;
        }
    }
}
