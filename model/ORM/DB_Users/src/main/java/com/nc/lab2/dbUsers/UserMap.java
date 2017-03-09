package com.nc.lab2.dbUsers;

import java.util.Map;

public abstract class UserMap {
    private Map<String, User> userMap;

    public Map<String, User> getUserMap() {
        return userMap;
    }

    public abstract boolean addUser(User user);

    public abstract boolean removeUser(User user);

}
