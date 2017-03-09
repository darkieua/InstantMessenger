package com.nc.lab2.db_users;

public interface CreateUser {

    Boolean validateUsername(String username);

    Boolean validateEmail(String email);

    Boolean validatePort(int port);

    Boolean validateIPAddress(String ipAddress);

    User createUser(String category, String username, String email, String ip, int port);

}
