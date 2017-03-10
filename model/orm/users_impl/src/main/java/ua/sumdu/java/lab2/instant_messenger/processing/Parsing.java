package ua.sumdu.java.lab2.instant_messenger.processing;

import ua.sumdu.java.lab2.instant_messenger.api.*;
import ua.sumdu.java.lab2.instant_messenger.entities.User;

public class Parsing implements ParsingGroupMap, ParsingUserMap {
    @Override
    public String userMapToJSonString(User user) {
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
    public UserMap jsonStringToUserMap(String jsonString) {
        return null;
    }

    @Override
    public void writeUserMapToFile(String jsonString) {

    }
}
