package ua.sumdu.java.lab2.messenger.processing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.api.UserCreator;
import ua.sumdu.java.lab2.messenger.entities.CategoryUsers;
import ua.sumdu.java.lab2.messenger.entities.User;

public enum UserCreatorImpl implements UserCreator {
    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(UserCreatorImpl.class);
    private static final String USERNAME_REG_EXP = "^[a-z0-9_-]{3,16}$";
    private static final String EMAIL_REG_EXP = "^([a-z0-9_\\.-]+)@([a-z0-9_\\.-]+)"
            + "\\.([a-z\\.]{2,6})$";

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
    public User createUser(CategoryUsers category, String username, String email,
                                                 InetAddress ipAddress, int port) throws UnknownHostException {
        LOG.debug("Data validation and creation new user");
        if (this.validateUsername(username) && this.validateEmail(email)) {
            return new User(category, username, email, port, ipAddress);
        } else {
            LOG.warn("Validation error");
            return User.getEmptyUser();
        }
    }

    /**
     * Converting a Json String to a UserMap.
     */

    public User toUser(String jsonString) {
        LOG.debug("Converting a Json String to a UserMap");
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.fromJson(jsonString, User.class);
    }
}
