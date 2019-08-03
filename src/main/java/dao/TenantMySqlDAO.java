package dao;

import util.DAOUtils;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;

public class TenantMySqlDAO extends AbstractMySqlDAO implements TenantDAO {

    private static final String RETRIEVE_TENANTS_BY_PROPERTY
            = "select /* RETRIEVE_TENANTS_BY_PROPERTY */\n"
            + "* \n"//again we should never do this
            + "from tenant\n"
            + "where tenant_property_id = ?";

    private static final String RETRIEVE_TENANTS_BY_PROPERTIES
            = RETRIEVE_TENANTS_BY_PROPERTY.replace("where tenant_property_id = ?",
            "where tenant_property_id in ??");

    private final PreparedStatement retrieveTenants;
    private final PreparedStatement listTenants;

    private static Logger getLogger() {
        return Logger.getLogger(TenantMySqlDAO.class.getName());
    }

    public TenantMySqlDAO(Connection conn) {
        super(conn);
        this.retrieveTenants = prepareQuery(RETRIEVE_TENANTS_BY_PROPERTY);
        this.listTenants = prepareQuery(RETRIEVE_TENANTS_BY_PROPERTIES);
    }

    private TenantData mapTenantData(ResultSet rs) throws SQLException {
        int pid = rs.getInt(1);
        String firstName = rs.getString(2);
        String lastName = rs.getString(3);
        LocalDate dob = DAOUtils.getLocalDate(rs, 4);
        String phoneNumber = rs.getString(5);
        String address = rs.getString(6);
        String city = rs.getString(7);
        String zipCode = rs.getString(8);
        int propertyId = rs.getInt(9);

        return new TenantData(pid, firstName, lastName, dob, phoneNumber, address, city, zipCode, propertyId);
    }

    @Override
    public List<TenantData> listTenantsByProperty(int propertyId) throws SQLException {
        long start = System.currentTimeMillis();

        try {
            List<TenantData> result = DAOUtils.queryForList(getConn(), retrieveTenants, ps -> {
                ps.setInt(1, propertyId);
            }, this::mapTenantData);

            long dur = System.currentTimeMillis() - start;
            getLogger().log(INFO, "[SQLStats] RETRIEVE_TENANTS_BY_PROPERTY [pid: {0}] returned {1} rows in {2}ms.",
                    new Object[]{propertyId, result.size(), dur});
            return result;
        } catch (Exception e) {
            long dur = System.currentTimeMillis() - start;
            getLogger().log(WARNING, "[SQLStats] RETRIEVE_TENANTS_BY_PROPERTY [pid: {0}] failed({1}) in {2}ms.",
                    new Object[]{propertyId, e.getMessage().trim(), dur});
            throw e;
        }
    }

    @Override
    public List<TenantData> listTenantByProperties(List<Integer> propertyIds) throws  SQLException {
        long start = System.currentTimeMillis();

        try{
            List<TenantData> result = DAOUtils.queryForList(getConn(), listTenants, ps -> {
                int i =0, collateSize = getCollateSize();
                for(Integer pid : propertyIds) ps.setInt(++i, pid);
                while(i < collateSize) ps.setNull(++i, Types.INTEGER);
            }, this::mapTenantData);

            long dur = System.currentTimeMillis() - start;
            getLogger().log(INFO, "[SQLStats] RETRIEVE_TENANTS_BY_PROPERTIES[{0}] retrieved {1} rows in {2} ms.",
                    new Object[]{DAOUtils.mkPrintList(propertyIds), result.size(), dur});
            return result;
        }catch(Exception e){
            long dur = System.currentTimeMillis() - start;
            getLogger().log(WARNING,  "[SQLStats] RETRIEVE_TENANTS_BY_PROPERTIES[{0}] failed({1}) in {2} ms.",
                    new Object[]{DAOUtils.mkPrintList(propertyIds), e.getMessage().trim(), dur});
            throw e;
        }
    }
}
