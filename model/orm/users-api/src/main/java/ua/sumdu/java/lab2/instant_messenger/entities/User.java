package ua.sumdu.java.lab2.instant_messenger.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class User implements Cloneable{

    private static final Logger LOG = LoggerFactory.getLogger(User.class);

    private CategoryUsers category;
    private String username = "";
    private String email = "";
    private int port = -1;
    private InetAddress ipAddress;

    public User(CategoryUsers category, String username, String email, int port, InetAddress ipAddress) {
        LOG.debug("Creating a new user");
        this.category = category;
        this.username = username;
        this.email = email;
        this.port = port;
        this.ipAddress = ipAddress;
    }

    public User() throws UnknownHostException {
        LOG.debug("Creating an empty user");
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
        LOG.info("Update user");
        return this.setCategory(category).setUsername(username).setEmail(email).setIpAddress(ipAddress).setPort(port);
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
        return "User{" +
                "category=" + category +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", port=" + port +
                ", ipAddress=" + ipAddress +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return port == user.port &&
                category == user.category &&
                Objects.equals(username, user.username) &&
                Objects.equals(email, user.email) &&
                Objects.equals(ipAddress, user.ipAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, username, email, port, ipAddress);
    }
}
