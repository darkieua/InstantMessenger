package ua.sumdu.java.lab2.messenger.handler.processing;

import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Iterator;
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
import ua.sumdu.java.lab2.messenger.transferring.impl.DataTransferImpl;

public class ResponseParsingImpl implements ResponseParsing {

  @Override
  public String responseParsing(String str) {
    int responseType = Integer.parseInt(str.substring(0, 4));
    String context = str.substring(5);
    if (responseType > 1999 && responseType < 4000) {
      return infoResponses(responseType, context);
    } else if (responseType > 3999 && responseType < 5000) {
      return responsesUpdates(responseType, context);
    } else if (responseType > 4999 && responseType < 6000) {
      return addResponses(responseType, context);
    } else if (responseType > 6999 && responseType <= 7999) {
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
      RequestParsingImpl requestParsing = new RequestParsingImpl();
      requestParsing.updateGroup(context);
      return "";
    } else if (responseType == REQUESTED_MESSAGES.getResponseNumber()
        || responseType == REQUESTED_GROUP_MESSAGES.getResponseNumber()) {
      Document doc = XmlParser.loadXmlFromString(context);
      MessageMapImpl messageMap = (MessageMapImpl) ParsingMessages
          .getMessagesFromSpecificDate(doc, 0);
      Iterator<Message> iterator = messageMap.getMapForMails().values().iterator();
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
      while (iterator.hasNext()) {
        currentMessageMap.addMessage(iterator.next());
      }
      XmlParser.INSTANCE.write(currentMessageMap, fileWithMails);
      return "";
    }
    return "";
  }

  private String addResponses(int responseType, String context) {
    if (ADDED_TO_FRIENDS.getResponseNumber() == responseType) {
      RequestParsingImpl requestParsing = new RequestParsingImpl();
      String name = requestParsing.addNewFriend(context);
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
      UserMapImpl userMap = currentGroups.getMap().get(groupName);
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
    File system = User.getSystemMessageFile();
    MessageMapImpl messages = (MessageMapImpl) XmlParser.INSTANCE.read(system);
    Message newMessage = new Message("system", User.getCurrentUser().getUsername(),
        "User(" + userIp + ") is offline", LocalDateTime.now());
    messages.addMessage(newMessage);
    XmlParser.INSTANCE.write(messages, system);
    return "";
  }

  private String notificationOfDeclinedRequest(String userName) {
    File system = User.getSystemMessageFile();
    MessageMapImpl messages = (MessageMapImpl) XmlParser.INSTANCE.read(system);
    Message newMessage = new Message("system", User.getCurrentUser().getUsername(),
        "User " + userName + "  declined your request", LocalDateTime.now());
    messages.addMessage(newMessage);
    XmlParser.INSTANCE.write(messages, system);
    return "";
  }

  private String notificationOfSuccessfulAdditionToFriends(String name) {
    File system = User.getSystemMessageFile();
    MessageMapImpl messages = (MessageMapImpl) XmlParser.INSTANCE.read(system);
    Message newMessage = new Message("system", User.getCurrentUser().getUsername(),
        "User " + name + " confirmed your request to friends", LocalDateTime.now());
    messages.addMessage(newMessage);
    XmlParser.INSTANCE.write(messages, system);
    return "";
  }

}
