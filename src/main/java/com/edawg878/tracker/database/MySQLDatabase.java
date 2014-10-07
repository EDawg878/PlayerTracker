package com.edawg878.tracker.database;

import com.edawg878.tracker.settings.BackendSettings;
import com.edawg878.tracker.User;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author EDawg878 <EDawg878@gmail.com>
 */
public class MySQLDatabase extends JDBCDatabase {

    private final Logger logger;
    private final BackendSettings settings;
    private static String TABLE_SCHEMA = "("
            + "`player_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,"
            + "`player_uuid` BINARY(16) NOT NULL,"
            + "`last_username` varchar(32) NOT NULL,"
            + "PRIMARY KEY (player_id),"
            + "UNIQUE KEY (player_uuid)"
            + ");";

    public MySQLDatabase(Plugin plugin, BackendSettings settings) throws ClassNotFoundException {
        super(plugin.getLogger());
        Class.forName("com.mysql.jdbc.Driver");
        this.logger = plugin.getLogger();
        this.settings = settings;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(settings.getMySQLURL(), settings.getMySQLUsername(), settings.getMySQLPassword());
    }

    @Override
    public String getTableSchema() {
        return TABLE_SCHEMA;
    }

    @Override
    public Integer log(String username, UUID uuid) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("INSERT INTO `" + BackendSettings.TABLE_NAME + "` (`player_uuid`, `last_username`) VALUES(?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setBytes(1, DatabaseUtil.toBinary(uuid));
            ps.setString(2, username);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error logging player " + username, e);
        }
        return null;
    }

    @Override
    public void update(Integer id, String username) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("UPDATE `" + BackendSettings.TABLE_NAME + "` SET `last_username` = ? WHERE `player_id` = ?")) {
            ps.setString(1, username);
            ps.setInt(2, id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating user for player " + username, e);
        }
    }

    @Override
    public Set<User> query() {
        Set<User> users = new HashSet<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT * FROM `" + BackendSettings.TABLE_NAME + "`")) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Integer id = rs.getInt("player_id");
                String username = rs.getString("last_username");
                UUID uuid = DatabaseUtil.fromBinary(rs.getBytes("player_uuid"));
                users.add(new User(id, username, uuid));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing query", e);
        }
        return users;
    }
}
