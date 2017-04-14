package ua.sumdu.java.lab2.messenger.api;

import ua.sumdu.java.lab2.messenger.entities.User;

public interface GroupMap {

  void addUser(String chatName, User user);

  void deleteUser(String chatName, User user);
}
