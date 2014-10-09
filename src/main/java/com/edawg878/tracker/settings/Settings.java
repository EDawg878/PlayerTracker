package com.edawg878.tracker.settings;

import com.google.common.base.Charsets;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author EDawg878 <EDawg878@gmail.com>
 */
public class Settings {

    private final Plugin plugin;
    private final File configFile;
    private final Logger logger;
    protected YamlConfiguration config;

    public Settings(Plugin plugin, String resource) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.configFile = new File(plugin.getDataFolder(), resource);
    }

    public void saveDefault() {
        if (!configFile.exists()) {
            plugin.getDataFolder().mkdirs();
            plugin.saveResource(configFile.getName(), false);
        }
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error saving config " + configFile, ex);
        }
    }

    public void load() {
        config = YamlConfiguration.loadConfiguration(configFile);
        InputStream defaultStream = plugin.getResource(configFile.getName());
        YamlConfiguration defaults = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream, Charsets.UTF_8));
        config.setDefaults(defaults);
    }

    public void reload() {
        if (config == null) {
            load();
        }
    }

    public File getConfigFile() {
        return configFile;
    }

    public FileConfiguration getConfig() {
        return config;
    }


}
