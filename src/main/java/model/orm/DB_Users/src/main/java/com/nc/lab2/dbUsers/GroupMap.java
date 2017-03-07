package com.nc.lab2.dbUsers;

import java.util.HashMap;
import java.util.Map;


public abstract class GroupMap {
    private Map<String, UserMap> groupMap;

    public GroupMap() {
        groupMap = new HashMap<String, UserMap>();
    }

    abstract boolean add(String chatName, User user);

    abstract boolean deleteUser(String chatName, User user);

}
