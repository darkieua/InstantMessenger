package ua.sumdu.java.lab2.instant_messenger.common_entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class User implements Cloneable{

    private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

    private CategoryUsers category;
    private String username = "";
    private String email = "";
    private int port = -1;
    private InetAddress ipAddress;

    public User(CategoryUsers category, String username, String email, int port, InetAddress ipAddress) {
        LOGGER.info("Creating a new user");
        this.category = category;
        this.username = username;
        this.email = email;
        this.port = port;
        this.ipAddress = ipAddress;
    }

    public User() throws UnknownHostException {
        LOGGER.info("Creating an empty user");
        this.category = CategoryUsers.BLACKLIST;
        this.port = -1;
        this.ipAddress = InetAddress.getLocalHost();
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

    public User update(CategoryUsers category, String username, String email, InetAddress ipAddress, int port) {
        LOGGER.info("Update user");
        return this.setCategory(category).setUsername(username).setEmail(email).setIpAddress(ipAddress).setPort(port);
    }


    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public User setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public boolean equals(Object user) {
        if (user == null) {
            LOGGER.warn("Comparison GroupMapImpl with null");
            return false;
        } else if (user.getClass() == this.getClass()) {
            LOGGER.info("Object types are the same");
            User user1 = (User) user;
            return this.getEmail().equals(user1.getEmail()) && this.getUsername().equals(user1.getUsername())
                    && this.getCategory().equals(user1.getCategory()) && this.getPort() == user1.getPort()
                    && this.getIpAddress().equals(user1.getIpAddress());
        } else {
            LOGGER.warn("Comparing objects are different");
            return false;
        }
    }

    public int hashCode() {
        LOGGER.info("Using hashCode function");
        int res = 13;
        res += category.hashCode();
        res += 2*username.hashCode();
        res += 3*email.hashCode();
        res += 4*port;
        res += 5*ipAddress.hashCode();
        return res;
    }

    public User clone() throws CloneNotSupportedException{
        LOGGER.info("Clone object");
        return (User) super.clone();
    }
}
