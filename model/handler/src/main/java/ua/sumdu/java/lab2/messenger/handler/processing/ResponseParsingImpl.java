package ua.sumdu.java.lab2.messenger.handler.processing;

import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.entities.GroupMapImpl;
import ua.sumdu.java.lab2.messenger.entities.Message;
import ua.sumdu.java.lab2.messenger.entities.MessageMapImpl;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.entities.UserMapImpl;
import ua.sumdu.java.lab2.messenger.handler.api.ResponseParsing;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;

public class ResponseParsingImpl implements ResponseParsing {

  private static final Logger LOG = LoggerFactory.getLogger(RequestGeneratingImpl.class);

  @Override
  public void responseParsing(String str) {
    if (str.length() == 4) {
      shortResponses(str);
    }
    int type = Integer.parseInt(str.substring(0,3));
    if (REQUEST_HAS_BEEN_DECLINED.getResponseNumber() == type) {
      notificationOfDeclinedRequest(str.substring(5));//Notify the user of the rejected application
    } else if (ADDED_TO_FRIENDS.getResponseNumber() == type) {
      RequestParsingImpl requestParsing = new RequestParsingImpl();
      requestParsing.addNewFriend(str.substring(5));
    } else if (type == ADDED_TO_GROUP.getResponseNumber()) {
      String smallGroup = str.substring(5);
      GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
      GroupMapImpl groupMap = (GroupMapImpl) groupMapParser.jsonStringToGroupMap(smallGroup);
      String groupName = (String) groupMap.getMap().keySet().toArray()[0];
      User newUser = (User) groupMap.getMap().get(groupName).getMap().values().toArray()[0];
      GroupMapImpl currentGroups = (GroupMapImpl) groupMapParser.getGroupMap();
      currentGroups.addUser(groupName, newUser);
      groupMapParser.writeGroupMapToFile(groupMapParser.groupMapToJSonString(currentGroups));
      UserMapImpl userMap = currentGroups.getMap().get(groupName);
      GroupMapImpl updatedGroup = new GroupMapImpl();
      for (User user : userMap.getMap().values()) {
        updatedGroup.addUser(groupName, user);
      }
      sendOutNewGroupList(updatedGroup);
    } else if (type == UPDATED_GROUP_LIST.getResponseNumber()) {
      RequestParsingImpl requestParsing = new RequestParsingImpl();
      requestParsing.updateGroup(str.substring(5));
    } else if (type == REQUESTED_MESSAGES.getResponseNumber()) {
      File temp = null;
      try {
        temp = File.createTempFile("temp", "message");
        FileWriter fileWriter = new FileWriter(temp);
        fileWriter.write(str.substring(5));
        fileWriter.flush();
        fileWriter.close();
      } catch (IOException e) {
        //log
      }
      MessageMapImpl messages = (MessageMapImpl) XmlParser.INSTANCE.read(temp);
      String sender = ((Message)messages.getMapForMails().values().toArray()[0]).getSender();
      File fileWithMails = new File(User.getUrlMessageDirectory() + sender);
      MessageMapImpl currentMessageMap = (MessageMapImpl) XmlParser.INSTANCE.read(fileWithMails);
      for (Message message : messages.getMapForMails().values()) {
        currentMessageMap.addMessage(message);
      }
      XmlParser.INSTANCE.write(currentMessageMap, fileWithMails);
    }
  }

  private void shortResponses(String str) {
    if (String.valueOf(UNIDENTIFIED_REQUEST.getResponseNumber()).equals(str)) {
      LOG.warn(UNIDENTIFIED_REQUEST.name());
      return;
    } else if (String.valueOf(USER_IS_OFFLINE.getResponseNumber()).equals(str)) {
      notificationThatUserIsOffline();
    } else if (str.length() == 4) {
      return;
    }

  }

  private void notificationThatUserIsOffline() {
    System.out.println("user is offline");
  }

  private void notificationOfDeclinedRequest(String userName) {

  }

  private void sendOutNewGroupList(GroupMapImpl groupMap) {

  }
}
