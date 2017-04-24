package ua.sumdu.java.lab2.messenger.api;

import java.net.InetAddress;
import java.net.UnknownHostException;
import ua.sumdu.java.lab2.messenger.entities.CategoryUsers;
import ua.sumdu.java.lab2.messenger.entities.User;

public interface UserCreator {

    boolean validateUsername(String username);

    boolean validateEmail(String email);

    User createUser(CategoryUsers category, String username, String email, InetAddress ipAddress,
                    int port) throws UnknownHostException;

}
