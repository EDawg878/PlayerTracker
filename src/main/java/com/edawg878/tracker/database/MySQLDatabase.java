package com.edawg878.tracker.database;

import com.edawg878.tracker.Tracker;
import com.edawg878.tracker.User;
import com.edawg878.tracker.settings.BackendSettings;
import com.edawg878.tracker.util.UUIDFetcher;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author EDawg878 <EDawg878@gmail.com>
 */
public class MySQLDatabase extends JDBCDatabase {

    private final Logger logger;
    private final BackendSettings settings;

    public MySQLDatabase(Plugin plugin, BackendSettings settings) {
        super(plugin.getLogger());
        this.logger = plugin.getLogger();
        this.settings = settings;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(settings.getMySQLURL(), settings.getMySQLUsername(), settings.getMySQLPassword());
    }

    @Override
    public String getTableSchema() {
        return "("
                + "`player_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "`player_uuid` BINARY(16) NOT NULL,"
                + "`last_username` varchar(16) NOT NULL,"
                + "PRIMARY KEY (player_id),"
                + "UNIQUE KEY (player_uuid)"
                + ");";
    }

    @Override
    public Integer log(String username, UUID uuid) {
        Integer id = null;
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("INSERT INTO `players` (`player_uuid`, `last_username`) VALUES(?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setBytes(1, UUIDFetcher.toBytes(uuid));
            ps.setString(2, username);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error logging player " + username, e);
        }
        return id;
    }

    @Override
    public void update(Integer id, String oldUsername, String newUsername) {
        try (Connection conn = getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE `players` SET `last_username` = ? WHERE `player_id` = ?")) {
                ps.setString(1, newUsername);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
            List<String> toUpdate = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM `players` WHERE `player_id` <> ? AND `last_username` = ?")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String username = rs.getString("last_username");
                    toUpdate.add(username);
                }
            }
            if (toUpdate.size() > 0) {
                try {
                    UUIDFetcher fetcher = new UUIDFetcher(toUpdate);
                    Map<String, UUID> map = fetcher.call();
                    for (Entry<String, UUID> entry : map.entrySet()) {
                        User user = Tracker.find(entry.getValue());
                        if (user != null) {
                            user.setName(entry.getKey());
                        }
                    }
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE `players` SET `last_username` = ? WHERE `player_uuid` = ?")) {
                        for (Entry<String, UUID> entry : map.entrySet()) {
                            ps.setString(1, entry.getKey());
                            ps.setBytes(2, UUIDFetcher.toBytes(entry.getValue()));
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error calling uuid fetcher:" + toUpdate.size() + " usernames failed to update", e);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating " + oldUsername + " to " + newUsername, e);
        }
    }

    @Override
    public Set<User> query() {
        Set<User> users = new HashSet<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT * FROM `players`")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer id = rs.getInt("player_id");
                String username = rs.getString("last_username");
                UUID uuid = UUIDFetcher.fromBytes(rs.getBytes("player_uuid"));
                users.add(new User(id, username, uuid));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing query", e);
        }
        return users;
    }
}
