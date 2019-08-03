package dao;

import util.DAOUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;

/**
 * Implementation of the {@code SLumlordDAO} interface
 *
 * @author Joshua Escareno
 */
public class SlumlordMySqlDAO extends AbstractMySqlDAO implements SlumlordDAO {

    private static final String RETRIEVE_SLUMLORD_INFO_QUERY
            = "select /* RETRIEVE_SLUMLORD_INFO_QUERY */\n"
            + " * \n"
            + "from slumlord\n"
            + "where slumlord_user_name = ?";

    private static final String REGISTER_NEW_SLUMLORD_STATEMENT
            = "insert into slumlord(slumlord_user_name, slumlord_first_name, slumlord_last_name, slumlord_dob)\n"
            + "values(?,?,?,?)";

    private static Logger getLogger() {
        return Logger.getLogger(SlumlordMySqlDAO.class.getName());
    }

    private final PreparedStatement retrieveSlumlord;
    private final PreparedStatement registerSlumlord;

    public SlumlordMySqlDAO(Connection conn) {
        super(conn);

        this.retrieveSlumlord = prepareQuery(RETRIEVE_SLUMLORD_INFO_QUERY);
        this.registerSlumlord = prepareQuery(REGISTER_NEW_SLUMLORD_STATEMENT);
    }

    private SlumData mapSlumlordData(ResultSet rs) throws SQLException {
        int pid = rs.getInt(1);
        String userName = rs.getString(2);
        String firstName = rs.getString(3);
        String lastName = rs.getString(4);
        LocalDate dob = DAOUtils.getLocalDate(rs, 5);

        return new SlumData(pid, userName, firstName, lastName, dob);
    }

    @Override
    public SlumData lookupSlumData(String userName) throws SQLException {
        long start = System.currentTimeMillis();

        try {
            List<SlumData> user = DAOUtils.queryForList(getConn(), retrieveSlumlord, ps -> {
                ps.setString(1, userName);
            }, this::mapSlumlordData);

            long dur = System.currentTimeMillis() - start;
            getLogger().log(INFO, "[SQLStats] Retrieved user {0} in {1} ms.", new Object[]{userName, dur});
            return user.get(0);
        } catch (Exception e) {
            long dur = System.currentTimeMillis() - start;
            getLogger().log(WARNING, "[SQLStats] Cannot find {0} RETRIEVE_SLUMLORD_INFO_QUERY failed ({1}) in {2} ms."
                , new Object[]{userName, e.getMessage().trim(), dur});
            throw e;
        }
    }

    @Override
    public void registerNewSlumlord(SlumData newSlumlord) throws SQLException{
        long start = System.currentTimeMillis();

        try(PreparedStatement ps = registerSlumlord){
            ps.setString(1, newSlumlord.getUserName());
            ps.setString(2, newSlumlord.getFirstName());
            ps.setString(3, newSlumlord.getLastName());
            ps.setDate(4, DAOUtils.asSqlDate(newSlumlord.getDob()));

            long dur = System.currentTimeMillis() - start;
            getLogger().log(INFO, "[SQLStats] REGISTER_NEW_SLUMLORD [{0}] succeeded in {1} ms.",
                    new Object[]{newSlumlord.getUserName(), dur});
        }catch(Exception e){
            long dur = System.currentTimeMillis() - start;
            getLogger().log(WARNING, "[SQLStats] REGISTER_NEW_SLUMLORD failed({0}) in {1}ms",
                    new Object[]{e.getMessage().trim(), dur});
            throw e;
        }
    }


}
