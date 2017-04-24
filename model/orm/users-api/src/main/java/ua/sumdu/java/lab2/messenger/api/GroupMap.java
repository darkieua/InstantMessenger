package ua.sumdu.java.lab2.messenger.api;

import ua.sumdu.java.lab2.messenger.entities.User;

import java.util.Map;

public interface GroupMap {

    Map<String, UserMap> getMap();

    void addUser(String chatName, User user);

    void deleteUser(String chatName, User user);

    GroupMap setMap(Map<String, UserMap> map);
}
