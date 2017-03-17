package ua.sumdu.java.lab2.instant_messenger.api;

import java.io.IOException;

public interface GroupMapParser {

    String groupMapToJSonString(GroupMap groupMap);

    GroupMap jsonStringToGroupMap(String jsonString);

    boolean writeGroupMapToFile(String jsonString) throws IOException;
}
