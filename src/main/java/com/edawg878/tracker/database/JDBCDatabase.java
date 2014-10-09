package com.edawg878.tracker.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
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
            if (checkTable(meta, table)) {
                return true;
            } else {
                st.execute("CREATE TABLE `" + table + "` " + getTableSchema());
                return checkTable(meta, table);
            }
        }
    }

    private boolean checkTable(DatabaseMetaData meta, String table) throws SQLException {
        return meta.getTables(null, null, table, null).next();
    }

    @Override
    public boolean connect() {
        try (Connection conn = getConnection()) {
            return conn != null && createTable(conn, "players");
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error connecting to database", ex);
        }
        return false;
    }

}
