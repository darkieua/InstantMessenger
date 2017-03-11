package ua.sumdu.java.lab2.instant_messenger.api;

import ua.sumdu.java.lab2.instant_messenger.entities.CategoryUsers;
import ua.sumdu.java.lab2.instant_messenger.entities.User;

public interface CreateUser {

    Boolean validateUsername(String username);

    Boolean validateEmail(String email);

    User createUser(CategoryUsers category, String username, String email, String ipAddress, int port);

}
