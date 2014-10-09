package com.edawg878.tracker;

import com.edawg878.tracker.database.Database;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.UUID;

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
        final User user = Tracker.find(event.getUniqueId());
        if(user == null) {
            addNewUser(event.getName(), event.getUniqueId());
        } else {
            checkNameChange(user, event.getName());
        }
    }

    private void checkNameChange(final User user, final String newUsername) {
        if(!newUsername.equals(user.getName())) {
            scheduler.runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    final String oldUsername = user.getName();
                    user.setName(newUsername);
                    database.update(user.getId(), oldUsername, newUsername);
                }
            });
        }
    }

    private void addNewUser(final String username, final UUID uuid) {
        scheduler.runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Integer id = database.log(username, uuid);
                if (id != null) {
                    Tracker.add(new User(id, username, uuid));
                }
            }
        });
    }
}
