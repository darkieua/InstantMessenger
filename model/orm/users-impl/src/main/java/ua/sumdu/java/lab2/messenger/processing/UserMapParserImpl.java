package ua.sumdu.java.lab2.messenger.processing;

import com.google.gson.Gson;
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
    LOG.info("Converting a UserMap to a Json String");
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.setPrettyPrinting().create();
    UserMapImpl newUsers = (UserMapImpl) userMap;
    return gson.toJson(newUsers);
  }

  @Override
  public UserMap jsonStringToUserMap(String jsonString) {
    LOG.info("Converting a Json String to a UserMap");
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.setPrettyPrinting().create();
    UserMapImpl userMap = gson.fromJson(jsonString, UserMapImpl.class);
    if (Objects.isNull(userMap)) {
      return new UserMapImpl();
    } else {
      return userMap;
    }
  }

  @Override
  public boolean writeUserMapToFile(String jsonString) {
    File friends = User.getFriendsFile();
    if (!friends.exists()) {
      try {
        friends.createNewFile();
      } catch (IOException e) {
        return false;
      }
    }
    try {
      FileUtils.writeStringToFile(friends, jsonString, "UTF-8");
      return true;
    } catch (IOException e) {
      LOG.error("writeUserMapToFile: IOException");
      return false;
    }
  }

  @Override
  public UserMap getFriends() {
    File friends = User.getFriendsFile();
    if (!friends.exists()) {
      try {
        friends.createNewFile();
        return new UserMapImpl();
      } catch (IOException e) {
        LOG.error("writeUserMapToFile: IOException");
      }
    }
    try {
      String jsonString = FileUtils.readFileToString(friends, "UTF-8");
      return jsonStringToUserMap(jsonString);
    } catch (IOException e) {
      LOG.error("writeUserMapToFile: IOException");
      return new UserMapImpl();
    }
  }


}
