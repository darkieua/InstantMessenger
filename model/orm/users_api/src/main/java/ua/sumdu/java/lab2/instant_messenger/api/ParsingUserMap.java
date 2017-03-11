package ua.sumdu.java.lab2.instant_messenger.api;

public interface ParsingUserMap {

    String userMapToJSonString(UserMap userMap);

    UserMap jsonStringToUserMap(String jsonString);

    void writeUserMapToFile(String jsonString);
}
