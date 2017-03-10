package ua.sumdu.java.lab2.instant_messenger;

import java.util.Map;


public abstract class AbstractGroupMap {

    Map<String, AbstractUserMap> map;

    abstract boolean addUser(String chatName, User user);

    abstract boolean deleteUser(String chatName, User user);

}
