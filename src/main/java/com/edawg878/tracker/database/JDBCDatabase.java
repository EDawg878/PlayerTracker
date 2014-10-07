package com.edawg878.tracker.database;

import com.edawg878.tracker.settings.BackendSettings;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author EDawg878 <EDawg878@gmail.com>
 */
public abstract class JDBCDatabase implements Database {

    private final Logger logger;

    public JDBCDatabase(Logger logger) {
        this.logger = logger;
    }

    public abstract Connection getConnection() throws SQLException;
    public abstract String getTableSchema();

    public boolean createTable(Connection conn, String table) throws SQLException {
        try (Statement st = conn.createStatement()) {
            DatabaseMetaData meta = conn.getMetaData();
            boolean found = meta.getTables(null, null, table, null).next();
            if(found) {
                return true;
            } else {
                st.execute("CREATE TABLE `" + table + "` " + getTableSchema());
                return meta.getTables(null, null, table, null).next();
            }
        }
    }

    @Override
    public boolean connect() {
        try (Connection conn = getConnection()) {
            return conn != null && createTable(conn, BackendSettings.TABLE_NAME);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error connecting to database", ex);
        }
        return false;
    }

}
