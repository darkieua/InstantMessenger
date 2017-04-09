package ua.sumdu.java.lab2.instant_messenger.handler.processing;

import ua.sumdu.java.lab2.instant_messenger.config.parser.UserConfigParser;
import ua.sumdu.java.lab2.instant_messenger.entities.*;
import ua.sumdu.java.lab2.instant_messenger.handler.api.RequestParsing;
import ua.sumdu.java.lab2.instant_messenger.handler.entities.RequestType;
import ua.sumdu.java.lab2.instant_messenger.handler.entities.ResponseType;
import ua.sumdu.java.lab2.instant_messenger.parsers.XMLParser;
import ua.sumdu.java.lab2.instant_messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.instant_messenger.processing.UserCreatorImpl;
import ua.sumdu.java.lab2.instant_messenger.processing.UserMapParserImpl;

import java.io.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class RequestParsingImpl implements RequestParsing {
    private static final Logger LOG = LoggerFactory.getLogger(RequestParsingImpl.class);

    @Override
    public String parse(String string) {
        int requestType = Integer.parseInt(string.substring(0,1));
        String context = string.substring(3);
        if (requestType == RequestType.ADD_TO_FRIENDS.getRequestNumber()) {
            boolean bool = true;//Result of interaction with the user
            if (bool) {
                addNewFriend(context);
                return String.valueOf(ResponseType.ADDED_TO_FRIENDS.getResponseNumber());
            } else {
                return String.valueOf(ResponseType.REQUEST_HAS_BEEN_DECLINED.getResponseNumber());
            }
        } else if (requestType == RequestType.ADD_TO_GROUP.getRequestNumber()) {
            boolean bool = true;//Result of interaction with the user
            if (bool) {
                String groupName = addGroup(context);
                return ResponseType.ADDED_TO_GROUP.getResponseNumber() + " " + groupName;
            } else {
                return String.valueOf(ResponseType.REQUEST_HAS_BEEN_DECLINED.getResponseNumber());
            }
        } else if (requestType == RequestType.NEW_MESSAGE.getRequestNumber()) {
            newMessage(context, false);
            return String.valueOf(ResponseType.SUCCESSFUL.getResponseNumber());
        } else if (requestType == RequestType.NEW_MESSAGE_WITH_FILES_TO_FRIEND.getRequestNumber()) {
            boolean bool = true;//Result of interaction with the user ???
            newMessage(context, bool);
            return String.valueOf(ResponseType.SUCCESSFUL.getResponseNumber());
        } else if (requestType == RequestType.NEW_MESSAGE_TO_GROUP.getRequestNumber()) { //???
            newMessage(context, false);
            return String.valueOf(ResponseType.SUCCESSFUL.getResponseNumber());
        } else if (requestType == RequestType.NEW_MESSAGE_WITH_FILES_TO_GROUP.getRequestNumber()) { //???
            boolean bool = true;//Result of interaction with the user ???
            newMessage(context, bool);
            return String.valueOf(ResponseType.SUCCESSFUL.getResponseNumber());
        } else if (requestType == RequestType.UPDATE_GROUP_LIST.getRequestNumber()) {
            updateGroup(context);
            return String.valueOf(ResponseType.SUCCESSFUL.getResponseNumber());
        } else if (requestType == RequestType.REQUEST_FOR_UPDATE_GROUP_LIST.getRequestNumber()) {
            return String.valueOf(ResponseType.UPDATED_GROUP_LIST.getResponseNumber());
        } else if (requestType == RequestType.MESSAGES_FROM_A_SPECIFIC_DATE.getRequestNumber()) {
            return ResponseType.REQUESTED_MESSAGES.getResponseNumber() + ' '+ string.substring(3);
        } else {
            return String.valueOf(ResponseType.UNIDENTIFIED_REQUEST.getResponseNumber());
        }
    }

    public void addNewFriend(String str) {
        File friends = UserConfigParser.getFriendsFile();
        UserMapParserImpl userMapParser = UserMapParserImpl.getInstance();
        UserMapImpl currentFriends = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(friends));
            StringBuilder result = new StringBuilder();
            String temp;
            while((temp=reader.readLine())!=null) {
                result.append(temp);
            }
            currentFriends = (UserMapImpl) userMapParser.jsonStringToUserMap(result.toString());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            currentFriends = new UserMapImpl();
        }
        User newUser = UserCreatorImpl.INSTANCE.toUser(str);
        currentFriends.addUser(newUser);
        userMapParser.writeUserMapToFile(userMapParser.userMapToJSonString(currentFriends));
    }

    public String addGroup(String str) {
        GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
        GroupMapImpl groupMap = (GroupMapImpl) groupMapParser.jsonStringToGroupMap(str);
        GroupMapImpl currentGroups = (GroupMapImpl) groupMapParser.getGroupMap();
        String key = (String) currentGroups.getMap().keySet().toArray()[0];
        UserMapImpl userMap = currentGroups.getMap().get(key);
        for (User user: userMap.getMap().values()) {
            groupMap.addUser(key, user);
        }
        groupMap.addUser(key, UserConfigParser.getCurrentUser());
        groupMapParser.writeGroupMapToFile(groupMapParser.groupMapToJSonString(currentGroups));
        return key;
    }

    public void newMessage(String str, boolean existFile) {
        try {
            File temp = File.createTempFile("temp", "message");
            IOUtils.write(str, new FileWriter(temp));
            Document doc = XMLParser.INSTANCE.getDocument(temp);
            Message message = XMLParser.INSTANCE.parseMessage(doc.getFirstChild(), existFile);
            temp.delete();
            String sender = message.getSender();// ???
            File file = new File(UserConfigParser.getURLMessageDirectory() +  sender);
            MessageMapImpl messageMap = (MessageMapImpl) XMLParser.INSTANCE.read(file);
            messageMap.addMessage(message);
            XMLParser.INSTANCE.write(messageMap, file);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
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

}
