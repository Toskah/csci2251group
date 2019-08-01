package util;

import service.SlumlordConnection;
import oracle.jdbc.OracleConnection;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;
import static java.util.stream.Collectors.joining;

/**
 * Static methods supporting DAO operations (such as connection management)
 * @author Joshua Escareno
 */
public interface DAOUtils {

    /**
     * Name of the class holding database connection information in a Banner Batch Integration
     * Framework (BIF) context
     */
    String SLUMLORD_CONTEXT_CLASS = "change.me.to.a.valid.string";

    /**
     * Name of the static method returning a database connection in a Banner Batch Integration
     * Framework (BIF) context
     */
    String SLUMLORD_CONNECTION_METHOD = "getConnection";

    /**
     * Empty parameter list having correct syntax for a SQL "IN" clause
     */
    String EMPTY_PARAMETER_LIST = "(null)";

    /**
     * Default row prefetch size for Oracle database connections.
     */
    int DEFAULT_ROW_PREFETCH = 250;

    /**
     * Convert a SQL {@link RowId} to a printable string.
     *
     * @param rowId the row ID to be printed
     * @return the string representation of the row ID (not the same as RowId.toString())
     */
    static String asString(RowId rowId) {
        return new String(rowId.getBytes(), Charset.forName("ascii"));
    }

    /**
     * Null-safe version to convert a {@link LocalDate} value to a {@link Date}.
     *
     * @param date the LocalDate to convert to a java.sql.Date
     * @return the java.sql.Date, or null if the LocalDate was given as null
     */
    static Date asSqlDate(LocalDate date) {
        return date != null ? Date.valueOf(date) : null;
    }

    /**
     * Null-safe version to convert a {@link LocalDateTime} value to a {@link Timestamp}.
     *
     * @param timestamp the LocalDateTime to convert to a java.sql.Timestamp
     * @return the java.sql.Timestamp, or null if the LocalDateTime was given as null
     */
    static Timestamp asSqlTimestamp(LocalDateTime timestamp) {
        return timestamp != null ? Timestamp.valueOf(timestamp) : null;
    }

    /**
     * Retrieve a SQL database {@link Connection} object from the application context. This
     * method provides a context independent way of obtaining a database connection. Currently,
     * it supports obtaining database connections in the context of the Banner Batch Integration
     * Framework (BIF) used by Ellucian to execute batch jobs for the Banner ERP system, and a
     * method for standalone applications run from the cron or through Jenkins where connection
     * parameters are passed in as environment variables.
     *
     * <p><b>Note:</b> The context determines whether the {@link Connection} should be closed after
     * use. In the BIF context, the database connection must not be closed by the user since it
     * is needed by the BIF itself after the main job has finished. This is an issue that will
     * have to be addressed if we want to support another context such as a JNDI {@link DataSource}.
     *
     * @return the database connection
     * @throws RuntimeException if no suitable context is available to provide a DB connection
     */
    static Connection getConnection() {
        // Reflection is slightly nasty, but we cannot introduce a hard dependency on the
        // BatchResourceHolder class, since otherwise DAOUtils won't load when BatchResourceHolder
        // is unavailable on the classpath.
        Connection conn = null;

        /*try {
            //TODO: create the connection here for the database

            Class<?> brh = Class.forName(BANNER_BIF_CONTEXT_CLASS);
            Method connProvider = brh.getDeclaredMethod(SLUMLORD_CONNECTION_METHOD);

            conn = (Connection) connProvider.invoke(null);
        } catch (ClassCastException
                | ClassNotFoundException
                | IllegalAccessException
                | NoSuchMethodException
                | InvocationTargetException e) {
            // InvocationTargetException is what we get when the invoked method throws an
            // exception. That exception is then wrapped inside the InvocationTargetException.
        }*/

        if (conn == null) {
            conn = SlumlordConnection.getInstance();
        }

        if (conn instanceof OracleConnection) {
            try {
                OracleConnection oraConn = (OracleConnection) conn;
                int currentRowPrefetch = oraConn.getDefaultRowPrefetch();

                if (currentRowPrefetch != DEFAULT_ROW_PREFETCH) {
                    oraConn.setDefaultRowPrefetch(DEFAULT_ROW_PREFETCH);
                    Logger.getLogger(DAOUtils.class.getName()).log(INFO,
                            "Changed default row prefetch size from {0} to {1}.",
                            new Object[]{currentRowPrefetch, DEFAULT_ROW_PREFETCH});
                }
            } catch (SQLException e) {
                // swallow
            }
        }

        return conn;
    }

