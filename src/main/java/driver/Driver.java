package driver;

import dao.DAOFactory;
import dao.PropertyDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;


/**
 * Author @Dexter Elliott
 */
// create instances of DAO Packages
// send to web client
public class Driver {
    String OwnerID = "ID";
    PropertyDAO DAO = DAOFactory.create(PropertyDAO.class);
    try{
        List<PropertyDAO.PropertyBaseData> Properties = DAO.listAllPropertiesByOwner(OwnerID);
    } catch SQLException e


    public Driver() throws SQLException {

    }
    private static Logger getLogger(){
        return Logger.getLogger(Driver.class.getName());
    }

    private void sendInfo(){

    }


}
