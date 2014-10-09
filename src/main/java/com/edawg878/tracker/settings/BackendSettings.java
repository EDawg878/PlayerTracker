package com.edawg878.tracker.settings;

import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

/**
 * @author EDawg878 <EDawg878@gmail.com>
 */
public class BackendSettings extends Settings {

    private Logger logger;
    private Backend backend;
    private Backend convert;

    public BackendSettings(Plugin plugin) {
        super(plugin, "settings.yml");
    }

    @Override
    public void reload() {
        super.reload();
        backend = parseBackend(config.getString("backend"), Backend.MYSQL);
        convert = parseBackend(config.getString("convert"), null);
    }

    public Backend getBackend() {
        return backend;
    }

    public Backend getConvert() {
        return convert;
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
        } catch (Exception ex) {
            if(def != null) {
                logger.severe("Invalid backend specified, defaulting to " + def);
            }
        }
        return def;
    }
}
