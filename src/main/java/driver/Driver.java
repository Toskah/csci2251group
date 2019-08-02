package driver;

import dao.DAOFactory;
import dao.PropertyDAO;
import dao.SlumlordDAO;

import javax.sound.sampled.Line;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Author @Dexter Elliott
 */
// create instances of DAO Packages
// send to web client
public class Driver {
    String OwnerID = "ID"; //Place Holder until I can grab data from database and website
    PropertyDAO DAO = DAOFactory.create(PropertyDAO.class);
    SlumlordDAO SDAO = DAOFactory.create(SlumlordDAO.class);


    public Driver(){
        try{
            List<PropertyDAO.PropertyBaseData> Properties = DAO.listAllPropertiesByOwner(OwnerID);
            getLogger().log(Level.INFO, "Grabbing property list");
        } catch(SQLException e){
            System.out.println("SQL Exception occurred");
            System.out.println(e.getErrorCode());
            getLogger().log(Level.WARNING, "SQL try catch caught something");
        }

    }

    /**
     * Will log info in case of an error
     * @return the logged action
     */
    private static Logger getLogger() {
        return Logger.getLogger(Driver.class.getName());
    }

    /**
     * Will send info to the website
     */
    private void sendInfo() {

    }
    private void recInfo(){

    }

}