    /**
     * Retrieve a SQL {@code NUMBER NULL} type from a {@link ResultSet}, and return it as a
     * {@link Integer}.
     *
     * @param rs          the ResultSet containing the row data
     * @param columnIndex the index of the column containing the NUMBER value
     * @return the column value as an Integer, or null if the column was NULL
     * @throws SQLException when ResultSet.getInt() fails
     */
    static Integer getInteger(ResultSet rs, int columnIndex) throws SQLException {
        int value = rs.getInt(columnIndex);

        return rs.wasNull() ? null : value;
    }

    /**
     * Retrieve a SQL {@code NUMBER NULL} type from a {@link ResultSet}, and return it as a
     * {@link Integer}.
     *
     * @param rs          the ResultSet containing the row data
     * @param columnLabel the label of the column containing the NUMBER value
     * @return the column value as an Integer, or null if the column was NULL
     * @throws SQLException when ResultSet.getInt() fails
     */
    static Integer getInteger(ResultSet rs, String columnLabel) throws SQLException {
        int value = rs.getInt(columnLabel);

        return rs.wasNull() ? null : value;
    }

    /**
     * Retrieve a SQL {@code DATE} or {@code DATETIME} type from a {@link ResultSet}, and return
     * it as a {@link LocalDate}.
     *
     * @param rs          the ResultSet containing the row data
     * @param columnIndex the index of the column containing the DATE or DATETIME value
     * @return the column data as a LocalDate, or null if the column contains no data
     * @throws SQLException when ResultSet.getDate() fails
     */
    static LocalDate getLocalDate(ResultSet rs, int columnIndex) throws SQLException {
        Date d = rs.getDate(columnIndex);

        return d != null ? d.toLocalDate() : null;
    }

    /**
     * Retrieve a SQL {@code DATE} or {@code DATETIME} type from a {@link ResultSet}, and return
     * it as a {@link LocalDate}.
     *
     * @param rs          the ResultSet containing the row data
     * @param columnLabel the label of the column containing the DATE or DATETIME value
     * @return the column data as a LocalDate, or null if the column contains no data
     * @throws SQLException when ResultSet.getDate() fails
     */
    static LocalDate getLocalDate(ResultSet rs, String columnLabel) throws SQLException {
        Date d = rs.getDate(columnLabel);

        return d != null ? d.toLocalDate() : null;
    }

    /**
     * Retrieve a SQL {@code DATE} or {@code DATETIME} type from a {@link ResultSet}, and return
     * it as a {@link LocalDateTime}.
     *
     * @param rs          the ResultSet containing the row data
     * @param columnIndex the index of the column containing the DATE or DATETIME value
     * @return the column data as a LocalDateTime, or null if the column contains no data
     * @throws SQLException when ResultSet.getTimestamp() fails
     */
    static LocalDateTime getLocalDateTime(ResultSet rs, int columnIndex) throws SQLException {
        Timestamp ts = rs.getTimestamp(columnIndex);

        return ts != null ? ts.toLocalDateTime() : null;
    }

    /**
     * Retrieve a SQL {@code DATE} or {@code DATETIME} type from a {@link ResultSet}, and return
     * it as a {@link LocalDateTime}.
     *
     * @param rs          the ResultSet containing the row data
     * @param columnLabel the label of the column containing the DATE or DATETIME value
     * @return the column data as a LocalDateTime, or null if the column contains no data
     * @throws SQLException when ResultSet.getTimestamp() fails
     */
    static LocalDateTime getLocalDateTime(ResultSet rs, String columnLabel) throws SQLException {
        Timestamp ts = rs.getTimestamp(columnLabel);

        return ts != null ? ts.toLocalDateTime() : null;
    }

    /**
     * Return an "IN" clause list containing <em>n</em> replacement parameters. Used to
     * dynamically build SQL statements that contain IN clauses for variable number of arguments.
     *
     * @param n number of replacement parameters in this list
     * @return a string of the format "(?,...)" containing <em>n</em> replacement parameters
     */
    static String mkInList(int n) {
        return mkInList(n, 1);
    }

