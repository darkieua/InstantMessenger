package ua.sumdu.java.lab2.instant_messenger.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.instant_messenger.api.UserCreator;
import ua.sumdu.java.lab2.instant_messenger.entities.CategoryUsers;
import ua.sumdu.java.lab2.instant_messenger.entities.User;;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UserCreatorImpl implements UserCreator {

    private static final Logger LOG = LoggerFactory.getLogger(UserCreatorImpl.class);
    private static final String USERNAME_REG_EXP = "^[a-z0-9_-]{3,16}$";
    private static final String EMAIL_REG_EXP = "^([a-z0-9_\\.-]+)@([a-z0-9_\\.-]+)\\.([a-z\\.]{2,6})$";

    private static UserCreatorImpl instance;

    private UserCreatorImpl() {
    }

    public static UserCreatorImpl getInstance() {
        synchronized (UserCreatorImpl.class) {
            LOG.debug("Create a new UserCreator");
            if (instance == null) {
                instance = new UserCreatorImpl();
            }
            return instance;
        }
    }

    @Override
    public boolean validateUsername(String username) {
        LOG.debug("Validation username");
        Pattern pattern = Pattern.compile(USERNAME_REG_EXP);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    @Override
    public boolean validateEmail(String email) {
        LOG.debug("Validation e-mail");
        Pattern pattern = Pattern.compile(EMAIL_REG_EXP);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public User createUser(CategoryUsers category, String username, String email, InetAddress ipAddress, int port) throws UnknownHostException {
        LOG.info("Data validation and creation new user");
        if (this.validateUsername(username)&& this.validateEmail(email)) {
            return new User(category, username, email, port, ipAddress);
        } else {
            LOG.warn("Validation error");
            return new User();
        }

    }
}
