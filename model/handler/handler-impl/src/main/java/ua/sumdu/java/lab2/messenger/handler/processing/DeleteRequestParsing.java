package ua.sumdu.java.lab2.messenger.handler.processing;

import ua.sumdu.java.lab2.messenger.api.GroupMap;
import ua.sumdu.java.lab2.messenger.api.MessageMap;
import ua.sumdu.java.lab2.messenger.api.UserMap;
import ua.sumdu.java.lab2.messenger.entities.Message;
import ua.sumdu.java.lab2.messenger.entities.MessageMapImpl;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.processing.UserMapParserImpl;

import java.io.File;
import java.time.LocalDateTime;

import static ua.sumdu.java.lab2.messenger.handler.entities.RequestType.REMOVING_FROM_FRIENDS;
import static ua.sumdu.java.lab2.messenger.handler.entities.RequestType.USER_LEFT_GROUP;
import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.SUCCESSFUL;

public class DeleteRequestParsing {
    static String deleteRequests(int requestType, String context) {
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
}
