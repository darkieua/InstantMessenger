package com.nc.lab2.db_users;

import java.util.Map;

public abstract class AbstractUserMap {
    private Map<String, User> map;

    public Map<String, User> getUserMap() {
        return map;
    }

    public abstract boolean addUser(User user);

    public abstract boolean removeUser(User user);

}
