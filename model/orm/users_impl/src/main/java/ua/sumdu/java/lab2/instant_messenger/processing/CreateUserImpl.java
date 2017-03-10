package ua.sumdu.java.lab2.instant_messenger.processing;

import ua.sumdu.java.lab2.instant_messenger.api.CreateUser;
import ua.sumdu.java.lab2.instant_messenger.entities.User;

public class CreateUserImpl implements CreateUser {

    private static CreateUserImpl instance;

    private CreateUserImpl() {
    }

    public static CreateUserImpl getInstance() {
        if (instance == null) {
            instance = new CreateUserImpl();
        }
        return instance;
    }

    @Override
    public Boolean validateUsername(String username) {
        return false;
    }

    @Override
    public Boolean validateEmail(String email) {
        return false;
    }

    @Override
    public Boolean validatePort(int port) {
        return false;
    }

    @Override
    public User createUser(String category, String username, String email, String ipAddress, int port) {
        return null;
    }
}
