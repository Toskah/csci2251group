package driver;


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

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;


/**
 * Author @Dexter Elliott
 */
// create instances of DAO Packages
// send to web client
public class Driver {
    String OwnerID = "ID"; //Place Holder until I can grab data from database and website
    String todo = null; //What the driver is supposed to do
    PropertyDAO DAO = DAOFactory.create(PropertyDAO.class);
    SlumlordDAO SDAO = DAOFactory.create(SlumlordDAO.class);
    TenantDAO TDAO = DAOFactory.create(TenantDAO.class);
    DBDriver DBD = new DBDriver();

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

    public void decideCommand(int userCommand) throws SQLException {
        //TODO decision logic
        //TODO fill dao by ownerID

        switch (userCommand) {
            case 1:
                todo = "Total Rent Due";
                totalRentDue(OwnerID);
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
                Vacancy();
            default:
                todo = "Invalid command";
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
        return result;
    }

    public List<PropertyBaseData> firstNoPay(String ownerID) throws SQLException {
        List<PropertyBaseData> result = DAO.listAllPropertiesByOwner(ownerID);

        result = result.stream()
                .map(property -> {
                    int daysSincePaid = Period.between(LocalDate.now(), property.getLastPaymentDate()).getDays();

                    if (daysSincePaid >= 37)
                        return property;
                    else return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        getLogger().log(INFO, "Rent due notice sent to [{0}]", DAOUtils.mkPrintList(result.stream().map(PropertyBaseData::getPropertyAddress).collect(Collectors.toList())));
        return result;
    }

    public List<PropertyBaseData> SecondNoPay(String ownerID) throws SQLException {
        List<PropertyBaseData> result = DAO.listAllPropertiesByOwner(ownerID);

        result = result.stream()
                .map(property -> {
                    int daysSincePaid = Period.between(LocalDate.now(), property.getLastPaymentDate()).getDays();

                    if (daysSincePaid >= 60)
                        return property;
                    else return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        getLogger().log(INFO, "Second Rent due notice sent to [{0}]", DAOUtils.mkPrintList(result.stream().map(PropertyBaseData::getPropertyAddress).collect(Collectors.toList())));
        return result;
    }

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

    public List<PropertyBaseData> Vacancy() throws SQLException {
        List<PropertyBaseData> result = DAO.listAllVacantProperties();
        getLogger().log(INFO, "Getting list of all vacancies");
        return result;

    }

}
