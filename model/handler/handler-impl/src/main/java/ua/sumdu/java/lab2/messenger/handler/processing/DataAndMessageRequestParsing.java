package ua.sumdu.java.lab2.messenger.handler.processing;

import static ua.sumdu.java.lab2.messenger.handler.entities.RequestType.*;
import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.*;

import java.io.File;
import java.util.Objects;
import org.w3c.dom.Document;
import ua.sumdu.java.lab2.messenger.entities.*;
import ua.sumdu.java.lab2.messenger.parsers.MessageCounterParser;
import ua.sumdu.java.lab2.messenger.parsers.ParsingMessages;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.processing.UserMapParserImpl;
import ua.sumdu.java.lab2.messenger.transferring.impl.DataTransferImpl;

public class DataAndMessageRequestParsing {

    private static final String USER = "user";

    static String receivingDataAndMessages(int requestType, String context) {
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

    private static String dataRequests(int requestType, String context) {
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

    private static void newMessage(String str, String type) {
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
                MessageCounter newMessages = MessageCounterParser.PARSER.getMessageCounter();
                if (Objects.isNull(newMessages)) {
                    newMessages = new MessageCounter();
                }
                newMessages.add(fileName, 1);
                MessageCounterParser.PARSER.write(newMessages);
            }
        }
    }
}
