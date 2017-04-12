package ua.sumdu.java.lab2.instant_messenger.handler.processing;

import ua.sumdu.java.lab2.instant_messenger.entities.GroupMapImpl;
import ua.sumdu.java.lab2.instant_messenger.entities.MessageMapImpl;
import ua.sumdu.java.lab2.instant_messenger.entities.User;
import ua.sumdu.java.lab2.instant_messenger.entities.UserMapImpl;
import ua.sumdu.java.lab2.instant_messenger.handler.api.ResponseGenerating;
import ua.sumdu.java.lab2.instant_messenger.handler.entities.ResponseType;
import ua.sumdu.java.lab2.instant_messenger.parsers.XMLParser;
import ua.sumdu.java.lab2.instant_messenger.processing.GroupMapParserImpl;

import org.w3c.dom.Document;
import java.io.File;

import static ua.sumdu.java.lab2.instant_messenger.entities.User.CURRENT_USER;
import static ua.sumdu.java.lab2.instant_messenger.entities.User.getURLMessageDirectory;

public class ResponseGeneratingImpl implements ResponseGenerating {
    @Override
    public String generate(String string) {
        StringBuilder result = new StringBuilder();
        if (string.length() == 3) {
            result.append(string);
            int type = Integer.parseInt(string);
            if (type == ResponseType.REQUEST_HAS_BEEN_DECLINED.getResponseNumber()) {
                User thisUser = CURRENT_USER;
                result.append('=').append(thisUser.getUsername()).append('(')
                        .append(thisUser.getIpAddress()).append(')');
            } else if (type == ResponseType.ADDED_TO_FRIENDS.getResponseNumber()) {
                result.append(CURRENT_USER.toJSonString());
            }
        } else {
            int responseType = Integer.parseInt(string.substring(0, 2));
            result.append(responseType).append('=');
            if (responseType == ResponseType.UPDATED_GROUP_LIST.getResponseNumber()) {
                String groupName = string.substring(3);
                GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
                UserMapImpl userMap = (UserMapImpl) groupMapParser.getUserMap(groupName);
                GroupMapImpl currentGroup = new GroupMapImpl();
                for (User user : userMap.getMap().values()) {
                    currentGroup.addUser(groupName, user);
                }
                result.append(groupMapParser.groupMapToJSonString(currentGroup));
            } else if (responseType == ResponseType.REQUESTED_MESSAGES.getResponseNumber()) {
                 String[] words = string.substring(3).split("=");
                 long date = Long.parseLong(words[0]);
                 Document doc = XMLParser.INSTANCE.getDocument(new File(getURLMessageDirectory() + words[1]));
                 MessageMapImpl messageMap = (MessageMapImpl) XMLParser.INSTANCE.getMessagesFromSpecificDate(doc, date);
                 result.append(XMLParser.INSTANCE.toXML(XMLParser.INSTANCE.writeMessageToDocument(messageMap, null)));
            } else if (responseType == ResponseType.ADDED_TO_GROUP.getResponseNumber()) {
                String groupName = string.substring(3);
                GroupMapImpl thisUser = new GroupMapImpl();
                thisUser.addUser(groupName, CURRENT_USER);
                result.append(GroupMapParserImpl.getInstance().groupMapToJSonString(thisUser));
            }
        }
        return result.toString();
    }
}
