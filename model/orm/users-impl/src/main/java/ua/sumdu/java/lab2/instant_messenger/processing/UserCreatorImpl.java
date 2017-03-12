package ua.sumdu.java.lab2.instant_messenger.processing;

import ua.sumdu.java.lab2.instant_messenger.api.UserCreator;
import ua.sumdu.java.lab2.instant_messenger.common_entities.CategoryUsers;
import ua.sumdu.java.lab2.instant_messenger.common_entities.User;;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UserCreatorImpl implements UserCreator {

    private static UserCreatorImpl instance;

    private UserCreatorImpl() {
    }

    public static UserCreatorImpl getInstance() {
        synchronized (UserCreatorImpl.class) {
            if (instance == null) {
                instance = new UserCreatorImpl();
            }
            return instance;
        }
    }

    @Override
    public boolean validateUsername(String username) {
        Pattern pattern = Pattern.compile("^[a-z0-9_-]{3,16}$");
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    @Override
    public boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile("^([a-z0-9_\\.-]+)@([a-z0-9_\\.-]+)\\.([a-z\\.]{2,6})$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public User createUser(CategoryUsers category, String username, String email, InetAddress ipAddress, int port) throws UnknownHostException {
        if (this.validateUsername(username)&& this.validateEmail(email)) {
            return new User(category, username, email, port, ipAddress);
        } else {
            return new User();
        }

    }
}
