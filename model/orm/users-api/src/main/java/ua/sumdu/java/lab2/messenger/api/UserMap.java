package ua.sumdu.java.lab2.messenger.api;

import ua.sumdu.java.lab2.messenger.entities.User;

public interface UserMap {
  
  void addUser(User user);

  void removeUser(User user);
}