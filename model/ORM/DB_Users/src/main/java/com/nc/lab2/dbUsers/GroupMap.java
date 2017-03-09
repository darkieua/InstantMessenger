package com.nc.lab2.dbUsers;

import java.util.HashMap;
import java.util.Map;


public abstract class GroupMap {

    Map<String, UserMap> groupMap;

    abstract boolean addUser(String chatName, User user);

    abstract boolean deleteUser(String chatName, User user);

}
