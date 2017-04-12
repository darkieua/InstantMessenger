package ua.sumdu.java.lab2.messenger.processing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.api.GroupMap;
import ua.sumdu.java.lab2.messenger.api.GroupMapParser;
import ua.sumdu.java.lab2.messenger.api.UserMap;
import ua.sumdu.java.lab2.messenger.entities.GroupMapImpl;
import ua.sumdu.java.lab2.messenger.entities.User;

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
      FileUtils.writeStringToFile(User.getGroupsFile(), jsonString, "UTF-8");
      return true;
    } catch (IOException e) {
      LOG.error("writeGroupMapToFile: IOException");
      return false;
    }
  }

  public UserMap getUserMap(String groupName) {
    return ((GroupMapImpl)getGroupMap()).getMap().get(groupName);
  }

  /**
  * Method returns all the groups in which the user is.
  */

  public GroupMap getGroupMap() {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(User.getGroupsFile()));
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