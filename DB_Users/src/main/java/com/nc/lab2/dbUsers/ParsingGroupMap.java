package com.nc.lab2.dbUsers;

public interface ParsingGroupMap {

    String userMapToJSonString(User user);

    GroupMap jsonStringToGroupMap(String jsonString);

    Boolean writeGroupMapToFile(String jsonString);
}
