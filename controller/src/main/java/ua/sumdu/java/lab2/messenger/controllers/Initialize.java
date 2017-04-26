package ua.sumdu.java.lab2.messenger.controllers;

import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.EMPTY_USER;
import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.CURRENT_USER;

import java.io.File;
import java.util.Set;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.api.GroupMap;
import ua.sumdu.java.lab2.messenger.api.MessageMap;
import ua.sumdu.java.lab2.messenger.api.UserMap;
import ua.sumdu.java.lab2.messenger.entities.Message;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.processing.UserMapParserImpl;

public final class Initialize {
    private static final Logger LOG = LoggerFactory.getLogger(Initialize.class);

    private Initialize() {
        LOG.debug("Initialize constructor");
    }

    public static void initFriends(final JFXListView<String> friendsList) {
        UserMap friends = UserMapParserImpl.getInstance()
                .getFriends();
        ObservableList<String> list = FXCollections.observableArrayList();
        for (User user : friends.getMap()
                .values()) {
            if (!user.getCategory().name()
                    .equals(EMPTY_USER.name())
                    && !user.getCategory().name()
                    .equals(CURRENT_USER.name())) {
                list.add(user.getUsername());
            }
        }
        friendsList.setItems(list);
    }

    public static void initGroups(final JFXListView<String> groupList) {
        GroupMap groups = GroupMapParserImpl.getInstance()
                .getGroupMap();
        Set<String> groupNames = groups.getMap()
                .keySet();
        ObservableList<String> list = FXCollections.observableArrayList();
        for (String groupName : groupNames) {
            if (groups.getMap()
                    .get(groupName)
                    .getMap()
                    .size() != 0) {
                list.add(groupName);
            }
        }
        groupList.setItems(list);
    }

    public static void initBlackList(final JFXListView<String> blackList) {
        UserMap blackListUsers = UserMapParserImpl
                .getInstance()
                .getBlackList();
        ObservableList<String> list = FXCollections.observableArrayList();
        for (User user : blackListUsers.getMap()
                .values()) {
            list.add(user.getUsername());
        }
        blackList.setItems(list);
    }

    public static void updateMessages(final ListView<Text> chat,
                                      final String groupName) {
        File messages = new File(User.getUrlMessageDirectory()
                + File.separator
                + groupName
                + ".xml");
        ObservableList<Text> list = FXCollections.observableArrayList();
        MessageMap messageMap = XmlParser.INSTANCE.read(messages);
        for (Message message : messageMap.getMapForMails().values()) {
            Text text = new Text(message.getSender() + ": "
                    + message.getText() + " ("
                    + message.getTimeSending() + ")");
            text.wrappingWidthProperty().bind(chat.widthProperty());
            list.add(text);
        }
        chat.setItems(list);
    }
}
