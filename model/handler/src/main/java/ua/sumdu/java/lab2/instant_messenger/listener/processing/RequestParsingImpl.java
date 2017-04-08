package ua.sumdu.java.lab2.instant_messenger.listener.processing;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import ua.sumdu.java.lab2.instant_messenger.config.parser.UserConfigParser;
import ua.sumdu.java.lab2.instant_messenger.entities.*;
import ua.sumdu.java.lab2.instant_messenger.listener.api.RequestParsing;
import ua.sumdu.java.lab2.instant_messenger.listener.entities.RequestType;
import ua.sumdu.java.lab2.instant_messenger.parsers.XMLParser;
import ua.sumdu.java.lab2.instant_messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.instant_messenger.processing.UserCreatorImpl;
import ua.sumdu.java.lab2.instant_messenger.processing.UserMapParserImpl;

import java.io.*;

public class RequestParsingImpl implements RequestParsing {
    private static final Logger LOG = LoggerFactory.getLogger(RequestParsingImpl.class);

    @Override
    public boolean parse(String string) {
        int requestType = Integer.parseInt(string.substring(0,1));
        String context = string.substring(3);
        if (requestType == RequestType.ADD_TO_FRIENDS.getRequestNumber()) {
            return addNewFriend(context);
        } else if (requestType == RequestType.ADD_TO_GROUP.getRequestNumber()) {
            return addGroup(context);
        } else if (requestType == RequestType.NEW_MESSAGE.getRequestNumber()) {
            return newMessage(context, false);
        } else if (requestType == RequestType.NEW_MESSAGE_WITH_FILES_TO_FRIEND.getRequestNumber()) {
            return newMessage(context, true);
        } else if (requestType == RequestType.NEW_MESSAGE_TO_GROUP.getRequestNumber()) {
            return newMessage(context, false);
        } else if (requestType == RequestType.NEW_MESSAGE_WITH_FILES_TO_GROUP.getRequestNumber()) {
            return newMessage(context, true);
        } else if (requestType == RequestType.UPDATE_GROUP_LIST.getRequestNumber()) {
            return updateGroup(context);
        } else {
            return true;
        }
    }

    public boolean addNewFriend(String str) {
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
        return true;
    }

    public boolean addGroup(String str) {
        GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
        GroupMapImpl groupMap = (GroupMapImpl) groupMapParser.jsonStringToGroupMap(str);
        GroupMapImpl currentGroups = (GroupMapImpl) groupMapParser.getGroupMap();
        String key = (String) currentGroups.getMap().keySet().toArray()[0];
        UserMapImpl userMap = currentGroups.getMap().get(key);
        for (User user: userMap.getMap().values()) {
            groupMap.addUser(key, user);
        }
        groupMapParser.writeGroupMapToFile(groupMapParser.groupMapToJSonString(currentGroups));
        return true;
    }

    public boolean newMessage(String str, boolean existFile) {
        try {
            File temp = File.createTempFile("temp", "message");
            IOUtils.write(str, new FileWriter(temp));
            Document doc = XMLParser.INSTANCE.getDocument(temp);
            Message message = XMLParser.INSTANCE.parseMessage(doc.getFirstChild(), existFile);
            temp.delete();
            String sender = message.getSender();
            File file = new File(UserConfigParser.getURLMessageDirectory() + sender);
            MessageMapImpl messageMap = (MessageMapImpl) XMLParser.INSTANCE.read(file);
            messageMap.addMessage(message);
            XMLParser.INSTANCE.write(messageMap, file);
            return true;
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public boolean updateGroup(String str) {
        GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
        GroupMapImpl currentGroup = (GroupMapImpl) groupMapParser.jsonStringToGroupMap(str);
        String groupName = (String) currentGroup.getMap().keySet().toArray()[0];
        GroupMapImpl allGroup = (GroupMapImpl) groupMapParser.getGroupMap();
        allGroup.getMap().remove(groupName);
        for (User user : currentGroup.getMap().get(groupName).getMap().values()) {
            allGroup.addUser(groupName, user);
        }
        groupMapParser.writeGroupMapToFile(groupMapParser.groupMapToJSonString(allGroup));
        return true;
    }

}
