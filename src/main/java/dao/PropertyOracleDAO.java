package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PropertyOracleDAO extends AbstractOracleDAO implements PropertyDAO {



    public PropertyOracleDAO(Connection conn){
        super(conn);
    }

    @Override
    public List<ProprtyBaseData> listAllPropertiesByOwner(String ownerId) throws SQLException {
        return null;
    }
}
