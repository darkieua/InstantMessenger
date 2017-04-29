package ua.sumdu.java.lab2.messenger.handler.processing;

import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.*;
import static ua.sumdu.java.lab2.messenger.handler.processing.AddingRequestParsing.addNewFriend;
import static ua.sumdu.java.lab2.messenger.handler.processing.UpdateRequestParsing.updateGroup;

import java.io.File;
import java.util.Iterator;
import java.util.Objects;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.w3c.dom.Document;
import ua.sumdu.java.lab2.messenger.api.UserMap;
import ua.sumdu.java.lab2.messenger.entities.*;
import ua.sumdu.java.lab2.messenger.handler.api.ResponseParsing;
import ua.sumdu.java.lab2.messenger.parsers.MessageCounterParser;
import ua.sumdu.java.lab2.messenger.parsers.ParsingMessages;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.transferring.impl.DataTransferImpl;

public class ResponseParsingImpl implements ResponseParsing {

    @Override
    public String responseParsing(String str) {
        int responseType = Integer.parseInt(str.substring(0, 4));
        if (responseType > 1999 && responseType < 4000) {
            String context = str.substring(5);
            return infoResponses(responseType, context);
        } else if (responseType > 3999 && responseType < 5000) {
            String context = str.substring(5);
            return responsesUpdates(responseType, context);
        } else if (responseType > 4999 && responseType < 6000) {
            String context = str.substring(5);
            return addResponses(responseType, context);
        } else if (responseType > 6999 && responseType <= 7999) {
            String context = str.substring(5);
            return dataResponse(responseType, context);
        }
        return "";
    }

    private String dataResponse(int responseType, String context) {
        DataTransferImpl dataTransfer = new DataTransferImpl();
        if (responseType == DATA_ACQUISITION.getResponseNumber()) {
            return dataTransfer.parsingDataAcquisitionResponse(context);
        } else if (responseType == DATA_SENDING_REJECTED.getResponseNumber()) {
            return dataTransfer.parsingDataSendingRejectedResponse(context);
        }
        return "";
    }

    private String infoResponses(int responseType, String context) {
        if (REQUEST_HAS_BEEN_DECLINED.getResponseNumber() == responseType) {
            return notificationOfDeclinedRequest(context);
        } else if (USER_IS_OFFLINE.getResponseNumber() == responseType) {
            return notificationThatUserIsOffline(context);
        } else {
            return "";
        }
    }

    private String responsesUpdates(int responseType, String context) {
        if (responseType == UPDATED_GROUP_LIST.getResponseNumber()) {
            updateGroup(context);
        } else if (responseType == REQUESTED_MESSAGES.getResponseNumber()
                || responseType == REQUESTED_GROUP_MESSAGES.getResponseNumber()) {
            Document doc = XmlParser.loadXmlFromString(context);
            MessageMapImpl messageMap = (MessageMapImpl) ParsingMessages
                    .getMessagesFromSpecificDate(doc, 0);
            Iterator<Message> iterator = messageMap.getMapForMails().values().iterator();
            if (iterator.hasNext()) {
                Message mess1 = iterator.next();
                String fileName;
                if (responseType == REQUESTED_MESSAGES.getResponseNumber()) {
                    fileName = mess1.getSender();
                } else {
                    fileName = mess1.getReceiver();
                }
                File fileWithMails = new File(User.getUrlMessageDirectory() + "/" + fileName + ".xml");
                MessageMapImpl currentMessageMap = (MessageMapImpl) XmlParser.INSTANCE.read(fileWithMails);
                currentMessageMap.addMessage(mess1);
                int count = 0;
                while (iterator.hasNext()) {
                    currentMessageMap.addMessage(iterator.next());
                    count++;
                }
                XmlParser.INSTANCE.write(currentMessageMap, fileWithMails);
                MessageCounter messageCounter = MessageCounterParser.PARSER.getMessageCounter();
                if (Objects.isNull(messageCounter)) {
                    messageCounter = new MessageCounter();
                }
                messageCounter.add(fileName, count);
                MessageCounterParser.PARSER.write(messageCounter);
            }
        }
        return "";
    }

    private String addResponses(int responseType, String context) {
        if (ADDED_TO_FRIENDS.getResponseNumber() == responseType) {
            String name = addNewFriend(context);
            notificationOfSuccessfulAdditionToFriends(name);
            return "";
        } else if (responseType == ADDED_TO_GROUP.getResponseNumber()) {
            GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
            GroupMapImpl groupMap = (GroupMapImpl) groupMapParser.jsonStringToGroupMap(context);
            String groupName = groupMap.getMap().keySet().iterator().next();
            User newUser = groupMap.getMap().get(groupName).getMap().values().iterator().next();
            GroupMapImpl currentGroups = (GroupMapImpl) groupMapParser.getGroupMap();
            currentGroups.addUser(groupName, newUser);
            groupMapParser.writeGroupMapToFile(groupMapParser.groupMapToJSonString(currentGroups));
            UserMap userMap = currentGroups.getMap().get(groupName);
            GroupMapImpl updatedGroup = new GroupMapImpl();
            for (User user : userMap.getMap().values()) {
                updatedGroup.addUser(groupName, user);
            }
            return newUser.getUsername() + "=" + groupName;
        } else {
            return "";
        }
    }

    private String notificationThatUserIsOffline(String userIp) {
        Platform.runLater(() ->{
            Notifications notification = Notifications.create()
                    .title("Information")
                    .darkStyle()
                    .graphic(null)
                    .text("User(" + userIp + ") is offline")
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.BOTTOM_RIGHT);
            notification.showError();
        });
        return "";
    }

    private String notificationOfDeclinedRequest(String userName) {
        Platform.runLater(() -> {
            Notifications notification = Notifications.create()
                    .title("Information")
                    .darkStyle()
                    .graphic(null)
                    .text("User " + userName + " declined your request")
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.BOTTOM_RIGHT);
            notification.showConfirm();
        });
        return "";
    }

    private void notificationOfSuccessfulAdditionToFriends(String name) {
        Platform.runLater(() -> {
            Notifications notification = Notifications.create()
                    .title("Information")
                    .darkStyle()
                    .graphic(null)
                    .text("User " + name + " confirmed your request to friends")
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.BOTTOM_RIGHT);
            notification.showConfirm();
        });
    }

}
