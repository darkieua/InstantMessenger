package ua.sumdu.java.lab2.messenger.api;

import java.io.IOException;

public interface UserMapParser {

  String userMapToJSonString(UserMap userMap);

  UserMap jsonStringToUserMap(String jsonString);

  boolean writeUserMapToFile(String jsonString) throws IOException;

  UserMap getFriends();
}