    /**
     * Return an "IN" clause list containing <em>n</em> replacement parameters of <em>m</em>
     * values each. Used to dynamically build SQL statements that contain IN clauses for variable
     * number of arguments.
     *
     * @param n number of replacement parameters in this list
     * @param m number of values in each replacement parameter
     * @return a string of the format "((?,...),...)" containing <em>n</em> replacement parameters
     */
    static String mkInList(int n, int m) {
        if (m < 1) throw new IllegalArgumentException("m must be >= 1, but is " + m);

        return n <= 0 ?
                EMPTY_PARAMETER_LIST :
                Collections.nCopies(n, m == 1 ? "?" : mkInList(m)).stream()
                        .collect(joining(",", "(", ")"));
    }

    /**
     * Convert a collection into a string representation for logging. Collections with more than 5
     * elements are simply printed as {@code "List(n)"}.
     *
     * @param collection the collection to be logged
     * @return a string representation of the collection, either the elements concatenated by
     * commas, or the string "List(n)"
     */
    static String mkPrintList(Collection<?> collection) {
        if (collection == null) return "null";

        int size = collection.size();

        if (size <= 5) {
            return collection.stream().map(Object::toString).collect(joining(","));
        } else {
            return String.format("List(%d)", size);
        }
    }

    /**
     * Convenience method implementing the SQL {@code NVL()} function. Useful if {@code nullable}
     * is the result of a function call and you want to avoid calling the function twice in case
     * the result is not {@code null}.
     *
     * @param nullable a value that may be null
     * @param ifNull   the default value to return if nullable is null
     * @param <T>      the type processed by this method
     * @return nullable if it is not null, otherwise ifNull
     */
    static <T> T nvl(T nullable, T ifNull) {
        return nullable != null ? nullable : ifNull;
    }

    /**
     * Executes a database query from a prepared statement, performing an action on each row of
     * the result.
     *
     * @param conn   the database connection
     * @param ps     the prepared statement to be executed, optionally containing {@code '?'}
     *               parameters
     * @param setter the functional interface providing parameters to the query
     * @param action the functional interface consuming a row from the ResultSet
     * @return the number of rows returned by the query, i.e., how often the action was called
     * @throws SQLException if the database query could not be executed
     */
    static int queryRows(
            Connection conn,
            PreparedStatement ps,
            ParameterSupplier setter,
            ResultSetConsumer action
    ) throws SQLException {
        if (setter != null) setter.setParameters(ps);

        try (ResultSet rs = ps.executeQuery()) {
            int rows = 0;

            while (rs.next()) {
                action.consume(rs);
                ++rows;
            }

            return rows;
        }
    }

