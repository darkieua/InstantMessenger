package ua.sumdu.java.lab2.messenger.controllers;

import java.io.File;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.entities.*;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.processing.UserMapParserImpl;

public class Initialize {
  private static final Logger LOG = LoggerFactory.getLogger(Initialize.class);

  public static void initFriends(ListView<String> friendsList) {
    UserMapImpl friends = (UserMapImpl) UserMapParserImpl.getInstance().getFriends();
    ObservableList<String> list = FXCollections.observableArrayList();
    for (User user : friends.getMap().values()) {
      if (!user.getCategory().name().equals(CategoryUsers.EMPTY_USER.name())
          && !user.getCategory().name().equals(CategoryUsers.CURRENT_USER.name()))
        list.add(user.getUsername());
    }
    friendsList.setItems(list);
  }

  public static void initGroups(ListView<String> groupList) {
    GroupMapImpl groups = (GroupMapImpl) GroupMapParserImpl.getInstance().getGroupMap();
    Set<String> groupNames = groups.getMap().keySet();
    ObservableList<String> list = FXCollections.observableArrayList();
    for (String groupName : groupNames) {
      if (groups.getMap().get(groupName).getMap().size() != 0) {
        list.add(groupName);
      }
    }
    groupList.setItems(list);
  }

  public static void initBlackList(ListView<String> blackList) {
    UserMapImpl blackListUsers = (UserMapImpl) UserMapParserImpl.getInstance().getBlackList();
    ObservableList<String> list = FXCollections.observableArrayList();
    for (User user : blackListUsers.getMap().values()) {
      list.add(user.getUsername());
    }
    blackList.setItems(list);
  }

  public static void updateMessages(ListView<String> chat, String groupName) {
    File messages = new File(User.getUrlMessageDirectory() + File.separator + groupName + ".xml");
    MessageMapImpl messageMap = (MessageMapImpl) XmlParser.INSTANCE.read(messages);
    ObservableList<String> list = FXCollections.observableArrayList();
    for (Message message : messageMap.getMapForMails().values()) {
      String result = message.getSender() + ": " + message.getText() + "  (" + message.getTimeSending() + ")";
      list.add(result);
    }
    try {
      chat.setItems(list);
    } catch (IllegalStateException e) {
      LOG.error(e.getMessage());
    }
  }
}
