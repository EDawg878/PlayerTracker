package com.edawg878.tracker.settings;

import org.bukkit.plugin.Plugin;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author EDawg878 <EDawg878@gmail.com>
 */
public class BackendSettings extends Settings {

    private Logger logger;
    private Backend backend;
    public static final String TABLE_NAME = "players";

    public BackendSettings(Plugin plugin) {
        super(plugin, "settings.yml");
    }

    @Override
    public void reload() {
        super.reload();
        backend = parseBackend(config.getString("backend"), Backend.MYSQL);
    }

    public Backend getBackend() {
        return backend;
    }

    public String getMySQLURL() {
        return config.getString("settings.mysql.url");
    }

    public String getMySQLUsername() {
        return config.getString("settings.mysql.username");
    }

    public String getMySQLPassword() {
        return config.getString("settings.mysql.password");
    }

    private Backend parseBackend(String str, Backend def) {
        try {
            return Backend.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE, "Invalid backend specified, defaulting to " + def);
        }
        return def;
    }
}
