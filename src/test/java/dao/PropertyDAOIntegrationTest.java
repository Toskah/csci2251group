package dao;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class PropertyDAOIntegrationTest {
    private static final String JDBC_CONNECTION = "jdbc:mysql://localhost/slumlord";
    private static PropertyDAO dao;

    @Before
    public void createConnectionAndDAO() throws SQLException{
        Connection conn = DriverManager.getConnection(JDBC_CONNECTION, "root", "password");
        dao = DAOFactory.create(PropertyDAO.class, conn);
    }
    @Test
    public void canConnectToDatabaseAndQueryPropertyTable() throws SQLException {
       createConnectionAndDAO();
    }

    @Test
    public void canRetrieveVacantProperrties() throws SQLException{
        createConnectionAndDAO();
        List<PropertyDAO.PropertyBaseData> result = dao.listAllVacantProperties();
    }


}
