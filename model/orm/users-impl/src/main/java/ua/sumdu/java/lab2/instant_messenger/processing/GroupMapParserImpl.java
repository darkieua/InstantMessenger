package ua.sumdu.java.lab2.instant_messenger.processing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.instant_messenger.api.GroupMap;
import ua.sumdu.java.lab2.instant_messenger.api.GroupMapParser;
import ua.sumdu.java.lab2.instant_messenger.entities.GroupMapImpl;

import java.io.File;
import java.io.IOException;

public final class GroupMapParserImpl implements GroupMapParser{

    private static final Logger LOG = LoggerFactory.getLogger(GroupMapParserImpl.class);

    private static GroupMapParserImpl instance;

    private GroupMapParserImpl() {
    }

    public static GroupMapParserImpl getInstance() {
        synchronized (GroupMapParserImpl.class) {
            LOG.debug("Create a new GroupMap Parser");
            if (instance == null) {
                instance = new GroupMapParserImpl();
            }
            return instance;
        }
    }


    @Override
    public String groupMapToJSonString(GroupMap groupMap) {
        LOG.info("Converting a GroupMap to a Json String");
        GroupMapImpl newGroup = (GroupMapImpl) groupMap;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.toJson(newGroup);
    }

    @Override
    public GroupMap jsonStringToGroupMap(String jsonString) {
        LOG.info("Converting a Json String to a GroupMap");
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.fromJson(jsonString, GroupMapImpl.class);
    }

    @Override
    public boolean writeGroupMapToFile(String jsonString) {
        try {
            FileUtils.writeStringToFile(new File("src/main/java/resources/friends.json"), jsonString, "UTF-8");
            return true;
        } catch (IOException e) {
            LOG.error("writeGroupMapToFile: IOException");
            return false;
        }
    }
}
