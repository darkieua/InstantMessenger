package ua.sumdu.java.lab2.instant_messenger.processing;

import ua.sumdu.java.lab2.instant_messenger.api.UserMap;
import ua.sumdu.java.lab2.instant_messenger.entities.User;

import java.util.Map;
import java.util.TreeMap;

public class UserMapImpl implements UserMap {

    Map<String, User> map = new TreeMap<>();

    @Override
    public void addUser(User user) {
    }

    @Override
    public void removeUser(User user) {

    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
