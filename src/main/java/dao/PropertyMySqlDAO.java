package dao;

import util.DAOUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;

/**
 * Implementation of the {@code PropertyDAO} interface
 * while I like to think my code is self documenting that isn't always the case.
 *
 * @author Joshua Escareno
 */
public class PropertyMySqlDAO extends AbstractMySqlDAO implements PropertyDAO {

    //SQL statements
    private static final String ALL_PROPERTIES_BY_OWNER_QUERY
            = "select /* ALL_PROPERTIES_BY_OWNER_QUERY /*\n"
            + "    * \n" //this is very bad we should never do this
            + "from property\n"
            + "where property_owner_id = ?";

    private static final String ALL_VACANT_PROPERTIES_QUERY =
            ALL_PROPERTIES_BY_OWNER_QUERY.replace("/* ALL_PROPERTIES_BY_OWNER_QUERY /*\n",
                    "ALL_VACANT_PROPERTIES_QUERY")
                    .replace("where property_owner_id = ?", "where property_vacancy_indicator = 'V'\n")
                    .concat("and property_numb_tenants = 0");

    private static final String INSERT_NEW_PROPERTY_STATEMENT
            = "insert into property(property_type, property_address, property_city_code, property_num_rooms, "
            + "property_num_tenants, property_rental_fee, property_last_payment_date, property_owner_id"
            + "property_vacancy_ind)\n"
            + "values(?,?,?,?,?,?,?,?,?)";

    private static final String UPDATE_RENTAL_FEE_STATEMENT
            = "update property\n"
            + "set property_rental_fee = ?\n"
            + "where property_id = ?\n"
            + "and owner_id = ?";

    private final PreparedStatement listPropertiesByOwner;
    private final PreparedStatement listVacantProperties;
    private final CallableStatement insertNewProperty;
    private final CallableStatement updatePropertyFee;

    private static Logger getLogger() {
        return Logger.getLogger(PropertyMySqlDAO.class.getName());
    }

    /**
     * DAO constructor
     *
     * @param conn the connection string to the DB you wish to connect to
     */
    public PropertyMySqlDAO(Connection conn) {
        super(conn);

        this.listPropertiesByOwner = prepareQuery(ALL_PROPERTIES_BY_OWNER_QUERY);
        this.listVacantProperties = prepareQuery(ALL_VACANT_PROPERTIES_QUERY);
        this.insertNewProperty = prepareCall(INSERT_NEW_PROPERTY_STATEMENT);
        this.updatePropertyFee = prepareCall(UPDATE_RENTAL_FEE_STATEMENT);
    }

    /**
     * a method to map a record retrieved in a query to its respective java dao
     *
     * @param rs the result set we a translating into an object
     * @return an instance of {@code PropertyBaseData}
     * @throws SQLException if an error occurs retrieving data from the result set
     */
    private PropertyBaseData mapPropertyBaseData(ResultSet rs) throws SQLException {
        int propertyId = rs.getInt(1);
        String propertyType = rs.getString(2);
        String propertyAddress = rs.getString(3);
        String cityCode = rs.getString(4);
        int numberOfRooms = rs.getInt(5);
        int numberOfBathrooms = rs.getInt(6);
        int garageCount = rs.getInt(7);
        BigDecimal sqrFoot = rs.getBigDecimal(8);
        BigDecimal fYardFoot = rs.getBigDecimal(9);
        BigDecimal bYardFoot = rs.getBigDecimal(10);
        int numberOfTenants = rs.getInt(11);
        BigDecimal rentalFee = rs.getBigDecimal(12);
        LocalDate lastPaymentDate = DAOUtils.getLocalDate(rs, 13);
        String ownerId = rs.getString(14);
        String vacancyInd = rs.getString(15);

        return new PropertyBaseData(propertyId, propertyType, propertyAddress, cityCode, numberOfRooms,
                numberOfBathrooms, garageCount, sqrFoot, fYardFoot, bYardFoot, numberOfTenants, rentalFee,
                lastPaymentDate, ownerId, vacancyInd);
    }

