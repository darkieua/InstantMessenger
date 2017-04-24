package ua.sumdu.java.lab2.messenger.processing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.api.GroupMap;
import ua.sumdu.java.lab2.messenger.api.GroupMapParser;
import ua.sumdu.java.lab2.messenger.api.UserMap;
import ua.sumdu.java.lab2.messenger.entities.GroupMapImpl;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.entities.UserMapImpl;

public final class GroupMapParserImpl implements GroupMapParser {

    private static final Logger LOG = LoggerFactory.getLogger(GroupMapParserImpl.class);

    private static GroupMapParserImpl instance;

    private GroupMapParserImpl() {
    }

    /**
    * Method returns a static instance of the class.
    */

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
        LOG.debug("Converting a GroupMap to a Json String");
        GroupMapImpl newGroup = (GroupMapImpl) groupMap;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.toJson(newGroup);
    }

    @Override
    public GroupMap jsonStringToGroupMap(String jsonString) {
        LOG.debug("Converting a Json String to a GroupMap");
        GroupMap groupMap = new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .fromJson(jsonString, GroupMapImpl.class);
        if (Objects.isNull(groupMap)) {
            return new GroupMapImpl();
        } else {
            return groupMap;
        }
    }

    @Override
    public boolean writeGroupMapToFile(String jsonString) {
        try {
            File groups = new File (User.getGroupsPath());
            if (!groups.exists()) {
                groups.createNewFile();
            }
            FileUtils.writeStringToFile(groups, jsonString, "UTF-8");
            return true;
        } catch (IOException e) {
            LOG.error("writeGroupMapToFile: IOException");
            return false;
        }
    }

    public UserMap getUserMap(String groupName) {
        UserMap groupMap = getGroupMap().getMap().get(groupName);
        if (Objects.isNull(groupMap)) {
            return new UserMapImpl();
        } else {
            return groupMap;
        }
    }

    /**
    * Method returns all the groups in which the user is.
    */
    public GroupMap getGroupMap() {
        try {
            File groups = new File(User.getGroupsPath());
            if (!groups.exists()) {
                groups.createNewFile();
                return new GroupMapImpl();
            }
            BufferedReader reader = new BufferedReader(new FileReader(groups));
            StringBuilder result = new StringBuilder();
            String temp;
            while ((temp = reader.readLine()) != null) {
                result.append(temp);
            }
            return jsonStringToGroupMap(result.toString());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return new GroupMapImpl();
        }
    }
}
