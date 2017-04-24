package ua.sumdu.java.lab2.messenger.api;

import javafx.collections.ObservableList;
import ua.sumdu.java.lab2.messenger.entities.User;

import java.util.Map;

public interface UserMap {

    Map<String, User> getMap();
    
    void addUser(User user);

    void removeUser(User user);

    ObservableList<User> getAllUsers();
}