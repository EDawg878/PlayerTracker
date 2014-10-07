package com.edawg878.tracker;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author EDawg878 <EDawg878@gmail.com>
 */
public class User {

    private String username;
    private final UUID uuid;
    private final Integer id;

    public User(@Nullable Integer id, String username, UUID uuid) {
        this.id = id;
        this.username = username;
        this.uuid = uuid;
    }

    public String getName() {
        return username;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    @Nullable
    public Integer getId() {
        return id;
    }

}
