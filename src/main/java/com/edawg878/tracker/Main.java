package com.edawg878.tracker;

import com.edawg878.tracker.database.Database;
import com.edawg878.tracker.database.MySQLDatabase;
import com.edawg878.tracker.database.SQLiteDatabase;
import com.edawg878.tracker.settings.Backend;
import com.edawg878.tracker.settings.BackendSettings;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.logging.Level;

/**
 * @author EDawg878 <EDawg878@gmail.com>
 */
public class Main extends JavaPlugin {

    private final String FAILED_TO_CONVERT = "Failed to convert database: %s";
    private BackendSettings settings;
    private Database database;
    private boolean errorLoading;

    @Override
    public void onLoad() {
        settings = new BackendSettings(this);
        settings.saveDefault();
        settings.reload();
        database = getDatabase(settings.getBackend());
        boolean connected = database != null && database.connect();
        if (!connected) {
            getLogger().severe("Failed to connect to database");
            errorLoading = true;
            return;
        }

        Set<User> users = database.query();
        for (User user : users) {
            Tracker.add(user);
        }

        Backend oldBackend = settings.getConvert();
        if (oldBackend != null) {
            getLogger().info("Attempting to convert database...");
            if (oldBackend != settings.getBackend()) {
                if (users.isEmpty()) {
                    if (convert(oldBackend)) {
                        settings.getConfig().set("convert", "none");
                        settings.save();
                    } else {
                        getLogger().warning(FAILED_TO_CONVERT);
                    }
                } else {
                    getLogger().warning(String.format(FAILED_TO_CONVERT, "existing database must be empty"));
                }
            } else {
                System.out.println(oldBackend);
                System.out.println(settings.getBackend());
                getLogger().warning(String.format(FAILED_TO_CONVERT, "conversion backend cannot be the same as the current backend"));
            }
        }
        getLogger().info("Loaded " + users.size() + " users");
    }

    @Override
    public void onEnable() {
        if (errorLoading) {
            getLogger().severe("Error loading plugin");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this, database), this);
    }

    private Database getDatabase(Backend backend) {
        Database database = null;
        switch (backend) {
            case MYSQL:
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    database = new MySQLDatabase(this, settings);
                } catch (ClassNotFoundException e) {
                    getLogger().log(Level.SEVERE, "Error finding MySQL driver", e);
                }
                break;
            case SQLITE:
                try {
                    Class.forName("org.sqlite.JDBC");
                    database = new SQLiteDatabase(this, settings);
                } catch (ClassNotFoundException e) {
                    getLogger().log(Level.SEVERE, "Error finding SQLite driver", e);
                }
                break;
        }
        return database;
    }

    private boolean convert(Backend oldBackend) {
        Database oldDatabase = getDatabase(oldBackend);
        if (oldBackend != null && oldDatabase.connect()) {
            Set<User> users = oldDatabase.query();
            int converted = 0;
            for (User user : users) {
                Tracker.add(user);
                database.log(user.getName(), user.getUniqueId());
                converted++;
            }
            getLogger().info("Successfully converted " + converted + " users from old database");
            return true;
        }
        return false;
    }

}
