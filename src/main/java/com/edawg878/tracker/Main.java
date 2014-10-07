package com.edawg878.tracker;

import com.edawg878.tracker.database.Database;
import com.edawg878.tracker.database.MySQLDatabase;
import com.edawg878.tracker.settings.BackendSettings;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.logging.Level;

/**
 * @author EDawg878 <EDawg878@gmail.com>
 */
public class Main extends JavaPlugin {

    private BackendSettings settings;
    private Database database;
    private boolean errorLoading;

    @Override
    public void onLoad() {
        settings = new BackendSettings(this);
        settings.saveDefault();
        settings.reload();
        boolean connected = connectToDatabase();
        if(!connected) {
            getLogger().severe("Failed to connect to database");
            errorLoading = true;
            return;
        }
        Set<User> users = database.query();
        for(User user : users) {
            Tracker.add(user);
        }
        getLogger().info("Loaded " + users.size() + " users");
    }

    private boolean connectToDatabase() {
        try {
            switch (settings.getBackend()) {
                case MYSQL:
                    database = new MySQLDatabase(this, settings);
            }
        } catch (ClassNotFoundException e) {
            getLogger().log(Level.SEVERE, "Unable to connect to database", e);
        }
        return database.connect();
    }

    @Override
    public void onEnable() {
        if(errorLoading) {
            getLogger().severe("Error loading plugin");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this, database), this);
    }


}
