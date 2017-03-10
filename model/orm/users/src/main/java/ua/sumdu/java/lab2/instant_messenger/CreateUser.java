package ua.sumdu.java.lab2.instant_messenger;

public interface CreateUser {

    Boolean validateUsername(String username);

    Boolean validateEmail(String email);

    Boolean validatePort(int port);

    User createUser(String category, String username, String email, String ipAddress, int port);

}
