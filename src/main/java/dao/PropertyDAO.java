package dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface PropertyDAO extends BaseDAO {

    /**
     *  A Method to retrieve a list of all the properties owned by a user
     * @param ownerId the person who owns the properties we wish to list
     * @return a list of PropertyBaseData ojects
     * @throws SQLException if the query cannot be executed
     */
    List<ProprtyBaseData> listAllPropertiesByOwner(String ownerId) throws SQLException;

    final class ProprtyBaseData{
        private final int propertyId;
        private final String propertyType;
        private final String propertyAddress;
        private final int numberOfRooms;
        private final int numberOfTenants;
        private final BigDecimal rentalFee;
        private final LocalDate lastPaymentDate;
        private final String ownerID;

        public ProprtyBaseData(
                int propertyId,
                String propertyType,
                String propertyAddress,
                int numberOfRooms,
                int numberOfTenants,
                BigDecimal rentalFee,
                LocalDate lastPaymentDate,
                String ownerID
        ){
            this.propertyId = propertyId;
            this.propertyType = propertyType;
            this.propertyAddress = propertyAddress;
            this.numberOfRooms = numberOfRooms;
            this.numberOfTenants = numberOfTenants;
            this.rentalFee = rentalFee;
            this.lastPaymentDate = lastPaymentDate;
            this.ownerID = ownerID;
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
    }
}
