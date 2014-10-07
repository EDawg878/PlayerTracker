package com.edawg878.tracker;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author EDawg878 <EDawg878@gmail.com>
 */
public class Tracker {

    protected static final Map<String, User> usersByName = new ConcurrentHashMap<>();
    protected static final Map<UUID, User> usersByUniqueId = new ConcurrentHashMap<>();
    protected static final Map<Integer, User> usersById = new ConcurrentHashMap<>();

    protected static void add(User user) {
        usersByName.put(user.getName(), user);
        usersByUniqueId.put(user.getUniqueId(), user);
        usersById.put(user.getId(), user);
    }

    @Nullable
    public static User getCached(String username) {
        return usersByName.get(username);
    }

    @Nullable
    public static User getCached(UUID uuid) {
        return usersByUniqueId.get(uuid);
    }

    @Nullable
    public static User getCached(Integer id) {
        return usersById.get(id);
    }

    public static User get(String username) {
        User user = getCached(username);
        if(user != null) {
            return user;
        } else {
            OfflinePlayer player = Bukkit.getOfflinePlayer(username);
            return new User(null, player.getName(), player.getUniqueId());
        }
    }

    public static User get(UUID uuid) {
        User user = getCached(uuid);
        if(user != null) {
            return user;
        } else {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            return new User(null, player.getName(), player.getUniqueId());
        }
    }

}
