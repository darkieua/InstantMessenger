package ua.sumdu.java.lab2.instant_messenger.api;

import ua.sumdu.java.lab2.instant_messenger.entities.User;

public interface UserMap {
    public abstract boolean addUser(User user);

    public abstract boolean removeUser(User user);
}