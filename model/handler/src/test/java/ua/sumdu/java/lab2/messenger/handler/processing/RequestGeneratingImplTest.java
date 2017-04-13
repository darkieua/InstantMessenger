package ua.sumdu.java.lab2.messenger.handler.processing;

import ua.sumdu.java.lab2.messenger.entities.GroupMapImpl;
import ua.sumdu.java.lab2.messenger.entities.Message;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.entities.UserMapImpl;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.*;
import static ua.sumdu.java.lab2.messenger.handler.entities.RequestType.*;
import static ua.sumdu.java.lab2.messenger.entities.User.CURRENT_USER;

import static org.junit.Assert.assertEquals;

@RunWith(DataProviderRunner.class)
public class RequestGeneratingImplTest {

  private RequestGeneratingImpl requestGenerating;

  @Before
  public void init() {
    requestGenerating = new RequestGeneratingImpl();
  }

  @DataProvider
  public static Object[][] chatName() {
    return new Object[][]{{"main"}};
  }

  @DataProvider
  public static Object[][] messages() throws UnknownHostException {
    User[] users = {new User(FRIEND, "user1", "user1@ex.so", 8080, InetAddress.getLocalHost()),
        new User(ADMIN, "user2", "user2@ex.so", 8080, InetAddress.getLocalHost()),
        new User(VISITOR, "user3", "user3@ex.so", 8080, InetAddress.getLocalHost()),
        new User(FRIEND, "user4", "user4@ex.so", 8080, InetAddress.getLocalHost()),
        new User(FRIEND, "user5", "user5@ex.so", 8080, InetAddress.getLocalHost())};
    return new Object[][] {{new Message(users[0].getUsername(), users[1].getUsername(), "text1", LocalDateTime.now())},
        {new Message(users[1].getUsername(), users[2].getUsername(), "text2", LocalDateTime.now())},
        {new Message(users[2].getUsername(), users[3].getUsername(), "text3", LocalDateTime.now())},
        {new Message(users[3].getUsername(), users[4].getUsername(), "text4", LocalDateTime.now())}};
  }

  @Test
  public void addToFriends() {
    String str = CURRENT_USER.setCategory(FRIEND).toJSonString();
    String correctRequest = ADD_TO_FRIENDS.getRequestNumber() + "=" + str;
    String result = requestGenerating.addToFriends();
    assertEquals(RequestParsingImplTest.getMessage(result, correctRequest), correctRequest, result);
  }

  @UseDataProvider("chatName")
  @Test
  public void addToGroup(String chatName) {
    GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
    GroupMapImpl groups = (GroupMapImpl) groupMapParser.getGroupMap();
    GroupMapImpl desiredGroup = new GroupMapImpl();
    desiredGroup.getMap().put(chatName, groups.getMap().get(chatName));
    String correctRequest = ADD_TO_GROUP.getRequestNumber() + "=" +groupMapParser.groupMapToJSonString(desiredGroup);
    String result = requestGenerating.addToGroup(chatName);
    assertEquals(RequestParsingImplTest.getMessage(result, correctRequest), correctRequest, result);
  }

  @UseDataProvider("messages")
  @Test
  public void newMessage(Message message) {
    String result = requestGenerating.newMessage(message);
    String mess = requestGenerating.createMessage(message);
    String correctRequest = NEW_MESSAGE.getRequestNumber() + "=" + mess;
    assertEquals(RequestParsingImplTest.getMessage(result, correctRequest), correctRequest, result);
  }

  @UseDataProvider("messages")
  @Test
  public void newMessageToGroup(Message message) {
    String chatName = "main";
    message.setReceiver(chatName);
    String result = requestGenerating.newMessageToGroup(message);
    String mess = requestGenerating.createMessage(message);
    String correctRequest = NEW_MESSAGE_TO_GROUP.getRequestNumber() + "=" + mess;
    assertEquals(RequestParsingImplTest.getMessage(result, correctRequest), correctRequest, result);
  }

  @UseDataProvider("chatName")
  @Test
  public void updateGroupList(String chatName) {
    String result = requestGenerating.updateGroupList(chatName);
    UserMapImpl usermap = (UserMapImpl) GroupMapParserImpl.getInstance().getUserMap(chatName);
    GroupMapImpl groupMap = new GroupMapImpl();
    groupMap.getMap().put(chatName, usermap);
    String correctRequest = UPDATE_GROUP_LIST.getRequestNumber() + "=" + GroupMapParserImpl.getInstance().groupMapToJSonString(groupMap);
    assertEquals(RequestParsingImplTest.getMessage(result, correctRequest), correctRequest, result);
  }

  @UseDataProvider("chatName")
  @Test
  public void requestForUpdateGroupList(String chatName) {
    String result = requestGenerating.requestForUpdateGroupList(chatName);
    String correctRequest = REQUEST_FOR_UPDATE_GROUP_LIST.getRequestNumber() + "=" + chatName;
    assertEquals(RequestParsingImplTest.getMessage(result, correctRequest), correctRequest, result);
  }

  @Test
  public void messagesFromSpecificDate() {
    long date = 1;
    String result = requestGenerating.messagesFromSpecificDate(date);
    String correctRequest = MESSAGES_FROM_A_SPECIFIC_DATE.getRequestNumber() + "=" + date + "=" + CURRENT_USER.getUsername();
    assertEquals(RequestParsingImplTest.getMessage(result, correctRequest), correctRequest, result);
  }

}