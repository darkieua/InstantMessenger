package ua.sumdu.java.lab2.instant_messenger.processing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ua.sumdu.java.lab2.instant_messenger.api.GroupMap;
import ua.sumdu.java.lab2.instant_messenger.api.ParsingGroupMap;
import ua.sumdu.java.lab2.instant_messenger.api.ParsingUserMap;
import ua.sumdu.java.lab2.instant_messenger.api.UserMap;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public final class Parsing implements ParsingGroupMap, ParsingUserMap {
    private static Parsing ourInstance = new Parsing();

    public static Parsing getInstance() {
        return ourInstance;
    }

    private Parsing() {
    }

    @Override
    public String groupMapToJSonString(GroupMap groupMap) {
        GroupMapImpl newGroup = (GroupMapImpl) groupMap;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.toJson(newGroup);
    }

    @Override
    public GroupMap jsonStringToGroupMap(String jsonString) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.fromJson(jsonString, GroupMapImpl.class);
    }

    @Override
    public void writeGroupMapToFile(String jsonString) {
        try {
            Writer writer = new FileWriter("src/main/java/resources/friends.json");
            writer.write(jsonString);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String userMapToJSonString(UserMap userMap) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        UserMapImpl newUsers = (UserMapImpl) userMap;
        return gson.toJson(newUsers);
    }

    @Override
    public UserMap jsonStringToUserMap(String jsonString) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.fromJson(jsonString, UserMapImpl.class);
    }

    @Override
    public void writeUserMapToFile(String jsonString) {
        try {
            Writer writer = new FileWriter("src/main/java/resources/groups.json");
            writer.write(jsonString);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
