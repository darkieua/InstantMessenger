package ua.sumdu.java.lab2.messenger.processing;

import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.api.UserMap;
import ua.sumdu.java.lab2.messenger.api.UserMapParser;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.entities.UserMapImpl;

public final class UserMapParserImpl implements UserMapParser {

    private static final Logger LOG = LoggerFactory.getLogger(UserMapParserImpl.class);

    private static UserMapParserImpl instance;

    private UserMapParserImpl() {
    }

    /**
     * Create a new UserMap Parser.
     */

    public static UserMapParserImpl getInstance() {
        synchronized (UserMapParserImpl.class) {
            LOG.debug("Create a new UserMap Parser");
            if (instance == null) {
                instance = new UserMapParserImpl();
            }
            return instance;
        }
    }

    @Override
    public String userMapToJSonString(UserMap userMap) {
        LOG.debug("Converting a UserMap to a Json String");
        UserMapImpl newUsers = (UserMapImpl) userMap;
        return new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(newUsers);
    }

    @Override
    public UserMap jsonStringToUserMap(String jsonString) {
        LOG.debug("Converting a Json String to a UserMap");
        UserMapImpl userMap = new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .fromJson(jsonString, UserMapImpl.class);
        if (Objects.isNull(userMap)) {
            return new UserMapImpl();
        } else {
            return userMap;
        }
    }

    @Override
    public boolean writeUserMapToFile(String jsonString) {
        File friends = new File(User.getFriendsPath());
        return write(friends, jsonString);
    }

    @Override
    public boolean writeBlackListToFile(String jsonString) {
        File blackList = new File(User.getBlackListPath());
        return write(blackList, jsonString);
    }


    private boolean write(File file, String jsonString) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileUtils.writeStringToFile(file, jsonString, "UTF-8");
            return true;
        } catch (IOException e) {
            LOG.error("writeUserMapToFile: IOException");
            return false;
        }
    }

    @Override
    public UserMap getFriends() {
        try {
            File friends = new File(User.getFriendsPath());
            if (!friends.exists()) {
                friends.createNewFile();
                return new UserMapImpl();
            }
            String jsonString = FileUtils.readFileToString(friends, "UTF-8");
            return jsonStringToUserMap(jsonString);
        } catch (IOException e) {
            LOG.error("writeUserMapToFile: IOException");
            return new UserMapImpl();
        }
    }

    @Override
    public UserMap getBlackList() {
        try {
            File blackListFile = new File(User.getBlackListPath());
            if (!blackListFile.exists()) {
                blackListFile.createNewFile();
                return new UserMapImpl();
            }
            String jsonString = FileUtils.readFileToString(blackListFile, "UTF-8");
            return jsonStringToUserMap(jsonString);
        } catch (IOException e) {
            LOG.error("writeUserMapToFile: IOException");
            return new UserMapImpl();
        }
    }


}
