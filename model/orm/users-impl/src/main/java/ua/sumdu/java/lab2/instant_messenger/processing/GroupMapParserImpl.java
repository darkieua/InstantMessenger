package ua.sumdu.java.lab2.instant_messenger.processing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import ua.sumdu.java.lab2.instant_messenger.api.GroupMap;
import ua.sumdu.java.lab2.instant_messenger.api.GroupMapParser;
import ua.sumdu.java.lab2.instant_messenger.entities.GroupMapImpl;

import java.io.File;
import java.io.IOException;

public final class GroupMapParserImpl implements GroupMapParser{
    private static GroupMapParserImpl instance;

    private GroupMapParserImpl() {
    }

    public static GroupMapParserImpl getInstance() {
        synchronized (GroupMapParserImpl.class) {
            if (instance == null) {
                instance = new GroupMapParserImpl();
            }
            return instance;
        }
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
    public void writeGroupMapToFile(String jsonString) throws IOException {
        FileUtils.writeStringToFile(new File("src/main/java/resources/friends.json"), jsonString, "UTF-8");
    }
}
