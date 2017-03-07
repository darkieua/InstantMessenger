package com.nc.lab2.dbUsers;

import java.util.Map;

public abstract class UserMap {
    private Map<String, User> userlist;

    public Map<String, User> getUserlist() {
        return userlist;
    }

    public abstract boolean add(User user);

    public abstract boolean remove(User user);

}
