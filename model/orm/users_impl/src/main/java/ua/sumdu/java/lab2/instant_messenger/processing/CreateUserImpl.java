package ua.sumdu.java.lab2.instant_messenger.processing;

import ua.sumdu.java.lab2.instant_messenger.api.CreateUser;
import ua.sumdu.java.lab2.instant_messenger.entities.CategoryUsers;
import ua.sumdu.java.lab2.instant_messenger.entities.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Pattern p = Pattern.compile("^[a-z0-9_-]{3,16}$");
        Matcher m = p.matcher(username);
        return m.matches();
    }

    @Override
    public Boolean validateEmail(String email) {
        Pattern p = Pattern.compile("^([a-z0-9_\\.-]+)@([a-z0-9_\\.-]+)\\.([a-z\\.]{2,6})$");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    @Override
    public User createUser(CategoryUsers category, String username, String email, String ipAddress, int port) {
        if (validateUsername(username)&& validateEmail(email)) {
            return new User(category, username, email, port, ipAddress);
        } else {
            return null;
        }

    }
}
