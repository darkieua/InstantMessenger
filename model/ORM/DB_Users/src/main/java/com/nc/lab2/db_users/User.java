package com.nc.lab2.db_users;

public class User {
    private String category;
    private String username;
    private String email;
    private int port;
    private String IPAddress;

    public String getCategory() {
        return category;
    }

    public User setCategory(String category) {
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

    public User update(String category, String username, String email, String ipAddress, int port) {
        return this.setCategory(category).setUsername(username).setEmail(email).setIPAddress(ipAddress).setPort(port);
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public User setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
        return this;
    }
}
