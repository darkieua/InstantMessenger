package ua.sumdu.java.lab2.instant_messenger.api;

import ua.sumdu.java.lab2.instant_messenger.entities.CategoryUsers;
import ua.sumdu.java.lab2.instant_messenger.entities.User;

import java.net.InetAddress;
import java.net.UnknownHostException;

public interface UserCreator {

    boolean validateUsername(String username);

    boolean validateEmail(String email);

    User createUser(CategoryUsers category, String username, String email, InetAddress ipAddress, int port) throws UnknownHostException;

}