    @Override
    public List<PropertyBaseData> listAllPropertiesByOwner(String ownerId) throws SQLException {
        long start = System.currentTimeMillis();

        try {
            List<PropertyBaseData> result = DAOUtils.queryForList(getConn(), listPropertiesByOwner, ps -> {
                ps.setString(1, ownerId);
            }, this::mapPropertyBaseData);

            long dur = System.currentTimeMillis() - start;
            getLogger().log(INFO, "[SQLStats] ALL_PROPERTIES_BY_OWNER_QUERY ({0}) returned {1} rows in {2} ms.",
                    new Object[]{ownerId, result.size(), dur});
            return result;
        } catch (Exception e) {
            long dur = System.currentTimeMillis() - start;
            getLogger().log(WARNING, "[SQLStats] ALL_PROPERTIES_BY_OWNER_QUERY ({0}) failed ({1}) in {2} ms.",
                    new Object[]{ownerId, e.getMessage().trim(), dur});
            throw e;
        }
    }

    @Override
    public List<PropertyBaseData> listAllVacantProperties() throws SQLException {
        long start = System.currentTimeMillis();

        try {
            List<PropertyBaseData> result = DAOUtils.queryForList(getConn(), listVacantProperties, null,
                    this::mapPropertyBaseData);

            long dur = System.currentTimeMillis() - start;
            getLogger().log(INFO, "[SQLStats] ALL_VACANT_PROPERTIES_QUERY returned {1} rows in {2} ms.",
                    new Object[]{result.size(), dur});
            return result;
        } catch (Exception e) {
            long dur = System.currentTimeMillis() - start;
            getLogger().log(WARNING, "[SQLStats] ALL_VACANT_PROPERTIES_QUERY failed ({1}) in {2} ms.",
                    new Object[]{e.getMessage().trim(), dur});
            throw e;
        }
    }

    @Override
    public void insertNewProperty(PropertyBaseData property) throws SQLException {
        long start = System.currentTimeMillis();

        try (CallableStatement cs = insertNewProperty) {
            cs.setString(1, property.getPropertyType());
            cs.setString(2, property.getPropertyAddress());
            cs.setString(3, property.getCityCode());
            cs.setInt(4, property.getNumberOfRooms());
            cs.setInt(5, property.getNumberOfTenants());
            cs.setBigDecimal(6, property.getRentalFee());
            cs.setDate(7, null);
            cs.setString(8, property.getOwnerID());
            cs.setString(9, property.getVacancyIndicator());

            cs.executeQuery();

            long dur = System.currentTimeMillis() - start;
            getLogger().log(INFO, "[SQLStats] INSERT_NEW_PROPERTY_STATEMENT {0} owned by {1} inserted into " +
                    "property table in {2} ms.", new Object[]{property.toString(), property.getOwnerID(), dur});
        } catch (Exception e) {
            long dur = System.currentTimeMillis() - start;
            getLogger().log(WARNING, "[SQLStats] INSERT_NEW_PROPERTY_STATEMENT for {0} failed ({1}) in {2} ms.",
                    new Object[]{property.getOwnerID(), e.getMessage().trim(), dur});
            throw e;
        }
    }

    @Override
    public void updateRentalFee(BigDecimal newRentalFee, int propertyId, String ownerId) throws SQLException {
        long start = System.currentTimeMillis();
        //I could have added a query to
        try(CallableStatement cs = updatePropertyFee){
            cs.setBigDecimal(1, newRentalFee);
            cs.setInt(2, propertyId);
            cs.setString(3, ownerId);

            cs.executeUpdate();

            long dur = System.currentTimeMillis() - start;
            getLogger().log(INFO, "[SQLStats] UPDATE_RENTAL_FEE_STATEMENT updated {0} to {1}/month in {2} ms.",
                    new Object[]{propertyId, newRentalFee, dur});
        }catch(Exception e){
            long dur = System.currentTimeMillis() - start;
            getLogger().log(WARNING, "[SQLStats] UPDATE_RENTAL_FEE_STATEMENT failed({0}) in {1}ms.",
                    new Object[]{e.getMessage().trim(), dur});
            throw e;
        }
    }
}
