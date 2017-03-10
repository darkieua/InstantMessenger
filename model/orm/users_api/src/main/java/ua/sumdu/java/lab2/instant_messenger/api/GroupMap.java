package ua.sumdu.java.lab2.instant_messenger.api;

import ua.sumdu.java.lab2.instant_messenger.entities.User;

public interface GroupMap {

    abstract void addUser(String chatName, User user);

    abstract void deleteUser(String chatName, User user);
}
