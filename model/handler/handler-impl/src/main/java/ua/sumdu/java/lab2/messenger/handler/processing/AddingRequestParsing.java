package ua.sumdu.java.lab2.messenger.handler.processing;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.api.GroupMap;
import ua.sumdu.java.lab2.messenger.api.UserMap;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.processing.UserCreatorImpl;
import ua.sumdu.java.lab2.messenger.processing.UserMapParserImpl;

import java.util.Optional;

import static ua.sumdu.java.lab2.messenger.handler.entities.RequestType.*;
import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.*;

public class AddingRequestParsing {

    private static final Logger LOG = LoggerFactory.getLogger(AddingRequestParsing.class);

    private static final String USER = "user";

    public static boolean test = false;

    static String adding(int requestType, String context) {
        if (requestType == ADD_TO_FRIENDS.getRequestNumber()) {
            boolean usersReaction;
            usersReaction = getReaction(context, USER);
            if (usersReaction) {
                addNewFriend(context);
                return String.valueOf(ADDED_TO_FRIENDS.getResponseNumber());
            } else {
                return String.valueOf(REQUEST_HAS_BEEN_DECLINED.getResponseNumber());
            }
        } else if (requestType == ADD_TO_GROUP.getRequestNumber()) {
            boolean usersReaction;
            usersReaction = getReaction(context, "group");
            if (usersReaction) {
                String groupName = addGroup(context);
                return ADDED_TO_GROUP.getResponseNumber() + "=" + groupName;
            } else {
                return String.valueOf(REQUEST_HAS_BEEN_DECLINED.getResponseNumber());
            }
        } else {
            return String.valueOf(UNIDENTIFIED_REQUEST.getResponseNumber());
        }
    }

    /**
     * Handling the addition of a new user to friends.
     */
    static String addNewFriend(String str) {
        UserMapParserImpl userMapParser = UserMapParserImpl.getInstance();
        UserMap userMap = userMapParser.getFriends();
        User newUser = UserCreatorImpl.INSTANCE.toUser(str);
        userMap.addUser(newUser);
        userMapParser.writeUserMapToFile(userMapParser.userMapToJSonString(userMap));
        return newUser.getUsername();
    }

    private static String addGroup(String str) {
        GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
        GroupMap groupMap = groupMapParser.jsonStringToGroupMap(str);
        GroupMap currentGroups = groupMapParser.getGroupMap();
        String key = (String) groupMap.getMap().keySet().toArray()[0];
        UserMap userMap = groupMap.getMap().get(key);
        for (User user: userMap.getMap().values()) {
            currentGroups.addUser(key, user);
        }
        groupMap.addUser(key, User.getCurrentUser());
        groupMapParser.writeGroupMapToFile(groupMapParser.groupMapToJSonString(currentGroups));
        return key;
    }


    public static boolean getReaction(String context, String groupOrUser) {
        if (test) {
            return true;
        }
        if (USER.equals(groupOrUser)) {
            return getUserReaction(context);
        } else {
            GroupMap groups = GroupMapParserImpl.getInstance().getGroupMap();
            GroupMap groupMap = GroupMapParserImpl.getInstance().jsonStringToGroupMap(context);
            String name = groupMap.getMap().keySet().iterator().next();
            boolean isFind = false;
            for (String groupName : groups.getMap().keySet()) {
                if (name.equals(groupName)) {
                    isFind = true;
                    break;
                }
            }
            return !isFind && createAlert("Add to group", "Administrator of the group <" + name + "> sent you a request to add to the group.\n Do you want to join a group <" + name + ">?");
        }
    }

    private static boolean getUserReaction(String context) {
        UserMap friends = UserMapParserImpl.getInstance().getFriends();
        boolean isFind = false;
        User newUser = UserCreatorImpl.INSTANCE.toUser(context);
        for (User user : friends.getAllUsers()) {
            if (newUser.getUsername().equals(user.getUsername())) {
                isFind = true;
                break;
            }
        }
        if (!isFind) {
            UserMap blackList = UserMapParserImpl.getInstance().getBlackList();
            for (User user : blackList.getAllUsers()) {
                if (newUser.getUsername().equals(user.getUsername())) {
                    isFind = true;
                    break;
                }
            }
        }
        return !isFind && createAlert("Add to friends", "User " + newUser.getUsername() + " sent a request to add to friends. \n Do you want to add the user " + newUser.getUsername() + " as a friend?");
    }

    private static boolean createAlert(String title, String text) {
        final boolean[] reaction = new boolean[1];
        boolean[] work = {true};
        int time = 0;
        Platform.runLater(() -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(title);
            alert.setContentText(text);
            Optional<ButtonType> result = alert.showAndWait();
            reaction[0] =    result.isPresent() && result.get() == ButtonType.OK;
            work[0] = false;
        });
        while(work[0] && time < 1799 * 2) {
            time ++;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage());
            }
        }
        return reaction[0];
    }
}
