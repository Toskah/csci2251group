package dao;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;

public class TenantDAOIntegrationTest {
    private static final String JDBC_CONNECTION = "jdbc:mysql://localhost/slumlord";
    private TenantDAO dao;

    @BeforeAll
    public void initAll() throws SQLException {
        Connection conn = DriverManager.getConnection(JDBC_CONNECTION, "root", "password");
        dao = DAOFactory.create(TenantDAO.class, conn);
    }

    @Test
    public void canQueryTenantTables() throws SQLException {
        initAll();
        List<TenantDAO.TenantData> result = dao.listTenantsByProperty(1);

        assertNotNull(result);
    }
}
