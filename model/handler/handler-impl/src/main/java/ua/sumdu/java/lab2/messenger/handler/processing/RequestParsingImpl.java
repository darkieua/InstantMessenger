package ua.sumdu.java.lab2.messenger.handler.processing;

import static ua.sumdu.java.lab2.messenger.handler.entities.RequestType.*;
import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import ua.sumdu.java.lab2.messenger.api.GroupMap;
import ua.sumdu.java.lab2.messenger.api.MessageMap;
import ua.sumdu.java.lab2.messenger.api.UserMap;
import ua.sumdu.java.lab2.messenger.entities.*;
import ua.sumdu.java.lab2.messenger.handler.api.RequestParsing;
import ua.sumdu.java.lab2.messenger.parsers.ParsingMessages;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.processing.UserCreatorImpl;
import ua.sumdu.java.lab2.messenger.processing.UserMapParserImpl;
import ua.sumdu.java.lab2.messenger.transferring.impl.DataTransferImpl;

public class RequestParsingImpl implements RequestParsing {
    private static final Logger LOG = LoggerFactory.getLogger(RequestParsingImpl.class);

    private static final String USER = "user";

    @Override
    public String requestParsing(String string) {
        int requestType = Integer.parseInt(string.substring(0,4));
        String context = string.substring(5);
        if (requestType > 999 && requestType <= 2999) {
            return adding(requestType, context);
        } else if ((requestType > 2999 && requestType <= 3999)
                        || (requestType > 5999 && requestType <= 6999)) {
            return receivingDataAndMessages(requestType, context);
        } else if (requestType > 3999 && requestType <= 5999) {
            return updateRequestsAndUpdateData(requestType, context);
        } else if (requestType > 6999 && requestType <= 7999) {
            return deleteRequests(requestType, context);
        } else {
            return String.valueOf(UNIDENTIFIED_REQUEST.getResponseNumber());
        }
    }

    private String deleteRequests(int requestType, String context) {
        if (requestType == REMOVING_FROM_FRIENDS.getRequestNumber()) {
            UserMap friends = UserMapParserImpl.getInstance().getFriends();
            for (User user : friends.getMap().values()) {
                if (context.equals(user.getUsername())) {
                    friends.removeUser(user);
                    break;
                }
            }
            File system = User.getSystemMessageFile();
            MessageMapImpl messages = (MessageMapImpl) XmlParser.INSTANCE.read(system);
            Message newMessage = new Message("system", User.getCurrentUser().getUsername(),
                    "User " + context + " deleted you from friends", LocalDateTime.now());
            messages.addMessage(newMessage);
            XmlParser.INSTANCE.write(messages, system);
        } else if (requestType == USER_LEFT_GROUP.getRequestNumber()) {
            String[] words = context.split("==");
            GroupMap groupMap = GroupMapParserImpl.getInstance().getGroupMap();
            for (User user : groupMap.getMap().get(words[0]).getMap().values()) {
                if (words[1].equals(user.getUsername())) {
                    groupMap.deleteUser(words[0], user);
                    break;
                }
            }
            GroupMapParserImpl.getInstance().writeGroupMapToFile(GroupMapParserImpl.getInstance().groupMapToJSonString(groupMap));
            File groupFile = new File(User.getUrlMessageDirectory() + "/" + words[0] + ".xml");
            MessageMap messageMap = XmlParser.INSTANCE.read(groupFile);
            messageMap.addMessage(new Message("system", words[0], "User " + words[1] + " left group.", LocalDateTime.now()));
            XmlParser.INSTANCE.write(messageMap, groupFile);
        }
        return String.valueOf(SUCCESSFUL.getResponseNumber());
    }

    private String dataRequests(int requestType, String context) {
        if (requestType == DATA_REQUEST.getRequestNumber()) {
            DataTransferImpl dataTransfer = new DataTransferImpl();
            String result = dataTransfer.requestParsing(context);
            if ("".equals(result)) {
                return String.valueOf(DATA_SENDING_REJECTED.getResponseNumber());
            } else {
                return DATA_ACQUISITION.getResponseNumber() + "=" + result;
            }
        } else {
            return String.valueOf(UNIDENTIFIED_REQUEST.getResponseNumber());
        }
    }

