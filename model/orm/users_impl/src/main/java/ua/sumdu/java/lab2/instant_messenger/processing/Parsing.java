package ua.sumdu.java.lab2.instant_messenger.processing;

import ua.sumdu.java.lab2.instant_messenger.api.GroupMap;
import ua.sumdu.java.lab2.instant_messenger.api.ParsingGroupMap;
import ua.sumdu.java.lab2.instant_messenger.api.ParsingUserMap;
import ua.sumdu.java.lab2.instant_messenger.api.UserMap;
import ua.sumdu.java.lab2.instant_messenger.entities.User;

public class Parsing implements ParsingGroupMap, ParsingUserMap {
    private static Parsing ourInstance = new Parsing();

    public static Parsing getInstance() {
        return ourInstance;
    }

    private Parsing() {
    }

    @Override
    public String groupMapToJSonString(GroupMap groupMap) {
        return null;
    }

    @Override
    public GroupMap jsonStringToGroupMap(String jsonString) {
        return null;
    }

    @Override
    public void writeGroupMapToFile(String jsonString) {

    }


    @Override
    public String userMapToJSonString(UserMap userMap) {
        return null;
    }

    @Override
    public UserMap jsonStringToUserMap(String jsonString) {
        return null;
    }

    @Override
    public void writeUserMapToFile(String jsonString) {

    }
}
