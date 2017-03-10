package ua.sumdu.java.lab2.instant_messenger;

public interface ParsingGroupMap {

    String userMapToJSonString(User user);

    AbstractGroupMap jsonStringToGroupMap(String jsonString);

    Boolean writeGroupMapToFile(String jsonString);
}