    /**
     * Executes a database query, performing an action on each row of the result.
     *
     * @param conn   the database connection
     * @param query  the SQL query to be executed, optionally containing {@code '?'} parameters
     * @param setter the functional interface providing parameters to the query
     * @param action the functional interface consuming a row from the ResultSet
     * @return the number of rows returned by the query, i.e., how often the action was called
     * @throws SQLException if the database query could not be executed
     */
    static int queryRows(
            Connection conn,
            String query,
            ParameterSupplier setter,
            ResultSetConsumer action
    ) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            return queryRows(conn, ps, setter, action);
        }
    }

    /**
     * Executes a database query from a prepared statement for a list of objects. This is a more
     * specialized and parameterized version of the generic {@code queryRows()} method.
     *
     * @param conn   the database connection
     * @param ps     the prepared statement to be executed, optionally containing {@code '?'}
     *               parameters
     * @param setter the functional interface providing parameters to the query
     * @param mapper the functional interface mapping columns from the ResultSet to the returned
     *               object type
     * @param <E>    the object type to be returned by this query
     * @return a list of objects of type E, one element for each row in the ResultSet
     * @throws SQLException if the database query could not be executed
     */
    static <E> List<E> queryForList(
            Connection conn,
            PreparedStatement ps,
            ParameterSupplier setter,
            ResultSetMapper<E> mapper
    ) throws SQLException {
        List<E> result = new ArrayList<>();

        queryRows(conn, ps, setter, rs -> result.add(mapper.mapResult(rs)));

        return result;
    }

    /**
     * Executes a database query for a list of objects. This is a more specialized and
     * parameterized version of the generic {@code queryRows()} method.
     *
     * @param conn   the database connection
     * @param query  the SQL query to be executed, optionally containing {@code '?'} parameters
     * @param setter the functional interface providing parameters to the query
     * @param mapper the functional interface mapping columns from the ResultSet to the returned
     *               object type
     * @param <E>    the object type to be returned by this query
     * @return a list of objects of type E, one element for each row in the ResultSet
     * @throws SQLException if the database query could not be executed
     */
    static <E> List<E> queryForList(
            Connection conn,
            String query,
            ParameterSupplier setter,
            ResultSetMapper<E> mapper
    ) throws SQLException {
        List<E> result = new ArrayList<>();

        queryRows(conn, query, setter, rs -> result.add(mapper.mapResult(rs)));

        return result;
    }

    /**
     * Executes a database query from a prepared statement for a map of objects. The keys
     * extracted from the value types must be unique. This is a more specialized and
     * parameterized version of the generic {@code queryRows()} method.
     *
     * @param conn         the database connection
     * @param ps           the prepared statement to be executed, optionally containing {@code '?'}
     *                     parameters
     * @param setter       the functional interface providing parameters to the query
     * @param mapper       the functional interface mapping columns from the ResultSet to the
     *                     returned value type
     * @param keyExtractor the functional interface extracting the key from a value object
     * @param <K>          the key type used to index values in the map
     * @param <V>          the value type to be returned by this query
     * @return a map of value objects, one element for each row in the ResultSet
     * @throws SQLException if the database query could not be executed or if the map contains
     *                      duplicate keys
     */
    static <K, V> Map<K, V> queryForMap(
            Connection conn,
            PreparedStatement ps,
            ParameterSupplier setter,
            ResultSetMapper<V> mapper,
            Function<V, K> keyExtractor
    ) throws SQLException {
        Map<K, V> result = new HashMap<>();

        queryRows(conn, ps, setter, rs ->
                addValueToMap(result, mapper.mapResult(rs), keyExtractor));

        return result;
    }

    /**
     * Executes a database query for a map of objects. The keys extracted from the value types
     * must be unique. This is a more specialized and parameterized version of the generic
     * {@code queryRows()} method.
     *
     * @param conn         the database connection
     * @param query        the SQL query to be executed, optionally containing {@code '?'}
     *                     parameters
     * @param setter       the functional interface providing parameters to the query
     * @param mapper       the functional interface mapping columns from the ResultSet to the
     *                     returned value type
     * @param keyExtractor the functional interface extracting the key from a value object
     * @param <K>          the key type used to index values in the map
     * @param <V>          the value type to be returned by this query
     * @return a map of value objects, one element for each row in the ResultSet
     * @throws SQLException if the database query could not be executed or if the map contains
     *                      duplicate keys
     */
    static <K, V> Map<K, V> queryForMap(
            Connection conn,
            String query,
            ParameterSupplier setter,
            ResultSetMapper<V> mapper,
            Function<V, K> keyExtractor
    ) throws SQLException {
        Map<K, V> result = new HashMap<>();

        queryRows(conn, query, setter, rs ->
                addValueToMap(result, mapper.mapResult(rs), keyExtractor));

        return result;
    }

    /**
     * This is a <strong>private</strong> helper function used by the {@code queryForMap()} methods.
     *
     * @param map          the map to which the result should be added
     * @param value        the value to add to the map
     * @param keyExtractor the functional interface extracting the key from a value object
     * @param <K>          the key type used in the map
     * @param <V>          the value type used in the map
     * @throws SQLException if the key was already mapped to an existing value object
     */
    static <K, V> void addValueToMap(Map<K, V> map, V value, Function<V, K> keyExtractor)
            throws SQLException {
        K key = keyExtractor.apply(value);

        if (map.put(key, value) != null) {
            throw new SQLException("Duplicate key in mapped SQL result: " + key);
        }
    }

    @FunctionalInterface
    interface ParameterSupplier {
        void setParameters(PreparedStatement ps) throws SQLException;
    }

    @FunctionalInterface
    interface ResultSetConsumer {
        void consume(ResultSet rs) throws SQLException;
    }

    @FunctionalInterface
    interface ResultSetMapper<T> {
        T mapResult(ResultSet rs) throws SQLException;
    }
}
