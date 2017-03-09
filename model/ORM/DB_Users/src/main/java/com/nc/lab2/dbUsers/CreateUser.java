package com.nc.lab2.dbUsers;

public interface CreateUser {

    Boolean validateUsername(String username);

    Boolean validateEmail(String email);

    Boolean validatePort(int port);

    Boolean validateIP(String ip);

    User createUser(String category, String username, String email, String ip, int port);

}
