package ua.sumdu.java.lab2.messenger.handler.processing;

import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.FRIEND;
import static ua.sumdu.java.lab2.messenger.entities.User.CURRENT_USER;
import static ua.sumdu.java.lab2.messenger.handler.entities.RequestType.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import ua.sumdu.java.lab2.messenger.entities.GroupMapImpl;
import ua.sumdu.java.lab2.messenger.entities.Message;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.entities.UserMapImpl;
import ua.sumdu.java.lab2.messenger.handler.api.RequestGenerating;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;

public class RequestGeneratingImpl implements RequestGenerating {
  private static final Logger LOG = LoggerFactory.getLogger(RequestGeneratingImpl.class);

  @Override
  public String addToFriends() {
    return ADD_TO_FRIENDS.getRequestNumber() + "="
            + CURRENT_USER.setCategory(FRIEND).toJSonString();
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
    str.append(ADD_TO_GROUP.getRequestNumber()).append('=')
        .append(groupMapParser.groupMapToJSonString(newGroup));
    return str.toString();
  }

  @Override
  public String newMessage(Message message) {
    StringBuilder str = new StringBuilder();
    str.append(NEW_MESSAGE.getRequestNumber())
          .append('=').append(createMessage(message));
    return str.toString();
  }

  @Override
  public String newMessageToGroup(Message message) {
    StringBuilder str = new StringBuilder();
    str.append(NEW_MESSAGE_TO_GROUP.getRequestNumber())
          .append('=').append(createMessage(message));
    return str.toString();
  }

  @Override
  public String updateGroupList(String groupName) {
    String string = addToGroup(groupName);
    StringBuilder str = new StringBuilder();
    str.append(UPDATE_GROUP_LIST.getRequestNumber()).append(string.substring(4));
    return str.toString();
  }

  @Override
  public String requestForUpdateGroupList(String groupName) {
    StringBuilder str = new StringBuilder();
    str.append(REQUEST_FOR_UPDATE_GROUP_LIST.getRequestNumber())
        .append('=').append(groupName);
    return str.toString();
  }

  @Override
  public String messagesFromSpecificDate(long date) {
    StringBuilder str = new StringBuilder();
    str.append(MESSAGES_FROM_A_SPECIFIC_DATE.getRequestNumber())
        .append('=').append(date).append('=').append(CURRENT_USER.getUsername());
    return str.toString();
  }

  /**
   * Converts a message to a xml string.
   */

  public String createMessage(Message message) {
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
