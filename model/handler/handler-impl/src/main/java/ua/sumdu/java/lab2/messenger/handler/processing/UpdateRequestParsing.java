package ua.sumdu.java.lab2.messenger.handler.processing;

import ua.sumdu.java.lab2.messenger.api.GroupMap;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;

import static ua.sumdu.java.lab2.messenger.handler.entities.RequestType.*;
import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.*;

public class UpdateRequestParsing {
    static String updateRequestsAndUpdateData(int requestType, String context) {
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
     * Processing group list updates.
     */
    static void updateGroup(String str) {
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
}
