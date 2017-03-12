package ua.sumdu.java.lab2.instant_messenger.common_entities;

public class User implements Cloneable{
    private CategoryUsers category;
    private String username;
    private String email;
    private int port;
    private String ipAddress;

    public User(CategoryUsers category, String username, String email, int port, String ipAddress) {
        this.category = category;
        this.username = username;
        this.email = email;
        this.port = port;
        this.ipAddress = ipAddress;
    }

    public User() {
        this.category = CategoryUsers.BLACKLIST;
        this.port = -1;
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

    public User update(CategoryUsers category, String username, String email, String ipAddress, int port) {
        return this.setCategory(category).setUsername(username).setEmail(email).setIpAddress(ipAddress).setPort(port);
    }


    public String getIpAddress() {
        return ipAddress;
    }

    public User setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public boolean equals(Object user) {
        if (user == null) {
            return false;
        } else if (user.getClass() == this.getClass()) {
            User user1 = (User) user;
            return this.getEmail().equals(user1.getEmail()) && this.getUsername().equals(user1.getUsername())
                    && this.getCategory().equals(user1.getCategory()) && this.getPort() == user1.getPort()
                    && this.getIpAddress().equals(user1.getIpAddress());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return 13*(category.hashCode() + 2*username.hashCode() + 3*email.hashCode()
                + 4*port + 5 * ipAddress.hashCode());
    }

    public User clone() throws CloneNotSupportedException{
        return (User) super.clone();
    }
}
