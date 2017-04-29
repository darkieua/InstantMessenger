package ua.sumdu.java.lab2.messenger.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.Objects;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class User implements Cloneable, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(User.class);

    private CategoryUsers category;
    private String username = "";
    private String email = "";
    private int port = -1;
    private InetAddress ipAddress;


    /**
     * Create a new user.
     */
    public User(CategoryUsers category, String username, String email, int port,
                            InetAddress ipAddress) {
        LOG.debug("Creating a new user");
        this.category = category;
        this.username = username;
        this.email = email;
        this.port = port;
        this.ipAddress = ipAddress;
    }

    private User() {

    }

    public static User getEmptyUser() {
        LOG.debug("Creating an empty user");
        return new User().setCategory(CategoryUsers.EMPTY_USER);
    }

    public CategoryUsers getCategory() {
        return category;
    }

    public User setCategory(CategoryUsers category) {
        this.category = category;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public int getPort() {
        return port;
    }

    public User setPort(int port) {
        this.port = port;
        return this;
    }

    /**
     * Updates of user data.
     */

    public User update(CategoryUsers category, String username, String email,
                                         InetAddress ipAddress, int port) {
        LOG.debug("Update user");
        return this.setCategory(category)
                .setUsername(username)
                .setEmail(email)
                .setIpAddress(ipAddress)
                .setPort(port);
    }


    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public User setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    @Override
    public String toString() {
        return "User{" + "category=" + category + ", username='" + username + '\''
                + ", email='" + email + '\'' + ", port=" + port + ", ipAddress=" + ipAddress + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User user = (User) obj;
        return port == user.port && category == user.category
                && Objects.equals(username, user.username) && Objects.equals(email, user.email)
                && Objects.equals(ipAddress, user.ipAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, username, email, port, ipAddress);
    }

    /**
     * Converting a User to a Json String.
     */

    public String toJSonString() {
        LOG.debug("Converting a User to a Json String");
        return new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(this);
    }

    /**
     * Restore the current profile data.
     */

    public static User getCurrentUser() {
        try {
            String genreJson = IOUtils.toString(new FileReader(new File(getUserConfigPath())));
            JsonNode node = new ObjectMapper().readTree(genreJson);
            String username = node.get("username").asText();
            String email = node.get("email").asText();
            int port = node.get("port").asInt();
            String host = node.get("ipAddress").asText();
            return new User(CategoryUsers.CURRENT_USER, username, email, port,
                            InetAddress.getByName(host));
        } catch (IOException e) {
            LOG.error("Config file not found",e);
            return User.getEmptyUser();
        }
    }

    public static long getLastLoginTime() {
        try {
            String genreJson = IOUtils.toString(new FileReader(new File(getUserConfigPath())));
            JsonNode node = new ObjectMapper().readTree(genreJson);
            return Long.parseLong(node.get("lastLoginTime").asText());
        } catch (IOException e) {
            LOG.error("Config file not found",e);
            return 0;
        }
    }

    public static String getFriendsPath() {
        return getUserHome() + "/InstantMessenger/friends.json";
    }

    public static String getBlackListPath() {
        return getUserHome() + "/InstantMessenger/blacklist.json";
    }

    public static String getGroupsPath() {
        return getUserHome() + "/InstantMessenger/groups.json";
    }

    public static String getUserConfigPath() {
        File mainDirectory = new File(getUserHome() + "/InstantMessenger/");
        mainDirectory.mkdir();
        return getUserHome() + "/InstantMessenger/user_config.json";
    }

    /**
     * The method returns the path to the message folder.
     */

    public static String getUrlMessageDirectory() {
        File instant = new File(getUserHome() + "/InstantMessenger/messages/");
        if (!instant.exists()) {
            instant.mkdirs();
        }
        return instant.getPath();
    }

    private static String getUserHome() {
        return System.getProperty("user.home") + "/AppData/Local/";
    }

    public static String getDirectoryForDownloadFiles() {
        try {
            String genreJson = IOUtils.toString(new FileReader(new File(getUserConfigPath())));
            JsonNode node = new ObjectMapper().readTree(genreJson);
            return node.get("downloadPath").asText();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * The method returns the path to the system message file.
     */

    public static File getSystemMessageFile() {
        synchronized (User.class) {
            File system = new File(getUrlMessageDirectory() + "/system.xml");
            if (!system.exists()) {
                try {
                    system.createNewFile();
                } catch (IOException e) {
                    LOG.error("System file not found",e);
                }
            }
            return system;
        }
    }

    public static File getNewMessageFile() {
        synchronized (User.class) {
            File newMessages = new File(getUrlMessageDirectory() + "/newMessages.json");
            if (!newMessages.exists()) {
                try {
                    newMessages.createNewFile();
                } catch (IOException e) {
                    LOG.error("New messages file not found",e);
                }
            }
            return newMessages;
        }
    }
}
