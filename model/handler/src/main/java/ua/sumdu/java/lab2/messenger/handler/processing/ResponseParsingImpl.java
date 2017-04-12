package ua.sumdu.java.lab2.messenger.handler.processing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import ua.sumdu.java.lab2.messenger.entities.GroupMapImpl;
import ua.sumdu.java.lab2.messenger.entities.Message;
import ua.sumdu.java.lab2.messenger.entities.MessageMapImpl;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.entities.UserMapImpl;
import ua.sumdu.java.lab2.messenger.handler.api.ResponseParsing;
import ua.sumdu.java.lab2.messenger.handler.entities.ResponseType;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;

public class ResponseParsingImpl implements ResponseParsing {
  @Override
  public void parse(String str) {
    if (String.valueOf(ResponseType.UNIDENTIFIED_REQUEST.getResponseNumber()).equals(str)) {
      //Problem!
      return;
    } else if (String.valueOf(ResponseType.USER_IS_OFFLINE.getResponseNumber()).equals(str)) {
      notificationThatUserIsOffline();
    } else if (str.length() == 3) {
      return; //Message sent
    }
    int type = Integer.parseInt(str.substring(0,2));
    if (ResponseType.REQUEST_HAS_BEEN_DECLINED.getResponseNumber() == type) {
      notificationOfDeclinedRequest(str.substring(4));//Notify the user of the rejected application
    } else if (ResponseType.ADDED_TO_FRIENDS.getResponseNumber() == type) {
      RequestParsingImpl requestParsing = new RequestParsingImpl();
      requestParsing.addNewFriend(str.substring(4));
    } else if (type == ResponseType.ADDED_TO_GROUP.getResponseNumber()) {
      String smallGroup = str.substring(4);
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
    } else if (type == ResponseType.UPDATED_GROUP_LIST.getResponseNumber()) {
      RequestParsingImpl requestParsing = new RequestParsingImpl();
      requestParsing.updateGroup(str.substring(4));
    } else if (type == ResponseType.REQUESTED_MESSAGES.getResponseNumber()) {
      File temp = null;
      try {
        temp = File.createTempFile("temp", "message");
        FileWriter fileWriter = new FileWriter(temp);
        fileWriter.write(str.substring(4));
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

  void notificationThatUserIsOffline() {

  }

  void notificationOfDeclinedRequest(String userName) {

  }

  void sendOutNewGroupList(GroupMapImpl groupMap) {

  }
}
