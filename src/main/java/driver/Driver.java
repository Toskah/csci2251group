package driver;


import Serialize.SerialSender;
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
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.logging.Level.*;


/**
 * Author @Dexter Elliott
 * create instances of DAO Packages and creates methods that handle requests from the website
 */

public class Driver {
    String OwnerID = "ID"; //Place Holder until I can grab data from database and website
    String todo = null; //What the driver is supposed to do
    PropertyDAO DAO = DAOFactory.create(PropertyDAO.class);
    SlumlordDAO SDAO = DAOFactory.create(SlumlordDAO.class);
    TenantDAO TDAO = DAOFactory.create(TenantDAO.class);
    DBDriver DBD = new DBDriver();
    BigDecimal rent;

    public Driver() {
        try {
            List<PropertyDAO.PropertyBaseData> Properties = DAO.listAllPropertiesByOwner(OwnerID);
            getLogger().log(INFO, "Grabbing property list");
        } catch (SQLException e) {
            System.out.println("SQL Exception occurred");
            System.out.println(e.getErrorCode());
            getLogger().log(WARNING, "SQL try-catch caught something");
        }

    }

    /**
     * calls a set of methods depending on the command from the website
     * @param userCommand The number corresponding with the command that is being requested
     * @throws SQLException
     */
    public void decideCommand(int userCommand) throws SQLException {

        switch (userCommand) {
            case 1:
                todo = "Total Rent Due";
                firstNoPay(OwnerID);
                SecondNoPay(OwnerID);
                rent = totalRentDue(OwnerID);
                SerialSender.send(rent);
                break;
            case 2:
                todo = "Next Payment Due";
                upcomingRentalNotice(OwnerID);
                break;
            case 3:
                todo = "First missed payment";
                firstNoPay(OwnerID);
                break;
            case 4:
                todo = "Second missed payment";
                SecondNoPay(OwnerID);
                break;
            case 5:
                todo = "Third missed payment";
                ThirdNoPay(OwnerID);
                break;
            case 6:
                todo = "List all Vacancies";
                SerialSender.send(Vacancy());
                break;
            case 7:
                todo = "List all owned properties";
                SerialSender.send(DAO.listAllPropertiesByOwner(OwnerID));
                break;
            default:
                todo = "Invalid command";
                getLogger().log(SEVERE, "Invalid Command");
                break;

        }

    }

    /**
     * Will log info in case of an error
     *
     * @return the logged action
     */
    private static Logger getLogger() {
        return Logger.getLogger(Driver.class.getName());
    }

    /**
     * Serializes the list of properties by owner
     * @param ownerID The owner that is requesting the info
     * @return The list of Properties belonging to that owner
     */
    public List<Serializable> serializePropertyByOwnerList(String ownerID) {
        List<PropertyBaseData> result = null;
        try {
            result = DAO.listAllPropertiesByOwner(ownerID);
        } catch (Exception e) {
            getLogger().log(WARNING, "An error({0}) occurred fetching properties by ownerID",
                    e.getMessage().trim());
        }

        return null;
    }

