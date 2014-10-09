package com.edawg878.tracker;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author EDawg878 <EDawg878@gmail.com>
 */
public class Tracker {

    private static final Map<UUID, User> usersByUniqueId = new ConcurrentHashMap<>();
    private static final Map<Integer, User> usersById = new ConcurrentHashMap<>();
    private static final Map<String, User> usersByName = new ConcurrentHashMap<>();

    protected static void add(User user) {
        usersByUniqueId.put(user.getUniqueId(), user);
        usersById.put(user.getId(), user);
        usersByName.put(user.getName(), user);
    }

    @Nullable
    public static User find(UUID uuid) {
        return usersByUniqueId.get(uuid);
    }

    @Nullable
    public static User find(Integer id) {
        return usersById.get(id);
    }

    @Nullable
    public static User find(String username) {
        return usersByName.get(username);
    }

}
