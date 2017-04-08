package ua.sumdu.java.lab2.instant_messenger.listener.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import ua.sumdu.java.lab2.instant_messenger.config.parser.UserConfigParser;
import ua.sumdu.java.lab2.instant_messenger.entities.*;
import ua.sumdu.java.lab2.instant_messenger.listener.api.RequestGenerating;
import ua.sumdu.java.lab2.instant_messenger.listener.entities.RequestType;


import java.io.IOException;
import java.util.Objects;
import ua.sumdu.java.lab2.instant_messenger.parsers.XMLParser;
import ua.sumdu.java.lab2.instant_messenger.processing.GroupMapParserImpl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class RequestGeneratingImpl implements RequestGenerating {
    private static final Logger LOG = LoggerFactory.getLogger(RequestGeneratingImpl.class);

    @Override
    public String addToFriends() {
        StringBuilder str = new StringBuilder();
        str.append(RequestType.ADD_TO_FRIENDS.getRequestNumber()).append('=')
                .append(UserConfigParser.getCurrentUser()
                        .setCategory(CategoryUsers.FRIEND).toJSonString());
        return str.toString();
    }

    @Override
    public String addToGroup(String groupName) {
        StringBuilder str = new StringBuilder();
        GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
        UserMapImpl groupUsers = (UserMapImpl) groupMapParser.getUserMap(groupName);
        GroupMapImpl newGroup = new GroupMapImpl();
        for (User user: groupUsers.getMap().values()) {
            newGroup.addUser(groupName, user);
        }
        str.append(RequestType.ADD_TO_GROUP.getRequestNumber()).append('=')
                .append(groupMapParser.groupMapToJSonString(newGroup));
        return str.toString();
    }

    @Override
    public String newMessage(Message message) {
        StringBuilder str = new StringBuilder();
        if (Objects.isNull(message.getFileMap())) {
            str.append(RequestType.NEW_MESSAGE.getRequestNumber())
                    .append('=').append(createMessage(message));
            return str.toString();
        } else {
            str.append(RequestType.NEW_MESSAGE_WITH_FILES_TO_FRIEND.getRequestNumber())
                    .append('=').append(createMessage(message));
            return str.toString();
        }
    }

    @Override
    public String newMessageToGroup(Message message) {
        StringBuilder str = new StringBuilder();
        if (Objects.isNull(message.getFileMap())) {
            str.append(RequestType.NEW_MESSAGE_TO_GROUP.getRequestNumber())
                    .append('=').append(createMessage(message));
            return str.toString();
        } else {
            str.append(RequestType.NEW_MESSAGE_WITH_FILES_TO_GROUP.getRequestNumber())
                    .append('=').append(createMessage(message));
            return str.toString();
        }
    }

    @Override
    public String updateGroupList(String groupName) {
        String string = addToGroup(groupName);
        StringBuilder str = new StringBuilder();
        str.append(RequestType.UPDATE_GROUP_LIST).append(string.substring(2));
        return str.toString();
    }

    @Override
    public String requestForUpdateGroupList(String groupName) {
        StringBuilder str = new StringBuilder();
        str.append(RequestType.REQUEST_FOR_UPDATE_GROUP_LIST.getRequestNumber())
                .append('=').append(groupName);
        return str.toString();
    }

    @Override
    public String messagesFromSpecificDate(long date) {
        StringBuilder str = new StringBuilder();
        str.append(RequestType.MESSAGES_FROM_A_SPECIFIC_DATE.getRequestNumber())
                .append('=').append(date);
        return str.toString();
    }

    private String createMessage(Message message) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOG.error(e.getMessage(), e);
        }
        DOMImplementation impl = builder.getDOMImplementation();
        Document doc = impl.createDocument(null, null, null);
        XMLParser.INSTANCE.addMessage(null, message, doc);
        try {
            return XMLParser.INSTANCE.toXML(doc);
        } catch (TransformerException | IOException  e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

}
