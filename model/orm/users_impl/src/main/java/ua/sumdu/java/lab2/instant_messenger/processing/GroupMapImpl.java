package ua.sumdu.java.lab2.instant_messenger.processing;

import ua.sumdu.java.lab2.instant_messenger.api.GroupMap;
import ua.sumdu.java.lab2.instant_messenger.api.UserMap;
import ua.sumdu.java.lab2.instant_messenger.entities.User;

import java.util.Map;
import java.util.TreeMap;

public class GroupMapImpl implements GroupMap {

    Map<String, UserMap> map = new TreeMap<>();

    @Override
    public void addUser(String chatName, User user) {

    }

    @Override
    public void deleteUser(String chatName, User user) {

    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
