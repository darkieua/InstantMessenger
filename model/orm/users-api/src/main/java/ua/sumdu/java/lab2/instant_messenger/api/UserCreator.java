package ua.sumdu.java.lab2.instant_messenger.api;

import ua.sumdu.java.lab2.instant_messenger.common_entities.CategoryUsers;
import ua.sumdu.java.lab2.instant_messenger.common_entities.User;

import java.net.InetAddress;

public interface UserCreator {

    Boolean validateUsername(String username);

    Boolean validateEmail(String email);

    User createUser(CategoryUsers category, String username, String email, InetAddress ipAddress, int port);

}