    /**
     * Gets the total rent due for an owner before any late fees are applied
     * @param ownerID The owner requesting the info
     * @return The list of properties as well as the amount of money owed
     */
    public BigDecimal totalRentDue(String ownerID) {
        List<PropertyBaseData> result = null;
        try {
            result = DAO.listAllPropertiesByOwner(ownerID);
        } catch (Exception e) {
            getLogger().log(WARNING, "An error({0}) occurred fetching properties by ownerID",
                    e.getMessage().trim());
        }
        BigDecimal totalRent = new BigDecimal(0);

        for (PropertyBaseData prop : result) {
            totalRent.add(prop.getRentalFee());
        }
        return result.stream()
                .map(PropertyBaseData::getRentalFee)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.valueOf(0));
    }

    /**
     * Adds a property to a list of properties for an owner who have been sent a statement at 15 days prior to rent due
     * @param ownerID The owner that is requesting the info
     * @return The list of properties who have 15 days or less until rent is due
     * @throws SQLException
     */
    public List<PropertyBaseData> upcomingRentalNotice(String ownerID) throws SQLException {
        List<PropertyBaseData> result = DAO.listAllPropertiesByOwner(ownerID);

        result = result.stream()
                .map(property -> {
                    int daysTillRentDue = Period.between(LocalDate.now(), property.getLastPaymentDate()).getDays();

                    if (daysTillRentDue == 15)
                        return property;
                    else return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        getLogger().log(INFO, "Rent due notice sent to [{0}]", DAOUtils.mkPrintList(result.stream().map(PropertyBaseData::getPropertyAddress).collect(Collectors.toList())));
        SerialSender.send(result);
        return result;
    }

    /**
     * Makes a list of properties who have missed a payment by 7 days or more
     * @param ownerID The owner of the property in question
     * @return The list of properties that have missed the deadline
     * @throws SQLException
     */
    public List<PropertyBaseData> firstNoPay(String ownerID) throws SQLException {
        List<PropertyBaseData> result = DAO.listAllPropertiesByOwner(ownerID);

        result = result.stream()
                .map(property -> {
                    int daysSincePaid = Period.between(LocalDate.now(), property.getLastPaymentDate()).getDays();

                    if (daysSincePaid >= 37) {
                        rent.add(property.getRentalFee().multiply(new BigDecimal(1.15)));
                        return property;
                    } else return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        getLogger().log(INFO, "Rent due notice sent to [{0}]", DAOUtils.mkPrintList(result.stream().map(PropertyBaseData::getPropertyAddress).collect(Collectors.toList())));
        return result;
    }

    /**
     * Makes a list of properties who have missed two payments
     * @param ownerID The owner of the properties in question
     * @return The list of properties who have two missed payments
     * @throws SQLException
     */
    public List<PropertyBaseData> SecondNoPay(String ownerID) throws SQLException {
        List<PropertyBaseData> result = DAO.listAllPropertiesByOwner(ownerID);

        result = result.stream()
                .map(property -> {
                    int daysSincePaid = Period.between(LocalDate.now(), property.getLastPaymentDate()).getDays();

                    if (daysSincePaid >= 60) {
                        rent.add(property.getRentalFee().multiply(new BigDecimal(1.225)));
                        return property;
                    } else return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        getLogger().log(INFO, "Second Rent due notice sent to [{0}]", DAOUtils.mkPrintList(result.stream().map(PropertyBaseData::getPropertyAddress).collect(Collectors.toList())));
        return result;
    }

    /**
     * A list of properties who have not paid in 75 days and are to be evicted
     * @param ownerID The owner of the property in question
     * @return The list of properties to have eviction notices sent to
     * @throws SQLException
     */
    public List<PropertyBaseData> ThirdNoPay(String ownerID) throws SQLException {
        List<PropertyBaseData> result = DAO.listAllPropertiesByOwner(ownerID);
        List<PropertyBaseData> vacancies = DAO.listAllVacantProperties();
        result = result.stream()
                .map(property -> {
                    int daysSincePaid = Period.between(LocalDate.now(), property.getLastPaymentDate()).getDays();

                    if (daysSincePaid >= 75)
                        return property;
                    else return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        vacancies.addAll(result);
        getLogger().log(INFO, "Eviction notice sent to [{0}]", DAOUtils.mkPrintList(result.stream().map(PropertyBaseData::getPropertyAddress).collect(Collectors.toList())));
        return result;
    }

    /**
     * A list of all current vacancies
     * @return The list of all current vacancies
     * @throws SQLException
     */
    public List<PropertyBaseData> Vacancy() throws SQLException {
        List<PropertyBaseData> result = DAO.listAllVacantProperties();
        getLogger().log(INFO, "Getting list of all vacancies");
        return result;

    }


}
