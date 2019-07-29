package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * A base class from which all Oracle specific Data Access Objects can be derived. Common
 * functionality surrounding DAO lifecycle and dependencies should be defined in this abstract
 * class.
 * <p>
 * The only dependency that DAO classes need is a database connection. This can be an exclusive
 * connection (in BIF contexts, for example), or a pooled connection (in web service environments).
 * Treatment of these connections is different; pooled connections need to be released back to the
 * pool when the resource goes out of scope, i.e., when the {@code close()} method is called by
 * the DI context. TODO: Not sure how to handle this correctly in all possible situations.
 * For now, connections are <b>NOT</b> being released when the DAO is closed.
 */
public abstract class AbstractOracleDAO implements BaseDAO {
    protected static final int DEFAULT_COLLATE_SIZE = 1000;

    private final Connection conn;
    private final int collateSize;

    protected AbstractOracleDAO(Connection conn) {
        this(conn, DEFAULT_COLLATE_SIZE);
    }

    protected AbstractOracleDAO(Connection conn, int collateSize) {
        this.conn = conn;
        this.collateSize = collateSize;
    }

    protected Connection getConn() {
        return conn;
    }

    protected int getCollateSize() {
        return collateSize;
    }

    /**
     * Perform a collated database query operation, collecting the results into a single list.
     *
     * @param elements       a list of arguments to query
     * @param queryProcessor a function querying for objects of type R given a list of at most
     *                       {@code collateSize} objects of type E for each round
     * @param <E>            the argument type
     * @param <R>            the result type
     * @return a list of elements of type R, assembled from possibly many individual sublists
     * returned by the queryProcessor
     * @throws SQLException if the database query could not be executed
     */
    protected <E, R> List<R> collatedListExecutor(
            List<E> elements,
            QueryProcessor<List<E>, List<R>> queryProcessor
    ) throws SQLException {
        int length = elements.size();
        List<E> subList = elements.subList(0, Math.min(collateSize, length));
        List<R> result = queryProcessor.apply(subList);

        for (int i = collateSize; i < length; i += collateSize) {
            subList = elements.subList(i, Math.min(i + collateSize, length));
            result.addAll(queryProcessor.apply(subList));
        }

        return result;
    }

    /**
     * Perform a collated database query operation, collecting the results into a single map.
     *
     * @param elements       the list of arguments to query
     * @param queryProcessor a function querying for maps from K to V given a list of at most
     *                       {@code collateSize} objects of type E for each round
     * @param <E>            the argument type
     * @param <K>            the key type of the result map
     * @param <V>            the value type of the result map
     * @return a map from K to V, assembled from possibly many partial maps returned by the
     * queryProcessor
     * @throws SQLException if the database query could not be executed
     */
    protected <E, K, V> Map<K, V> collatedMapExecutor(
            List<E> elements,
            QueryProcessor<List<E>, Map<K, V>> queryProcessor
    ) throws SQLException {
        int length = elements.size();
        List<E> subList = elements.subList(0, Math.min(collateSize, length));
        Map<K, V> result = queryProcessor.apply(subList);

        for (int i = collateSize; i < length; i += collateSize) {
            subList = elements.subList(i, Math.min(i + collateSize, length));
            result.putAll(queryProcessor.apply(subList));
        }

        return result;
    }

    /**
     * Perform a collated database update operation, totaling the number of rows affected.
     *
     * @param elements       the list of arguments to update
     * @param queryProcessor a function performing the update and returning the number of rows
     *                       affected for each round
     * @param <E>            the argument type
     * @return the total number of rows affected, from all executions of the queryProcessor
     * @throws SQLException if the database query could not be executed
     */
    protected <E> int collatedUpdateExecutor(
            List<E> elements,
            QueryProcessor<List<E>, Integer> queryProcessor
    ) throws SQLException {
        int length = elements.size();
        List<E> subList = elements.subList(0, Math.min(collateSize, length));
        int totalRows = queryProcessor.apply(subList);

        for (int i = collateSize; i < length; i += collateSize) {
            subList = elements.subList(i, Math.min(i + collateSize, length));
            totalRows += queryProcessor.apply(subList);
        }

        return totalRows;
    }

    /**
     * Prepare a {@link CallableStatement} as part of DAO initialization. This method is meant to
     * be called from a constructor in a subclass, and therefore does not throw checked exceptions,
     * nor can it be overridden in a subclass.
     *
     * @param call the call statement to be prepared, including replacement parameters
     * @return the prepared CallableStatement for later use
     */
    protected final CallableStatement prepareCall(String call) {
        try {
            return conn.prepareCall(call);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to prepare SQL call:\n" + call, e);
        }
    }

    /**
     * Prepare a {@link PreparedStatement} as part of DAO initialization. This method is meant to
     * be called from a constructor in a subclass, and therefore does not throw checked exceptions,
     * nor can it be overridden in a subclass. The pseudo parameter {@code "??"} in the query is
     * replaced with the {@code inList} argument before the query is prepared.
     *
     * @param queryTemplate the query template to be prepared, including replacement parameters, and
     *                      the special "??" parameter that is to be replaced by inList
     * @param inList        the list of parameters suitable for a collated query
     * @return the PreparedStatement for later use
     */
    protected final PreparedStatement prepareCollatedQuery(
            String queryTemplate,
            String inList
    ) {
        return prepareQuery(queryTemplate.replace("??", inList));
    }

    /**
     * Prepare a {@link PreparedStatement} as part of DAO initialization. This method is meant to
     * be called from a constructor in a subclass, and therefore does not throw checked exceptions,
     * nor can it be overridden in a subclass.
     *
     * @param query the query to be prepared, including replacement parameters
     * @return the PreparedStatement for later use
     */
    protected final PreparedStatement prepareQuery(String query) {
        try {
            return conn.prepareStatement(query);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to prepare SQL query:\n" + query, e);
        }
    }

    @FunctionalInterface
    interface QueryProcessor<T, R> {
        R apply(T t) throws SQLException;
    }
}
