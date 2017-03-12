package ua.sumdu.java.lab2.instant_messenger.api;

public interface GroupMapParser {

    String groupMapToJSonString(GroupMap groupMap);

    GroupMap jsonStringToGroupMap(String jsonString);

    void writeGroupMapToFile(String jsonString);
}
