package ua.sumdu.java.lab2.messenger.handler.processing;

import static ua.sumdu.java.lab2.messenger.handler.entities.RequestType.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import ua.sumdu.java.lab2.messenger.entities.*;
import ua.sumdu.java.lab2.messenger.handler.api.RequestGenerating;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.transferring.impl.DataTransferImpl;

public class RequestGeneratingImpl implements RequestGenerating {
    private static final Logger LOG = LoggerFactory.getLogger(RequestGeneratingImpl.class);

    @Override
    public String creatingFriendsRequest() {
        return ADD_TO_FRIENDS.getRequestNumber() + "="
                + User.getCurrentUser().setCategory(CategoryUsers.FRIEND).toJSonString();
    }

    @Override
    public String createJoinRequestToGroup(String groupName) {
        StringBuilder str = new StringBuilder();
        GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
        UserMapImpl groupUsers = (UserMapImpl) groupMapParser.getUserMap(groupName);
        GroupMapImpl newGroup = new GroupMapImpl();
        for (User user: groupUsers.getMap().values()) {
            newGroup.addUser(groupName, user);
        }
        str.append(ADD_TO_GROUP.getRequestNumber()).append('=')
                .append(groupMapParser.groupMapToJSonString(newGroup));
        return str.toString();
    }

    @Override
    public String updateGroupList(String groupName) {
        String string = createJoinRequestToGroup(groupName);
        StringBuilder str = new StringBuilder();
        str.append(UPDATE_GROUP_LIST.getRequestNumber()).append(string.substring(4));
        return str.toString();
    }

    @Override
    public String createRequestForUpdateGroupList(String groupName) {
        StringBuilder str = new StringBuilder();
        str.append(REQUEST_FOR_UPDATE_GROUP_LIST.getRequestNumber())
                .append('=').append(groupName);
        return str.toString();
    }

    @Override
    public String createDataRequest(SentFiles files) {
        StringBuilder result = new StringBuilder();
        DataTransferImpl dataTransfer = new DataTransferImpl();
        result.append(DATA_REQUEST.getRequestNumber())
                .append('=')
                .append(User.getCurrentUser().getUsername())
                .append("==")
                .append(dataTransfer.dataRequest(files));
        return result.toString();
    }

    @Override
    public String creatingDeleteRequestFromFriends() {
        return REMOVING_FROM_FRIENDS.getRequestNumber() + "=" + User.getCurrentUser().getUsername();
    }

    @Override
    public String creatingDeleteRequestFromGroup(String groupName) {
        return USER_LEFT_GROUP.getRequestNumber() + "=" + groupName + "==" + User.getCurrentUser().getUsername();
    }


    /**
     * Converts a message to a xml string.
     */
    public static String createMessage(Message message) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOG.error(e.getMessage(), e);
        }
        DOMImplementation impl = builder.getDOMImplementation();
        Document doc = impl.createDocument(null, null, null);
        XmlParser.INSTANCE.addMessage(null, message, doc);
        return XmlParser.INSTANCE.toXml(doc);
    }
}
