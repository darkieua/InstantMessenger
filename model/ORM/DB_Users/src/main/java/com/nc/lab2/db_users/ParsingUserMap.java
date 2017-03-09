package com.nc.lab2.db_users;

public interface ParsingUserMap {

    String userMapToJSonString(User user);

    AbstractUserMap jsonStringToUserMap(String jsonString);

    Boolean writeUserMapToFile(String jsonString);
}
