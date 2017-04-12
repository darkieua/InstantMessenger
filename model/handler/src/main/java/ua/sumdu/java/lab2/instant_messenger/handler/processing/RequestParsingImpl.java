package ua.sumdu.java.lab2.instant_messenger.handler.processing;

import ua.sumdu.java.lab2.instant_messenger.api.UserMap;
import ua.sumdu.java.lab2.instant_messenger.entities.*;
import ua.sumdu.java.lab2.instant_messenger.handler.api.RequestParsing;
import ua.sumdu.java.lab2.instant_messenger.parsers.XMLParser;
import ua.sumdu.java.lab2.instant_messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.instant_messenger.processing.UserCreatorImpl;
import ua.sumdu.java.lab2.instant_messenger.processing.UserMapParserImpl;

import java.io.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import static ua.sumdu.java.lab2.instant_messenger.entities.User.CURRENT_USER;
import static ua.sumdu.java.lab2.instant_messenger.handler.entities.RequestType.*;
import static ua.sumdu.java.lab2.instant_messenger.handler.entities.ResponseType.*;

public class RequestParsingImpl implements RequestParsing {
    private static final Logger LOG = LoggerFactory.getLogger(RequestParsingImpl.class);

    private boolean test = false;

    @Override
    public String requestParser(String string) {
        int requestType = Integer.parseInt(string.substring(0,4));
        String context = string.substring(5);
        if (requestType > 999 && requestType < 2999) {
            return adding(requestType, context);
        } else if ((requestType > 2999 && requestType < 3999)||(requestType > 5999 && requestType < 6999)) {
            return receivingDataAndMessages(requestType, context);
        } else if (requestType > 3999 && requestType < 5999) {
            return updateRequestsAndUpdateData(requestType, context);
        } else {
            return String.valueOf(UNIDENTIFIED_REQUEST.getResponseNumber());
        }
    }

    private String adding(int requestType, String context) {
        if (requestType == ADD_TO_FRIENDS.getRequestNumber()) {
            boolean usersReaction;
            if (test) {
                usersReaction = true;
            } else {
                usersReaction = getReaction(context, "user");
            }
            if (usersReaction) {
                addNewFriend(context);
                return String.valueOf(ADDED_TO_FRIENDS.getResponseNumber());
            } else {
                return String.valueOf(REQUEST_HAS_BEEN_DECLINED.getResponseNumber());
            }
        } else if (requestType == ADD_TO_GROUP.getRequestNumber()) {
            boolean usersReaction;
            if (test) {
                usersReaction = true;
            } else {
                usersReaction = getReaction(context, "group");
            }
            if (usersReaction) {
                String groupName = addGroup(context);
                return ADDED_TO_GROUP.getResponseNumber() + " " + groupName;
            } else {
                return String.valueOf(REQUEST_HAS_BEEN_DECLINED.getResponseNumber());
            }
        } else {
            return String.valueOf(UNIDENTIFIED_REQUEST.getResponseNumber());
        }
    }

    private String receivingDataAndMessages(int requestType, String context) {
        if (requestType == NEW_MESSAGE.getRequestNumber()) {
            newMessage(context, "user");
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
            return String.valueOf(UPDATED_GROUP_LIST.getResponseNumber()) + " " + context;
        } else if (requestType == MESSAGES_FROM_A_SPECIFIC_DATE.getRequestNumber()) {
            return REQUESTED_MESSAGES.getResponseNumber() + " "+ context;
        } else {
            return String.valueOf(UNIDENTIFIED_REQUEST.getResponseNumber());
        }
    }

    public void addNewFriend(String str) {
        UserMapParserImpl userMapParser = UserMapParserImpl.getInstance();
        UserMap userMap = userMapParser.getFriends();
        User newUser = UserCreatorImpl.INSTANCE.toUser(str);
        userMap.addUser(newUser);
        userMapParser.writeUserMapToFile(userMapParser.userMapToJSonString(userMap));
    }

    private String addGroup(String str) {
        GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
        GroupMapImpl groupMap = (GroupMapImpl) groupMapParser.jsonStringToGroupMap(str);
        GroupMapImpl currentGroups = (GroupMapImpl) groupMapParser.getGroupMap();
        String key = (String) groupMap.getMap().keySet().toArray()[0];
        UserMapImpl userMap = groupMap.getMap().get(key);
        for (User user: userMap.getMap().values()) {
            currentGroups.addUser(key, user);
        }
        groupMap.addUser(key, CURRENT_USER);
        groupMapParser.writeGroupMapToFile(groupMapParser.groupMapToJSonString(currentGroups));
        return key;
    }

    private void newMessage(String str, String type) {
        Document doc = XMLParser.loadXMLFromString(str);
        Message message = XMLParser.INSTANCE.parseMessage(doc.getFirstChild());
        String fileName;
        if ("user".equals(type)) {
            fileName = message.getSender();
        } else {
            fileName = message.getReceiver();
        }
        File file = new File(User.getURLMessageDirectory() +  fileName + ".xml");
        MessageMapImpl messageMap = (MessageMapImpl) XMLParser.INSTANCE.read(file);
        messageMap.addMessage(message);
        XMLParser.INSTANCE.write(messageMap, file);

    }

    public void updateGroup(String str) {
        GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
        GroupMapImpl currentGroup = (GroupMapImpl) groupMapParser.jsonStringToGroupMap(str);
        String groupName = (String) currentGroup.getMap().keySet().toArray()[0];
        GroupMapImpl allGroup = (GroupMapImpl) groupMapParser.getGroupMap();
        allGroup.getMap().remove(groupName);
        for (User user : currentGroup.getMap().get(groupName).getMap().values()) {
            allGroup.addUser(groupName, user);
        }
        groupMapParser.writeGroupMapToFile(groupMapParser.groupMapToJSonString(allGroup));
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public boolean getReaction(String name, String groupOrUser) {
        return false;
    }
}
