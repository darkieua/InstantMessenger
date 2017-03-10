package ua.sumdu.java.lab2.instant_messenger;

public interface ParsingUserMap {

    String userMapToJSonString(User user);

    AbstractUserMap jsonStringToUserMap(String jsonString);

    Boolean writeUserMapToFile(String jsonString);
}
