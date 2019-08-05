package dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * DAO for the {@code TENANT} table in the database
 *
 * @author Joshua Escareno
 */
public interface TenantDAO extends BaseDAO {

    /**
     * A method to retrieve all the tenants inhabiting a property
     * @param propertyId the property that we are searching for tenants in
     * @return returns a list of tenant information
     * @throws SQLException if the query cannot be executed
     */
    List<TenantData> listTenantsByProperty(int propertyId) throws SQLException;

    /**
     * A method to retrieve all the tenants for a list of given property ids
     * @param propertyIds the list of properties that we need tenant info from
     * @return a list of tenant information
     * @throws SQLException if the query cannot be executed
     */
    List<TenantData> listTenantByProperties(List<Integer> propertyIds) throws  SQLException;

    /**
     * Data access class for the tenant table
     */
    final class TenantData implements Serializable {
        private final int pid;
        private final String firstName;
        private final String lastName;
        private final LocalDate dob;
        private final String phoneNumber;
        private final String address;
        private final String city;
        private final String zipCode;
        private final int propertyId;

        public TenantData(
                int pid,
                String firstName,
                String lastName,
                LocalDate dob,
                String phoneNumber,
                String address,
                String city,
                String zipCode,
                int propertId
        ) {
            this.pid = pid;
            this.firstName = firstName;
            this.lastName = lastName;
            this.dob = dob;
            this.phoneNumber = phoneNumber;
            this.address = address;
            this.city = city;
            this.zipCode = zipCode;
            this.propertyId = propertId;
        }

        public int getPid() {
            return pid;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public LocalDate getDob() {
            return dob;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getAddress() {
            return address;
        }

        public int getPropertyId() {
            return propertyId;
        }

        public String getCity() {
            return city;
        }

        public String getZipCode() {
            return zipCode;
        }
    }
}
