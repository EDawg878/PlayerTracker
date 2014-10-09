package com.edawg878.tracker.database;

import com.edawg878.tracker.settings.BackendSettings;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author EDawg878 <EDawg878@gmail.com>
 */
public class SQLiteDatabase extends MySQLDatabase {

    private final Path database;
    private final Logger logger;

    public SQLiteDatabase(Plugin plugin, BackendSettings settings) {
        super(plugin, settings);
        logger = plugin.getLogger();
        database = plugin.getDataFolder().toPath().resolve("players.db");
    }

    @Override
    public Connection getConnection() throws SQLException {
        createDatabase();
        return DriverManager.getConnection("jdbc:sqlite:" + database);
    }

    private void createDatabase() {
        if(!Files.exists(database)) {
            try {
                Files.createFile(database);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error creating SQLite database", e);
            }
        }
    }

    @Override
    public String getTableSchema() {
        return "("
                + "`player_id` INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "`player_uuid` BLOB(16) NOT NULL UNIQUE,"
                + "`last_username` VARCHAR(16) NOT NULL"
                + ")";
    }

}
