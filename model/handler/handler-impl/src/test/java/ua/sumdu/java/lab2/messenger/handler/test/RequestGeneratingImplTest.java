package ua.sumdu.java.lab2.messenger.handler.test;


import static ua.sumdu.java.lab2.messenger.handler.entities.RequestType.*;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ua.sumdu.java.lab2.messenger.entities.*;
import ua.sumdu.java.lab2.messenger.handler.processing.RequestGeneratingImpl;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;

@RunWith(DataProviderRunner.class)
public class RequestGeneratingImplTest {

  private RequestGeneratingImpl requestGenerating;

  @Before
  public void init() {
    requestGenerating = new RequestGeneratingImpl();
  }

  /**
   * Test messages.
   */
  @DataProvider
  public static Object[][] messages() throws UnknownHostException {
    User[] users = {new User(CategoryUsers.FRIEND, "user1", "user1@ex.so", 8080, InetAddress.getLocalHost()),
      new User(CategoryUsers.ADMIN, "user2", "user2@ex.so", 8080, InetAddress.getLocalHost()),
      new User(CategoryUsers.VISITOR, "user3", "user3@ex.so", 8080, InetAddress.getLocalHost()),
      new User(CategoryUsers.FRIEND, "user4", "user4@ex.so", 8080, InetAddress.getLocalHost()),
      new User(CategoryUsers.FRIEND, "user5", "user5@ex.so", 8080, InetAddress.getLocalHost())};
    return new Object[][] {{new Message(users[0].getUsername(), users[1].getUsername(), "text1",
      LocalDateTime.now())}, {new Message(users[1].getUsername(), users[2].getUsername(),
      "text2", LocalDateTime.now())}, {new Message(users[2].getUsername(),
      users[3].getUsername(), "text3", LocalDateTime.now())}, {new Message(users[3]
      .getUsername(), users[4].getUsername(), "text4", LocalDateTime.now())}};
  }

  @Test
  public void addToFriends() {
    String str = User.CURRENT_USER.setCategory(CategoryUsers.FRIEND).toJSonString();
    String correctRequest = ADD_TO_FRIENDS.getRequestNumber() + "=" + str;
    String result = requestGenerating.addToFriends();
    Assert.assertEquals(RequestParsingImplTest.getMessage(result, correctRequest), correctRequest, result);
  }

  @Test
  public void addToGroup() {
    GroupMapImpl groups = (GroupMapImpl) GroupMapParserImpl.getInstance().getGroupMap();
    UserMapImpl userMap = new UserMapImpl();
    userMap.addUser(User.getEmptyUser());
    userMap.addUser(User.CURRENT_USER);
    String chatName = "testGroup";
    groups.getMap().put(chatName, userMap);
    GroupMapParserImpl.getInstance().writeGroupMapToFile(GroupMapParserImpl.getInstance()
      .groupMapToJSonString(groups));
    GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
    GroupMapImpl desiredGroup = new GroupMapImpl();
    desiredGroup.getMap().put(chatName, groups.getMap().get(chatName));
    String correctRequest = ADD_TO_GROUP.getRequestNumber() + "=" +groupMapParser
      .groupMapToJSonString(desiredGroup);
    String result = requestGenerating.addToGroup(chatName);
    Assert.assertEquals(RequestParsingImplTest.getMessage(result, correctRequest), correctRequest, result);
    groups.getMap().remove(chatName);
    GroupMapParserImpl.getInstance().writeGroupMapToFile(GroupMapParserImpl.getInstance()
      .groupMapToJSonString(groups));
  }

  @UseDataProvider("messages")
  @Test
  public void newMessage(Message message) {
    String result = requestGenerating.newMessage(message);
    String mess = requestGenerating.createMessage(message);
    String correctRequest = NEW_MESSAGE.getRequestNumber() + "=" + mess;
    Assert.assertEquals(RequestParsingImplTest.getMessage(result, correctRequest), correctRequest, result);
  }

  @UseDataProvider("messages")
  @Test
  public void newMessageToGroup(Message message) {
    String chatName = "main";
    message.setReceiver(chatName);
    String result = requestGenerating.newMessageToGroup(message);
    String mess = requestGenerating.createMessage(message);
    String correctRequest = NEW_MESSAGE_TO_GROUP.getRequestNumber() + "=" + mess;
    Assert.assertEquals(RequestParsingImplTest.getMessage(result, correctRequest), correctRequest, result);
  }
}