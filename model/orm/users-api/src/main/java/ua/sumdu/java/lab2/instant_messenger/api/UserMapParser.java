package ua.sumdu.java.lab2.instant_messenger.api;

import java.io.IOException;

public interface UserMapParser {

    String userMapToJSonString(UserMap userMap);

    UserMap jsonStringToUserMap(String jsonString);

    boolean writeUserMapToFile(String jsonString) throws IOException;
}
