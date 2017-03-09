package com.nc.lab2.db_users;

public interface ParsingGroupMap {

    String userMapToJSonString(User user);

    AbstractGroupMap jsonStringToGroupMap(String jsonString);

    Boolean writeGroupMapToFile(String jsonString);
}
