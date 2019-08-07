package dao;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.Before;
import org.junit.Test;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.List;

import static junit.framework.TestCase.assertNotNull;


public class TenantDAOIntegrationTest {
    private static final String JDBC_CONNECTION = "jdbc:mysql://localhost/slumlord";
    private TenantDAO dao;


    @Before
    public void createConnectionAndDAO() throws SQLException {

        Connection conn = DriverManager.getConnection(JDBC_CONNECTION, "root", "password");
        dao = DAOFactory.create(TenantDAO.class, conn);
    }

    @Test
    public void canQueryTenantTable() throws SQLException {
        List<TenantDAO.TenantData> result = dao.listTenantsByProperty(11111);

        assertNotNull(result);
    }
}
