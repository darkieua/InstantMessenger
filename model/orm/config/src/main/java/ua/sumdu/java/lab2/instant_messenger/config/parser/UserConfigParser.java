package ua.sumdu.java.lab2.instant_messenger.config.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.instant_messenger.entities.CategoryUsers;
import ua.sumdu.java.lab2.instant_messenger.entities.User;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;

public final class UserConfigParser {

    private static final Logger LOG = LoggerFactory.getLogger(UserConfigParser.class);

    public static User getCurrentUser(){
        try {
            String genreJson = IOUtils.toString(new FileReader(getUserConfigFile()));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(genreJson);
            String username = node.get("username").asText();
            String email = node.get("email").asText();
            int port = node.get("port").asInt();
            String host = node.get("ipAddress").asText();
            return new User(CategoryUsers.CURRENT_USER, username, email, port, InetAddress.getByName(host));
        } catch (IOException e) {
            LOG.error("Config file not found",e);
            return User.getEmptyUser();
        }
    }

    public static File getFriendsFile() {
        return new File("model/orm/users_impl/src/test/resources/friends.json");
    }

    public static File getGroupsFile() {
        return new File("model/orm/users_impl/src/test/resources/groups.json");
    }

    public static File getUserConfigFile() {
        return new File("model/orm/config/src/main/resources/user_config.json");
    }

    public static String getURLMessageDirectory() {
        return "";
    }
}
