package com.edawg878.tracker;


import javax.annotation.Nullable;
import java.util.UUID;

import static com.google.common.base.Preconditions.*;

/**
 * @author EDawg878 <EDawg878@gmail.com>
 */
public class User {

    private String username;
    private final UUID uuid;
    private final Integer id;

    public User(Integer id, String username, UUID uuid) {
        this.id = checkNotNull(id, "id");
        this.username = checkNotNull(username, "username");
        this.uuid = checkNotNull(uuid, "uuid");
    }

    public void setName(String username) {
        this.username = username;
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
