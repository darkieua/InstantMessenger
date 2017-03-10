package ua.sumdu.java.lab2.instant_messenger.processing;

import ua.sumdu.java.lab2.instant_messenger.api.GroupMap;
import ua.sumdu.java.lab2.instant_messenger.entities.User;

public interface ParsingGroupMap {

    String userMapToJSonString(User user);

    GroupMap jsonStringToGroupMap(String jsonString);

    void writeGroupMapToFile(String jsonString);
}
