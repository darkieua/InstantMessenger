package ua.sumdu.java.lab2.instant_messenger.api;


import ua.sumdu.java.lab2.instant_messenger.common_entities.User;

public interface UserMap {
    void addUser(User user);

    void removeUser(User user);
}