    private String adding(int requestType, String context) {
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

    private String receivingDataAndMessages(int requestType, String context) {
        if (requestType > 5999 && requestType <= 6999) {
            return dataRequests(requestType, context);
        } else if (requestType == NEW_MESSAGE.getRequestNumber()) {
            newMessage(context, USER);
            return String.valueOf(SUCCESSFUL.getResponseNumber());
        } else if (requestType == NEW_MESSAGE_TO_GROUP.getRequestNumber()) {
            newMessage(context, "group");
            return String.valueOf(SUCCESSFUL.getResponseNumber());
        } else {
            return String.valueOf(UNIDENTIFIED_REQUEST.getResponseNumber());
        }
    }

    private String updateRequestsAndUpdateData(int requestType, String context) {
        if (requestType == UPDATE_GROUP_LIST.getRequestNumber()) {
            updateGroup(context);
            return String.valueOf(SUCCESSFUL.getResponseNumber());
        } else if (requestType == REQUEST_FOR_UPDATE_GROUP_LIST.getRequestNumber()) {
            return UPDATED_GROUP_LIST.getResponseNumber() + "=" + context;
        } else if (requestType == MESSAGES_FROM_A_SPECIFIC_DATE.getRequestNumber()) {
            return REQUESTED_MESSAGES.getResponseNumber() + "=" + context;
        } else if (requestType == GROUP_MESSAGES_FROM_A_SPECIFIC_DATE.getRequestNumber()) {
            return REQUESTED_GROUP_MESSAGES.getResponseNumber() + "=" + context;
        } else {
            return String.valueOf(UNIDENTIFIED_REQUEST.getResponseNumber());
        }
    }

    /**
     * Handling the addition of a new user to friends.
     */

    public String addNewFriend(String str) {
        UserMapParserImpl userMapParser = UserMapParserImpl.getInstance();
        UserMap userMap = userMapParser.getFriends();
        User newUser = UserCreatorImpl.INSTANCE.toUser(str);
        userMap.addUser(newUser);
        userMapParser.writeUserMapToFile(userMapParser.userMapToJSonString(userMap));
        return newUser.getUsername();
    }

    private String addGroup(String str) {
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

    private void newMessage(String str, String type) {
        Document doc = XmlParser.loadXmlFromString(str);
        Message message = ParsingMessages.parseMessage(doc.getFirstChild());
        String fileName;
        UserMapImpl users;
        if (USER.equals(type)) {
            fileName = message.getSender();
            users = (UserMapImpl) UserMapParserImpl.getInstance().getFriends();
        } else {
            fileName = message.getReceiver();
            users = (UserMapImpl) GroupMapParserImpl.getInstance().getUserMap(fileName);
        }
        for (User user: users.getMap().values()) {
            if (Objects.equals(user.getUsername(), message.getSender())
                    && !CategoryUsers.BLACKLIST.name().equals(user.getCategory().name())) {
                File file = new File(User.getUrlMessageDirectory() + "/" + fileName + ".xml");
                MessageMapImpl messageMap = (MessageMapImpl) XmlParser.INSTANCE.read(file);
                messageMap.addMessage(message);
                XmlParser.INSTANCE.write(messageMap, file);
            }
        }
    }

    /**
     * Processing group list updates.
     */
    public void updateGroup(String str) {
        GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
        GroupMap currentGroup = groupMapParser.jsonStringToGroupMap(str);
        String groupName = (String) currentGroup.getMap().keySet().toArray()[0];
        GroupMap allGroup = groupMapParser.getGroupMap();
        allGroup.getMap().remove(groupName);
        for (User user : currentGroup.getMap().get(groupName).getMap().values()) {
            allGroup.addUser(groupName, user);
        }
        groupMapParser.writeGroupMapToFile(groupMapParser.groupMapToJSonString(allGroup));
    }

    public boolean getReaction(String context, String groupOrUser) {
        final boolean[] reaction = new boolean[1];
        boolean[] work = {true};
        int time = 0;
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            if (USER.equals(groupOrUser)) {
                User user = UserCreatorImpl.INSTANCE.toUser(context);
                alert.setTitle("Add to friends");
                alert.setContentText("User " + user.getUsername() + " sent a request to add to friends. \n Do you want to add the user " + user.getUsername() + " as a friend?");
            } else {
                GroupMapImpl groupMap = (GroupMapImpl) GroupMapParserImpl.getInstance().jsonStringToGroupMap(context);
                String name = groupMap.getMap().keySet().iterator().next();
                alert.setTitle("Add to group");
                alert.setContentText("Administrator of the group <" + name + "> sent you a request to add to the group.\n Do you want to join a group <" + name + ">?");
            }
            Optional<ButtonType> result = alert.showAndWait();
            reaction[0] =    result.isPresent() && result.get() == ButtonType.OK;
            work[0] = false;
        });
        while(work[0] && time < 1790) {
            time ++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage());
            }
        }
        return reaction[0];
    }
}
