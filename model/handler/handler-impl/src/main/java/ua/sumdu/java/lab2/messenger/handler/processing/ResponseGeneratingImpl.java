package ua.sumdu.java.lab2.messenger.handler.processing;

import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.*;

import java.io.File;
import org.w3c.dom.Document;
import ua.sumdu.java.lab2.messenger.entities.*;
import ua.sumdu.java.lab2.messenger.handler.api.ResponseGenerating;
import ua.sumdu.java.lab2.messenger.parsers.ParsingMessages;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.transferring.impl.DataTransferImpl;

public class ResponseGeneratingImpl implements ResponseGenerating {

    @Override
    public String responseGenerate(String string) {
        StringBuilder result = new StringBuilder();
        if (string.length() == 4) {
            return shortResponses(string);
        }
        int responseType = Integer.parseInt(string.substring(0, 4));
        String context = string.substring(5);
        if (responseType > 3999 && responseType <= 4999) {
            return updatingRequest(responseType, context);
        } else if (responseType > 6999 && responseType <= 7999) {
            return dataAcquisition(responseType, context);
        } else if (responseType == ADDED_TO_GROUP.getResponseNumber()) {
            result.append(responseType).append('=');
            String groupName = string.substring(5);
            GroupMapImpl thisUser = new GroupMapImpl();
            thisUser.addUser(groupName, User.getCurrentUser().setCategory(CategoryUsers.VISITOR));
            result.append(GroupMapParserImpl.getInstance().groupMapToJSonString(thisUser));
        }
        return result.toString();
    }

    private String dataAcquisition(int responseType, String context) {
        if (responseType == DATA_ACQUISITION.getResponseNumber()) {
            DataTransferImpl dataTransfer = new DataTransferImpl();
            return responseType + "=" + dataTransfer.dataAcquisition(context);
        } else {
            return String.valueOf(UNIDENTIFIED_REQUEST.getResponseNumber());
        }
    }

    private String shortResponses(String string) {
        StringBuilder result = new StringBuilder();
        result.append(string);
        int type = Integer.parseInt(string);
        if (type == REQUEST_HAS_BEEN_DECLINED.getResponseNumber()) {
            result.append('=').append(User.getCurrentUser().getUsername()).append('(')
                    .append(User.getCurrentUser().getIpAddress()).append(')');
        } else if (type == ADDED_TO_FRIENDS.getResponseNumber()) {
            result.append('=').append(User.getCurrentUser().setCategory(CategoryUsers.FRIEND).toJSonString());
        } else if (type == DATA_SENDING_REJECTED.getResponseNumber()) {
            result.append('=').append(User.getCurrentUser().getUsername());
        }
        return result.toString();
    }

    private String updatingRequest(int responseType, String context) {
        StringBuilder result = new StringBuilder();
        result.append(responseType).append('=');
        if (responseType == UPDATED_GROUP_LIST.getResponseNumber()) {
            String groupName = context;
            GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
            UserMapImpl userMap = (UserMapImpl) groupMapParser.getUserMap(groupName);
            GroupMapImpl currentGroup = new GroupMapImpl();
            for (User user : userMap.getMap().values()) {
                currentGroup.addUser(groupName, user);
            }
            result.append(groupMapParser.groupMapToJSonString(currentGroup));
        } else if (responseType == REQUESTED_MESSAGES.getResponseNumber()
                || responseType == REQUESTED_GROUP_MESSAGES.getResponseNumber()) {
            String[] words = context.split("=");
            long date = Long.parseLong(words[0]);
            Document doc = XmlParser.INSTANCE.getDocument(new File(
                    User.getUrlMessageDirectory() + "/" + words[1] + ".xml"));
            MessageMapImpl messageMap = (MessageMapImpl) ParsingMessages
                    .getMessagesFromSpecificDate(doc, date);
            result.append(XmlParser.INSTANCE.toXml(XmlParser.INSTANCE
                    .writeMessageToDocument(messageMap, null)));
        }
        return result.toString();
    }

    public String userIsOffline(String userIp) {
        return USER_IS_OFFLINE.getResponseNumber() + "=" + userIp;
    }

}
