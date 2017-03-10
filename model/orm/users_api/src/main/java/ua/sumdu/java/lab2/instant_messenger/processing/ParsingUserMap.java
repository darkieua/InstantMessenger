package ua.sumdu.java.lab2.instant_messenger.processing;

import ua.sumdu.java.lab2.instant_messenger.api.UserMap;
import ua.sumdu.java.lab2.instant_messenger.entities.User;

public interface ParsingUserMap {

    String userMapToJSonString(User user);

    UserMap jsonStringToUserMap(String jsonString);

    void writeUserMapToFile(String jsonString);
}
