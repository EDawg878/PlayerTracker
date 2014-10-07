package com.edawg878.tracker;

import com.edawg878.tracker.database.Database;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * @author EDawg878 <EDawg878@gmail.com>
 */
public class PlayerListener implements Listener {

    private final Database database;
    private final Plugin plugin;
    private final BukkitScheduler scheduler;

    public PlayerListener(Plugin plugin, Database database) {
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
        this.database = database;
    }

    @EventHandler
    public void onPreLogin(final AsyncPlayerPreLoginEvent event) {
        if(Tracker.usersByUniqueId.containsKey(event.getUniqueId())) {
            final User user = Tracker.usersByUniqueId.get(event.getUniqueId());
            if(!event.getName().equals(user.getName())) {
                scheduler.runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        database.update(user.getId(), event.getName());
                    }
                });
            }
        } else {
            scheduler.runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    Integer id = database.log(event.getName(), event.getUniqueId());
                    if(id != null) {
                        Tracker.add(new User(id, event.getName(), event.getUniqueId()));
                    }
                }
            });
        }
    }

}
