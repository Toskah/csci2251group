package driver;

import Serialize.SerializedClass;
import dao.DAOFactory;
import dao.PropertyDAO.*;
import dao.PropertyDAO;
import dao.SlumlordDAO;
import dao.TenantDAO;
import database.DBDriver;
import util.DAOUtils;


import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;


/**
 * Author @Dexter Elliott
 */
// create instances of DAO Packages
// send to web client
public class Driver {
    String OwnerID = "ID"; //Place Holder until I can grab data from database and website
    PropertyDAO DAO = DAOFactory.create(PropertyDAO.class);
    SlumlordDAO SDAO = DAOFactory.create(SlumlordDAO.class);
    TenantDAO TDAO = DAOFactory.create(TenantDAO.class);
    DBDriver DBD = new DBDriver();

    public Driver(){
        try{
            List<PropertyDAO.PropertyBaseData> Properties = DAO.listAllPropertiesByOwner(OwnerID);
            getLogger().log(INFO, "Grabbing property list");
        } catch(SQLException e){
            System.out.println("SQL Exception occurred");
            System.out.println(e.getErrorCode());
            getLogger().log(WARNING, "SQL try-catch caught something");
        }

    }

    public void decideCommand(int userCommand){
        //TODO decision logic
        //TODO fill dao by ownerID
        /**
         * switch:
         *   case 1:
         *      totalRentDue(userCommand.getOwner)
         *      getInputString
         */
    }

    /**
     * Will log info in case of an error
     * @return the logged action
     */
    private static Logger getLogger() {
        return Logger.getLogger(Driver.class.getName());
    }

    public List<Serializable> serializePropertyByOwnerList(String ownerID) {
        List<PropertyBaseData> result = null;
        try {
           result = DAO.listAllPropertiesByOwner(ownerID);
        }catch(Exception e){
            getLogger().log(WARNING, "An error({0}) occurred fetching properties by ownerID",
                    e.getMessage().trim());
        }


    }

    public BigDecimal totalRentDue(String ownerID) {
        List<PropertyBaseData> result = null;
        try{
          result = DAO.listAllPropertiesByOwner(ownerID);
        }catch(Exception e){
            getLogger().log(WARNING, "An error({0}) occurred fetching properties by ownerID",
                    e.getMessage().trim());
        }
        BigDecimal totalRent = new BigDecimal(0);

        for(PropertyBaseData prop : result){
            totalRent.add(prop.getRentalFee());
        }
        return result.stream()
                .map(PropertyBaseData::getRentalFee)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.valueOf(0));
    }

    public List<PropertyBaseData> upcomingRentalNotice(String ownerID) throws SQLException{
        List<PropertyBaseData> result = DAO.listAllPropertiesByOwner(ownerID);

       result = result.stream()
                .map(property -> {
                   int daysTillRentDue = Period.between(LocalDate.now(), property.getLastPaymentDate()).getDays();

                   if(daysTillRentDue > 7)
                       return property;
                   else return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        getLogger().log(INFO, "Rent due notice sent to [{0}]", DAOUtils.mkPrintList(result.stream().map(PropertyBaseData::getPropertyAddress).collect(Collectors.toList())));
        return result;
    }


}
