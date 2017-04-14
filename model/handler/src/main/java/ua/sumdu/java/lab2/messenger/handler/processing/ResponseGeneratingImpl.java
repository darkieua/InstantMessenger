package ua.sumdu.java.lab2.messenger.handler.processing;

import static ua.sumdu.java.lab2.messenger.entities.User.CURRENT_USER;
import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.*;

import java.io.File;
import org.w3c.dom.Document;
import ua.sumdu.java.lab2.messenger.entities.*;
import ua.sumdu.java.lab2.messenger.handler.api.ResponseGenerating;
import ua.sumdu.java.lab2.messenger.parsers.ParsingMessages;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;

public class ResponseGeneratingImpl implements ResponseGenerating {

  @Override
  public String responseGenerate(String string) {
    StringBuilder result = new StringBuilder();
    if (string.length() == 4) {
      return shortResponses(string);
    }
    int responseType = Integer.parseInt(string.substring(0, 4));
    String context = string.substring(5);
    if (responseType > 3999 && responseType < 4999) {
      return updatingRequest(responseType, context);
    } else if (responseType == ADDED_TO_GROUP.getResponseNumber()) {
      result.append(responseType).append('=');
      String groupName = string.substring(5);
      GroupMapImpl thisUser = new GroupMapImpl();
      thisUser.addUser(groupName, CURRENT_USER.setCategory(CategoryUsers.FRIEND));
      result.append(GroupMapParserImpl.getInstance().groupMapToJSonString(thisUser));
    }
    return result.toString();
  }

  private String shortResponses(String string) {
    StringBuilder result = new StringBuilder();
    result.append(string);
    int type = Integer.parseInt(string);
    if (type == REQUEST_HAS_BEEN_DECLINED.getResponseNumber()) {
      result.append('=').append(CURRENT_USER.getUsername()).append('(')
          .append(CURRENT_USER.getIpAddress()).append(')');
    } else if (type == ADDED_TO_FRIENDS.getResponseNumber()) {
      result.append('=').append(CURRENT_USER.setCategory(CategoryUsers.FRIEND).toJSonString());
    }
    return result.toString();
  }

  private String updatingRequest(int responseType, String context) {
    StringBuilder result = new StringBuilder();
    result.append(responseType).append('=');
    if (responseType == UPDATED_GROUP_LIST.getResponseNumber()) {
      String groupName = context;
      GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
      UserMapImpl userMap = (UserMapImpl) groupMapParser.getUserMap(groupName);
      GroupMapImpl currentGroup = new GroupMapImpl();
      for (User user : userMap.getMap().values()) {
        currentGroup.addUser(groupName, user);
      }
      result.append(groupMapParser.groupMapToJSonString(currentGroup));
    } else if (responseType == REQUESTED_MESSAGES.getResponseNumber()) {
      String[] words = context.split("=");
      long date = Long.parseLong(words[0]);
      Document doc = XmlParser.INSTANCE.getDocument(new File(
          User.getUrlMessageDirectory() + "/" + words[1] + ".xml"));
      MessageMapImpl messageMap = (MessageMapImpl) ParsingMessages
          .getMessagesFromSpecificDate(doc, date);
      result.append(XmlParser.INSTANCE.toXml(XmlParser.INSTANCE
          .writeMessageToDocument(messageMap, null)));
    }
    return result.toString();
  }

  public String userIsOffline(String userIp) {
    return USER_IS_OFFLINE.getResponseNumber() + "=" + userIp;
  }

}
