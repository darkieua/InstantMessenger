package ua.sumdu.java.lab2.instant_messenger.api;

import ua.sumdu.java.lab2.instant_messenger.entities.User;

public interface UserMap {
    public abstract void addUser(User user);

    public abstract void removeUser(User user);
}