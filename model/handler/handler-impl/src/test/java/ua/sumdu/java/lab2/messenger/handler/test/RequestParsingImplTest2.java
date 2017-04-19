package ua.sumdu.java.lab2.messenger.handler.test;

import static ua.sumdu.java.lab2.messenger.handler.entities.RequestType.*;
import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.*;

import com.tngtech.java.junit.dataprovider.*;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import org.junit.*;
import org.junit.runner.RunWith;
import ua.sumdu.java.lab2.messenger.entities.*;
import ua.sumdu.java.lab2.messenger.handler.processing.RequestGeneratingImpl;
import ua.sumdu.java.lab2.messenger.handler.processing.RequestParsingImpl;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.processing.UserMapParserImpl;

@RunWith(DataProviderRunner.class)
public class RequestParsingImplTest2 {

  private RequestGeneratingImpl requestGenerating;
  private RequestParsingImpl requestParsing;
  private static final User TEST_USER = new User(CategoryUsers.BLACKLIST, "test_user", "test_user@ex.so",
    8080, User.getCurrentUser().getIpAddress());

  /**
   * The method returns test messages.
   */

  @DataProvider
  public static Object[][] messages() throws UnknownHostException {
    User currentUser = User.getCurrentUser();
    String testUsername = "user1";
    return new Object[][] {{new Message(testUsername, currentUser.getUsername(), "text1",
      LocalDateTime.now()), new Message(testUsername, currentUser.getUsername(), "text2",
      LocalDateTime.now()), new Message(testUsername, currentUser.getUsername(), "text3",
      LocalDateTime.now()), new Message(testUsername, currentUser.getUsername(), "text4",
      LocalDateTime.now())}};
  }

  /**
   * The method initialises variables for testing.
   */

  @Before
  public void init() throws UnknownHostException {
    requestParsing = new RequestParsingImpl();
    requestGenerating = new RequestGeneratingImpl();
    UserMapImpl userMap = (UserMapImpl) UserMapParserImpl.getInstance().getFriends();
    userMap.addUser(TEST_USER);
    UserMapParserImpl.getInstance().writeUserMapToFile(UserMapParserImpl.getInstance()
      .userMapToJSonString(userMap));
  }

  /**
   * The method deletes data for testing.
   */

  @After
  public void after() {
    UserMapImpl userMap = (UserMapImpl) UserMapParserImpl.getInstance().getFriends();
    userMap.removeUser(TEST_USER);
    UserMapParserImpl.getInstance().writeUserMapToFile(UserMapParserImpl.getInstance()
      .userMapToJSonString(userMap));
  }


  @UseDataProvider("messages")
  @Test
  public void newMessageToGroup(Message mess) {
    mess.setReceiver("testGroup");
    String request = requestGenerating.newMessageToGroup(mess);
    String receiver = mess.getReceiver();
    String path = User.getUrlMessageDirectory() + "/" + receiver + ".xml";
    MessageMapImpl messageMap = (MessageMapImpl) XmlParser.INSTANCE.read(new File(path));
    messageMap.addMessage(mess);
    XmlParser.INSTANCE.write(messageMap, new File(path));
    requestParsing.requestParser(request);
    MessageMapImpl newMap = (MessageMapImpl) XmlParser.INSTANCE.read(new File(path));
    Assert.assertEquals(RequestParsingImplTest.getMessage(newMap.toString(),
      messageMap.toString()), newMap, messageMap);
    messageMap.deleteMessage(mess);
    XmlParser.INSTANCE.write(messageMap, new File(path));
  }

  @Test
  public void updateGroupList() throws UnknownHostException {
    User user = new User(CategoryUsers.FRIEND, "tempUser", "tempUser@ex.com", 8020, InetAddress.getLocalHost());
    GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
    String chatName = "main";
    GroupMapImpl allGroups = (GroupMapImpl) groupMapParser.getGroupMap();
    GroupMapImpl groupMap = new GroupMapImpl();
    groupMap.getMap().put(chatName, allGroups.getMap().get(chatName));
    groupMap.addUser(chatName, user);
    allGroups.addUser(chatName, user);
    String request = UPDATE_GROUP_LIST.getRequestNumber() + "="
      + groupMapParser.groupMapToJSonString(groupMap);
    requestParsing.requestParser(request);
    GroupMapImpl newGroups = (GroupMapImpl) groupMapParser.getGroupMap();
    Assert.assertEquals(RequestParsingImplTest.getMessage(newGroups.toString(), allGroups.toString()),
      newGroups, allGroups);
    groupMap.getMap().get(chatName).removeUser(user);
    groupMapParser.writeGroupMapToFile(groupMapParser.groupMapToJSonString(groupMap));
  }

  @Test
  public void requestForUpdateLists() {
    String groupName = "main";
    String request1 = requestGenerating.requestForUpdateGroupList(groupName);
    String response1 = UPDATED_GROUP_LIST.getResponseNumber() + "=" + groupName;
    String newResponse1 = requestParsing.requestParser(request1);
    Assert.assertEquals(RequestParsingImplTest.getMessage(newResponse1, response1), newResponse1,
      response1);
    String request2 = requestGenerating.messagesFromSpecificDate(1);
    String response2 = REQUESTED_MESSAGES.getResponseNumber() + "=" + 1 + "=" + User.getCurrentUser().getUsername();
    String newResponse2 = requestParsing.requestParser(request2);
    Assert.assertEquals(RequestParsingImplTest.getMessage(newResponse2, response2), newResponse2,
      response2);
    String request3 = requestGenerating.groupMessagesFromSpecificDate(1, groupName);
    String response3 = REQUESTED_GROUP_MESSAGES.getResponseNumber() + "=" + 1 + "=" + groupName;
    String newResponse3 = requestParsing.requestParser(request3);
    Assert.assertEquals(RequestParsingImplTest.getMessage(newResponse3, response3), newResponse3,
        response3);
  }

  @Test
  public void newBlackListMessage() throws UnknownHostException {
    User user1 = new User(CategoryUsers.BLACKLIST, "test_user", "user1@ex.so", 8080, InetAddress.getLocalHost());
    UserMapImpl userMap = (UserMapImpl) UserMapParserImpl.getInstance().getFriends();
    userMap.addUser(user1);
    UserMapParserImpl.getInstance().writeUserMapToFile(UserMapParserImpl.getInstance().userMapToJSonString(userMap));
    Message mess = new Message(user1.getUsername(), User.getCurrentUser().getUsername(), "text",
      LocalDateTime.now());
    String request = requestGenerating.newMessage(mess);
    String sender = mess.getSender();
    File messageFile = new File(User.getUrlMessageDirectory() + "/" + sender + ".xml");
    MessageMapImpl messageMap = (MessageMapImpl) XmlParser.INSTANCE.read(messageFile);
    requestParsing.requestParser(request);
    MessageMapImpl newMap = (MessageMapImpl) XmlParser.INSTANCE.read(messageFile);
    Assert.assertEquals(RequestParsingImplTest.getMessage(newMap.toString(),  messageMap.toString()),
      newMap, messageMap);
    messageMap.deleteMessage(mess);
    XmlParser.INSTANCE.write(messageMap, messageFile);
    userMap.removeUser(user1);
    UserMapParserImpl.getInstance().writeUserMapToFile(UserMapParserImpl.getInstance().userMapToJSonString(userMap));
  }
}
