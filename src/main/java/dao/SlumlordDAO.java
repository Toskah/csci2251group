package dao;


import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * DAO for the {@code SLUMLORD} table
 * with various functions for Slumlord user management
 * This is intentionally left spartan as we don't have time to implement non functional requirements
 *
 * @author Joshua Escareno
 */
public interface SlumlordDAO extends BaseDAO {

    /**
     * A method to get the user record from the database
     * @param userName the userName that we are querying for must be unique in the db
     * @return a SlumData object used to get user data to be used else where in the application
     * @throws SQLException if the query cannot be executed
     */
    SlumData lookupSlumData(String userName) throws SQLException;

    /**
     * A method to insert a new slumlord in the database
     * @param newbie the newest member of the exploitative bourgeoisie
     * @throws SQLException if the insert cannot be completed
     */
    void registerNewSlumlord(SlumData newbie) throws SQLException;

    /**
     * data access class for the {@code SLUMLORD} table
     */
    final class SlumData implements Serializable {
        private final int pid;
        private final String userName;
        private final String firstName;
        private final String lastName;
        private final LocalDate dob;

        public SlumData(
                int pid,
                String userName,
                String firstName,
                String lastName,
                LocalDate dob
        ) {
            this.pid = pid;
            this.userName = userName;
            this.firstName = firstName;
            this.lastName = lastName;
            this.dob = dob;
        }

        public int getPid() {
            return pid;
        }

        public String getUserName() {
            return userName;
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
    }
}
