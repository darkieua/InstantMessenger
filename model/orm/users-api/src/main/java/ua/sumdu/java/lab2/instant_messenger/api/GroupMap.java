package ua.sumdu.java.lab2.instant_messenger.api;

import ua.sumdu.java.lab2.instant_messenger.common_entities.User;

public interface GroupMap {

    void addUser(String chatName, User user);

    void deleteUser(String chatName, User user);
}
