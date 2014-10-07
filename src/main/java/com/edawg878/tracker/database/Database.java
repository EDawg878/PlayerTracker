package com.edawg878.tracker.database;

import com.edawg878.tracker.User;

import java.util.Set;
import java.util.UUID;

/**
 * @author EDawg878 <EDawg878@gmail.com>
 */
public interface Database {

    public boolean connect();
    public Integer log(String username, UUID uuid);
    public void update(Integer id, String username);
    public Set<User> query();

}
