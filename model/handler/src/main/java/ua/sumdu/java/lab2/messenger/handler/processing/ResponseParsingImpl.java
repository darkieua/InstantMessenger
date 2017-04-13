package ua.sumdu.java.lab2.messenger.handler.processing;

import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import ua.sumdu.java.lab2.messenger.entities.GroupMapImpl;
import ua.sumdu.java.lab2.messenger.entities.Message;
import ua.sumdu.java.lab2.messenger.entities.MessageMapImpl;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.entities.UserMapImpl;
import ua.sumdu.java.lab2.messenger.handler.api.ResponseParsing;
import ua.sumdu.java.lab2.messenger.parsers.ParsingMessages;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;

public class ResponseParsingImpl implements ResponseParsing {

  private static final Logger LOG = LoggerFactory.getLogger(RequestGeneratingImpl.class);

  private boolean test;

  @Override
  public void responseParsing(String str) {
    int responseType = Integer.parseInt(str.substring(0, 4));
    String context = str.substring(5);
    if (responseType > 1999 && responseType < 4000) {
      infoResponses(responseType, context);
    } else if (responseType > 3999 && responseType < 5000) {
      responsesUpdates(responseType, context);
    } else if (responseType > 4999 && responseType < 6000) {
      addResponses(responseType, context);
    }
  }

  private void infoResponses(int responseType, String context) {
    if (REQUEST_HAS_BEEN_DECLINED.getResponseNumber() == responseType) {
      notificationOfDeclinedRequest(context);
    } else if (USER_IS_OFFLINE.getResponseNumber() == responseType) {
      notificationThatUserIsOffline(context);
    } else {
      return;
    }
  }

  private void responsesUpdates(int responseType, String context) {
    if (responseType == UPDATED_GROUP_LIST.getResponseNumber()) {
      RequestParsingImpl requestParsing = new RequestParsingImpl();
      requestParsing.updateGroup(context);
    } else if (responseType == REQUESTED_MESSAGES.getResponseNumber()) {
      Document doc = XmlParser.loadXmlFromString(context);
      MessageMapImpl messageMap = (MessageMapImpl) ParsingMessages
          .getMessagesFromSpecificDate(doc, 0);
      Iterator<Message> iterator = messageMap.getMapForMails().values().iterator();
      Message mess1 = iterator.next();
      String sender = mess1.getSender();
      File fileWithMails = new File(User.getUrlMessageDirectory() + "/" + sender + ".xml");
      MessageMapImpl currentMessageMap = (MessageMapImpl) XmlParser.INSTANCE.read(fileWithMails);
      currentMessageMap.addMessage(mess1);
      while (iterator.hasNext()) {
        currentMessageMap.addMessage(iterator.next());
      }
      XmlParser.INSTANCE.write(currentMessageMap, fileWithMails);
    }
  }

  private void addResponses(int responseType, String context) {
    if (ADDED_TO_FRIENDS.getResponseNumber() == responseType) {
      RequestParsingImpl requestParsing = new RequestParsingImpl();
      String name = requestParsing.addNewFriend(context);
      notificationOfSuccessfulAdditionToFriends(name);
    } else if (responseType == ADDED_TO_GROUP.getResponseNumber()) {
      GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
      GroupMapImpl groupMap = (GroupMapImpl) groupMapParser.jsonStringToGroupMap(context);
      String groupName = groupMap.getMap().keySet().iterator().next();
      User newUser = groupMap.getMap().get(groupName).getMap().values().iterator().next();
      GroupMapImpl currentGroups = (GroupMapImpl) groupMapParser.getGroupMap();
      currentGroups.addUser(groupName, newUser);
      groupMapParser.writeGroupMapToFile(groupMapParser.groupMapToJSonString(currentGroups));
      UserMapImpl userMap = currentGroups.getMap().get(groupName);
      GroupMapImpl updatedGroup = new GroupMapImpl();
      for (User user : userMap.getMap().values()) {
        updatedGroup.addUser(groupName, user);
      }
      if (!test) {
        sendOutNewGroupList(newUser.getUsername(), groupName, updatedGroup);
      }
    }
  }

  private void notificationThatUserIsOffline(String userIp) {
    File system = User.getSystemMessageFile();
    MessageMapImpl messages = (MessageMapImpl) XmlParser.INSTANCE.read(system);
    Message newMessage = new Message("system", User.CURRENT_USER.getUsername(),
        "User(" + userIp + ") is offline", LocalDateTime.now());
    messages.addMessage(newMessage);
    XmlParser.INSTANCE.write(messages, system);
  }

  private void notificationOfDeclinedRequest(String userName) {
    File system = User.getSystemMessageFile();
    MessageMapImpl messages = (MessageMapImpl) XmlParser.INSTANCE.read(system);
    Message newMessage = new Message("system", User.CURRENT_USER.getUsername(),
        "User " + userName + "  declined your request", LocalDateTime.now());
    messages.addMessage(newMessage);
    XmlParser.INSTANCE.write(messages, system);
  }

  private void notificationOfSuccessfulAdditionToFriends(String name) {
    File system = User.getSystemMessageFile();
    MessageMapImpl messages = (MessageMapImpl) XmlParser.INSTANCE.read(system);
    Message newMessage = new Message("system", User.CURRENT_USER.getUsername(),
        "User " + name + " confirmed your request to friends", LocalDateTime.now());
    messages.addMessage(newMessage);
    XmlParser.INSTANCE.write(messages, system);
  }

  private void sendOutNewGroupList(String newUserUsername, String groupName, GroupMapImpl groupMap) {

  }

  public void setTest(boolean test) {
    this.test = test;
  }
}
