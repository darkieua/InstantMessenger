package ua.sumdu.java.lab2.messenger.handler.processing;

import static ua.sumdu.java.lab2.messenger.handler.entities.RequestType.*;
import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.*;

import java.io.File;
import java.util.Objects;
import org.w3c.dom.Document;
import ua.sumdu.java.lab2.messenger.api.UserMap;
import ua.sumdu.java.lab2.messenger.entities.*;
import ua.sumdu.java.lab2.messenger.handler.api.RequestParsing;
import ua.sumdu.java.lab2.messenger.parsers.ParsingMessages;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.processing.UserCreatorImpl;
import ua.sumdu.java.lab2.messenger.processing.UserMapParserImpl;
import ua.sumdu.java.lab2.messenger.transferring.impl.DataTransferImpl;

public class RequestParsingImpl implements RequestParsing {

  private boolean test = false;

  @Override
  public String requestParser(String string) {
    int requestType = Integer.parseInt(string.substring(0,4));
    String context = string.substring(5);
    if (requestType > 999 && requestType <= 2999) {
      return adding(requestType, context);
    } else if ((requestType > 2999 && requestType <= 3999)
            || (requestType > 5999 && requestType <= 6999)) {
      return receivingDataAndMessages(requestType, context);
    } else if (requestType > 3999 && requestType <= 5999) {
      return updateRequestsAndUpdateData(requestType, context);
    } else {
      return String.valueOf(UNIDENTIFIED_REQUEST.getResponseNumber());
    }
  }

  private String dataRequests(int requestType, String context) {
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

  private String adding(int requestType, String context) {
    if (requestType == ADD_TO_FRIENDS.getRequestNumber()) {
      boolean usersReaction;
      if (test) {
        usersReaction = true;
      } else {
        usersReaction = getReaction(context, "user");
      }
      if (usersReaction) {
        addNewFriend(context);
        return String.valueOf(ADDED_TO_FRIENDS.getResponseNumber());
      } else {
        return String.valueOf(REQUEST_HAS_BEEN_DECLINED.getResponseNumber());
      }
    } else if (requestType == ADD_TO_GROUP.getRequestNumber()) {
      boolean usersReaction;
      if (test) {
        usersReaction = true;
      } else {
        usersReaction = getReaction(context, "group");
      }
      if (usersReaction) {
        String groupName = addGroup(context);
        return ADDED_TO_GROUP.getResponseNumber() + "=" + groupName;
      } else {
        return String.valueOf(REQUEST_HAS_BEEN_DECLINED.getResponseNumber());
      }
    } else {
      return String.valueOf(UNIDENTIFIED_REQUEST.getResponseNumber());
    }
  }

  private String receivingDataAndMessages(int requestType, String context) {
    if (requestType > 5999 && requestType <= 6999) {
      return dataRequests(requestType, context);
    } else if (requestType == NEW_MESSAGE.getRequestNumber()) {
      newMessage(context, "user");
      return String.valueOf(SUCCESSFUL.getResponseNumber());
    } else if (requestType == NEW_MESSAGE_TO_GROUP.getRequestNumber()) {
      newMessage(context, "group");
      return String.valueOf(SUCCESSFUL.getResponseNumber());
    } else {
      return String.valueOf(UNIDENTIFIED_REQUEST.getResponseNumber());
    }
  }

  private String updateRequestsAndUpdateData(int requestType, String context) {
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
   * Handling the addition of a new user to friends.
   */

  public String addNewFriend(String str) {
    UserMapParserImpl userMapParser = UserMapParserImpl.getInstance();
    UserMap userMap = userMapParser.getFriends();
    User newUser = UserCreatorImpl.INSTANCE.toUser(str);
    userMap.addUser(newUser);
    userMapParser.writeUserMapToFile(userMapParser.userMapToJSonString(userMap));
    return newUser.getUsername();
  }

  private String addGroup(String str) {
    GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
    GroupMapImpl groupMap = (GroupMapImpl) groupMapParser.jsonStringToGroupMap(str);
    GroupMapImpl currentGroups = (GroupMapImpl) groupMapParser.getGroupMap();
    String key = (String) groupMap.getMap().keySet().toArray()[0];
    UserMapImpl userMap = groupMap.getMap().get(key);
    for (User user: userMap.getMap().values()) {
      currentGroups.addUser(key, user);
    }
    groupMap.addUser(key, User.CURRENT_USER);
    groupMapParser.writeGroupMapToFile(groupMapParser.groupMapToJSonString(currentGroups));
    return key;
  }

  private void newMessage(String str, String type) {
    Document doc = XmlParser.loadXmlFromString(str);
    Message message = ParsingMessages.parseMessage(doc.getFirstChild());
    String fileName;
    UserMapImpl users;
    if ("user".equals(type)) {
      fileName = message.getSender();
      users = (UserMapImpl) UserMapParserImpl.getInstance().getFriends();
    } else {
      fileName = message.getReceiver();
      users = (UserMapImpl) GroupMapParserImpl.getInstance().getUserMap(fileName);
    }
    for (User user: users.getMap().values()) {
      if (Objects.equals(user.getUsername(), fileName)
          && !CategoryUsers.BLACKLIST.name().equals(user.getCategory().name())) {
        File file = new File(User.getUrlMessageDirectory() + "/" + fileName + ".xml");
        MessageMapImpl messageMap = (MessageMapImpl) XmlParser.INSTANCE.read(file);
        messageMap.addMessage(message);
        XmlParser.INSTANCE.write(messageMap, file);
      }
    }
  }

  /**
   * Processing group list updates.
   */

  public void updateGroup(String str) {
    GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
    GroupMapImpl currentGroup = (GroupMapImpl) groupMapParser.jsonStringToGroupMap(str);
    String groupName = (String) currentGroup.getMap().keySet().toArray()[0];
    GroupMapImpl allGroup = (GroupMapImpl) groupMapParser.getGroupMap();
    allGroup.getMap().remove(groupName);
    for (User user : currentGroup.getMap().get(groupName).getMap().values()) {
      allGroup.addUser(groupName, user);
    }
    groupMapParser.writeGroupMapToFile(groupMapParser.groupMapToJSonString(allGroup));
  }

  public void setTest(boolean test) {
    this.test = test;
  }

  public boolean getReaction(String name, String groupOrUser) {
    return false;
  }
}
