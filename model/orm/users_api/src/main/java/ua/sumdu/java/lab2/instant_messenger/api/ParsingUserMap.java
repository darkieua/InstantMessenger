package ua.sumdu.java.lab2.instant_messenger.api;

import ua.sumdu.java.lab2.instant_messenger.entities.User;

public interface ParsingUserMap {

    String userMapToJSonString(User user);

    UserMap jsonStringToUserMap(String jsonString);

    void writeUserMapToFile(String jsonString);
}
