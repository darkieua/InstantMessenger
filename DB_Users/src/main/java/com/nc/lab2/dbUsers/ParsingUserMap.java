package com.nc.lab2.dbUsers;

public interface ParsingUserMap {

    String userMapToJSonString(User user);

    UserMap jsonStringToUserMap(String jsonString);

    Boolean writeUserMapToFile(String jsonString);
}
